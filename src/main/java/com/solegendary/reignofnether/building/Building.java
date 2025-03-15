package com.solegendary.reignofnether.building;

import com.mojang.math.Vector3d;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.ability.Ability;
import com.solegendary.reignofnether.attackwarnings.AttackWarningClientboundPacket;
import com.solegendary.reignofnether.building.buildings.monsters.DarkWatchtower;
import com.solegendary.reignofnether.building.buildings.neutral.Beacon;
import com.solegendary.reignofnether.building.buildings.piglins.Bastion;
import com.solegendary.reignofnether.building.buildings.piglins.FlameSanctuary;
import com.solegendary.reignofnether.building.buildings.piglins.Fortress;
import com.solegendary.reignofnether.building.buildings.piglins.Portal;
import com.solegendary.reignofnether.building.buildings.shared.AbstractBridge;
import com.solegendary.reignofnether.building.buildings.shared.AbstractStockpile;
import com.solegendary.reignofnether.building.buildings.villagers.Watchtower;
import com.solegendary.reignofnether.fogofwar.*;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.player.PlayerServerEvents;
import com.solegendary.reignofnether.registrars.BlockRegistrar;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import com.solegendary.reignofnether.research.ResearchServerEvents;
import com.solegendary.reignofnether.research.researchItems.ResearchAdvancedPortals;
import com.solegendary.reignofnether.research.researchItems.ResearchSilverfish;
import com.solegendary.reignofnether.resources.*;
import com.solegendary.reignofnether.sandbox.SandboxServer;
import com.solegendary.reignofnether.survival.SurvivalServerEvents;
import com.solegendary.reignofnether.tutorial.TutorialClientEvents;
import com.solegendary.reignofnether.tutorial.TutorialServerEvents;
import com.solegendary.reignofnether.unit.UnitAction;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.unit.goals.BuildRepairGoal;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import com.solegendary.reignofnether.unit.interfaces.WorkerUnit;
import com.solegendary.reignofnether.unit.units.monsters.SilverfishUnit;
import com.solegendary.reignofnether.util.Faction;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.util.*;

import static com.solegendary.reignofnether.building.BuildingUtils.getMaxCorner;
import static com.solegendary.reignofnether.building.BuildingUtils.getMinCorner;
import static com.solegendary.reignofnether.player.PlayerServerEvents.isRTSPlayer;
import static com.solegendary.reignofnether.player.PlayerServerEvents.sendMessageToAllPlayers;

public abstract class Building {

    public boolean isExploredClientside = false; // show on minimap
    public boolean isDestroyedServerside = false;
    public boolean isBuiltServerside = false;

    private final static int BASE_MS_PER_BUILD = 500; // time taken to build each block with 1 villager assigned;
    // normally 500ms in real games
    public final float MELEE_DAMAGE_MULTIPLIER = 0.20f; // damage multiplier applied to melee attackers

    public String name;
    public static String structureName;
    public ResourceLocation icon;

    public final boolean isCapitol;

    public boolean isBuilt; // set true when blocksPercent reaches 100% the first time; the building can then be used
    public int msToNextBuild = BASE_MS_PER_BUILD; // 5ms per tick

    // building collapses at a certain % blocks remaining so players don't have to destroy every single block
    public final float MIN_BLOCKS_PERCENT = 0.5f;
    // chance for a mini explosion to destroy extra blocks if a player is breaking it
    // should be higher for large fragile buildings so players don't take ages to destroy it
    protected float explodeChance = 0.3f;
    protected float explodeRadius = 2.0f;
    protected float fireThreshold = 0.75f; // if building has less %hp than this, explosions caused can make fires
    protected float buildTimeModifier = 1.0f; // only affects non-built buildings, not repair times
    protected float repairTimeModifier = 1.25f; // only affects built buildings
    protected int highestBlockCountReached = 2; // effective max health of the building

    public String ownerName;
    public Block portraitBlock; // block rendered in the portrait GUI to represent this building
    public boolean canAcceptResources = false; // can workers drop off resources here?
    public int serverBlocksPlaced = 1;
    private int totalBlocksEverBroken = 0;

    private long ticksToExtinguish = 0;
    private final long TICKS_TO_EXTINGUISH = 100;

    private final long TICKS_TO_SPAWN_ANIMALS_MAX = 1800; // how often we attempt to spawn animals around each
    private long ticksToSpawnAnimals = 0; // spawn once soon after placement
    private final int MAX_ANIMALS = 8;
    private final int ANIMAL_SPAWN_BLOCK_RANGE = 70; // block range to check and spawn animals in
    private final int ANIMAL_SPAWN_RANGE_MIN = 15; // block range to check and spawn animals in
    private final int ANIMAL_SPAWN_RANGE_MAX = 80; // block range to check and spawn animals in

    public int foodCost;
    public int woodCost;
    public int oreCost;
    public int popSupply; // max population this building provides

    public boolean selfBuilding = false; // if set to true, will build itself quickly without workers (but not repair)

    // blocks types that are placed automatically when the building is placed
    // used to control size of initial foundations while keeping it symmetrical
    public final ArrayList<Block> startingBlockTypes = new ArrayList<>();

    protected final ArrayList<AbilityButton> abilityButtons = new ArrayList<>();
    protected final ArrayList<Ability> abilities = new ArrayList<>();

    public ArrayList<AbilityButton> getAbilityButtons() {
        return abilityButtons;
    }

    public ArrayList<Ability> getAbilities() {
        return abilities;
    }

    public Building(
        String ownerName,
        boolean isCapitol
    ) {
        this.ownerName = ownerName;
        this.isCapitol = isCapitol;
    }

    public float getMagicDamageMult() { return 1.0f; }
    public float getMeleeDamageMult() {
        return MELEE_DAMAGE_MULTIPLIER;
    }
}