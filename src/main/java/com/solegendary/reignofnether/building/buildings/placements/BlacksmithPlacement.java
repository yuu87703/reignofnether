package com.solegendary.reignofnether.building.buildings.placements;

import com.solegendary.reignofnether.ability.EquipAbility;
import com.solegendary.reignofnether.building.Building;
import com.solegendary.reignofnether.building.BuildingBlock;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class BlacksmithPlacement extends ProductionPlacement {
    public EquipAbility autoCastEquip = null;
    public BlacksmithPlacement(Building building, Level level, BlockPos originPos, Rotation rotation, String ownerName, ArrayList<BuildingBlock> blocks, boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    @Override
    public String getUpgradedName() {
        return I18n.get("buildings.villagers.reignofnether.blacksmith.superior");
    }

    @Override
    public void tick(Level tickLevel) {
        super.tick(tickLevel);

        if (tickAgeAfterBuilt > 0 && tickAgeAfterBuilt % 15 == 0 && isBuilt && autoCastEquip != null
                && autoCastEquip.isOffCooldown(this)) {

            List<Mob> mobs = new ArrayList<>();
            for (Mob e : MiscUtil.getEntitiesWithinRange(new Vector3d(
                    this.centrePos.getX(),
                    this.centrePos.getY(),
                    this.centrePos.getZ()
                ),
                    autoCastEquip.range - 1,
                Mob.class,
                tickLevel
            )) {
                if ((autoCastEquip.isCorrectUnit(e) && autoCastEquip.canAfford(this)
                    && !autoCastEquip.hasItemInSlot(e)
                )) {
                    mobs.add(e);
                }
            }
            if (!mobs.isEmpty()) {
                autoCastEquip.use(tickLevel, this, mobs.get(0));
            }
        }
    }
}
