package com.solegendary.reignofnether.building.buildings.placements;

import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.building.custombuilding.CustomBuilding;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CustomBuildingPlacement extends BuildingPlacement implements NightSource, NetherConvertingBuilding {

    public NetherZone netherConversionZone = null;

    public CustomBuildingPlacement(CustomBuilding customBuilding, Level level, BlockPos originPos, Rotation rotation, String ownerName, ArrayList<BuildingBlock> blocks, boolean isCapitol) {
        super(customBuilding, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    public CustomBuilding getCustomBuilding() {
        return (CustomBuilding) this.getBuilding();
    }

    @Override public double getMaxRange() { return this.getCustomBuilding().netherRadius; }
    @Override public double getStartingRange() { return 0; }

    @Nullable
    @Override
    public NetherZone getZone() {
        if (this.getCustomBuilding().netherRadius > 0)
            return netherConversionZone;
        return null;
    }

    @Override
    public void setNetherZone(NetherZone nz, boolean save) {
        if (netherConversionZone == null) {
            netherConversionZone = nz;
            if (!level.isClientSide()) {
                BuildingServerEvents.netherZones.add(netherConversionZone);
                if (save)
                    BuildingServerEvents.saveNetherZones((ServerLevel) level);
            }
        }
    }

    @Override
    public int getNightRange() {
        return this.getCustomBuilding().nightRadius;
    }
}
