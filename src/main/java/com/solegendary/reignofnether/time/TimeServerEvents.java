package com.solegendary.reignofnether.time;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.Building;
import com.solegendary.reignofnether.building.BuildingServerEvents;
import com.solegendary.reignofnether.building.BuildingUtils;
import com.solegendary.reignofnether.player.PlayerServerEvents;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static com.solegendary.reignofnether.player.PlayerServerEvents.sendMessageToAllPlayers;

public class TimeServerEvents {

    private static Random RANDOM = new Random();

    private static int bloodMoonTicksLeft = 0;
    private static String bloodMoonOwner = "";
    private static int BLOOD_MOON_MAX_RANGE = 20;
    private static int BLOOD_MOON_MIN_RANGE = 5;

    public static void startBloodMoon(int tickDuration, String ownerName) {
        bloodMoonTicksLeft = tickDuration;
        bloodMoonOwner = ownerName;
    }

    // TODO: spawns one neutral undead unit (zombie or skeleton at random) in each base NOT owned by bloodMoonOwner
    private static void doRandomBloodMoonSpawn(Level level) {
        if (level.isClientSide()) {
            return;
        }
        int retries = 0;
        final int MAX_RETRIES = 2;

        int spawnAttempts = 0;
        BlockState spawnBs;
        BlockPos spawnBp;
        Random random = new Random();

        List<String> enemyPlayerNames = PlayerServerEvents.rtsPlayers.stream()
                .map(rtsPlayer -> rtsPlayer.name)
                .filter(name -> !name.equals(bloodMoonOwner))
                .toList();

        ArrayList<Building> enemyBuildings = new ArrayList<>(BuildingServerEvents.getBuildings().stream()
                .filter(b -> !b.ownerName.equals(bloodMoonOwner))
                .toList());
        Collections.shuffle(enemyBuildings);

        // one random building per enemyPlayerName
        ArrayList<Building> singleEnemyBuildings = new ArrayList<>();
        for (Building building : enemyBuildings) {
            if (!singleEnemyBuildings.stream().map(b -> b.ownerName).toList().contains(building.ownerName))
                singleEnemyBuildings.add(building);
        }

        for (Building building : singleEnemyBuildings) {
            do {
                int x = building.centrePos.getX() + random.nextInt(-BLOOD_MOON_MAX_RANGE / 2, BLOOD_MOON_MAX_RANGE / 2);
                int z = building.centrePos.getZ() + random.nextInt(-BLOOD_MOON_MAX_RANGE / 2, BLOOD_MOON_MAX_RANGE / 2);
                int y = level.getChunkAt(new BlockPos(x, 0, z)).getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                BlockState bs;
                do {
                    bs = level.getBlockState(new BlockPos(x, y, z));
                    if (!bs.isSolid() && bs.getFluidState().isEmpty() && y > 0) {
                        y -= 1;
                    } else {
                        break;
                    }
                } while (true);
                spawnBp = new BlockPos(x, y, z);
                spawnBs = level.getBlockState(spawnBp);
                spawnAttempts += 1;
                if (spawnAttempts > 20) {
                    if (retries < MAX_RETRIES) {
                        spawnAttempts = 0;
                        retries += 1;
                        BLOOD_MOON_MAX_RANGE -= (BLOOD_MOON_MAX_RANGE * 0.35f);
                    } else {
                        ReignOfNether.LOGGER.warn("Gave up trying to find a suitable blood moon spawn!");
                        return;
                    }
                }
            } while (!spawnBs.isSolid()
                    || spawnBs.is(BlockTags.LEAVES)
                    || spawnBs.getBlock() == Blocks.BARRIER
                    || spawnBs.is(BlockTags.LOGS) || spawnBs.is(BlockTags.PLANKS)
                    || spawnBp.distSqr(building.centrePos) < BLOOD_MOON_MAX_RANGE * BLOOD_MOON_MAX_RANGE
                    || spawnBp.distSqr(building.centrePos) > BLOOD_MOON_MAX_RANGE * BLOOD_MOON_MAX_RANGE
                    || BuildingUtils.isPosInsideAnyBuilding(level.isClientSide(), spawnBp)
                    || BuildingUtils.isPosInsideAnyBuilding(level.isClientSide(), spawnBp.above()));

            EntityType<? extends Mob> mobType = RANDOM.nextBoolean() ? EntityRegistrar.ZOMBIE_UNIT.get() : EntityRegistrar.SKELETON_UNIT.get();
            UnitServerEvents.spawnMobs(mobType, (ServerLevel) level, spawnBp.above(), 1, "");
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END || evt.level.isClientSide() || evt.level.dimension() != Level.OVERWORLD)
            return;

        if (bloodMoonTicksLeft > 0) {
            bloodMoonTicksLeft -= 1;
            if (bloodMoonTicksLeft <= 0) {
                sendMessageToAllPlayers("abilities.reignofnether.blood_moon.end");
            } else if (bloodMoonTicksLeft % 120 == 0) {
                doRandomBloodMoonSpawn(evt.level);
            }
        }
    }
}














