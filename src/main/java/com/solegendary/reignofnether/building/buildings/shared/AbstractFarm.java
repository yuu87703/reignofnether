package com.solegendary.reignofnether.building.buildings.shared;

import com.solegendary.reignofnether.building.Building;
import com.solegendary.reignofnether.building.BuildingBlock;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.buildings.placements.BridgePlacement;
import com.solegendary.reignofnether.building.buildings.placements.FarmPlacement;
import com.solegendary.reignofnether.resources.ResourceCost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.solegendary.reignofnether.building.BuildingUtils.getAbsoluteBlockData;

public abstract class AbstractFarm extends Building {
    public AbstractFarm(String structureName, ResourceCost cost, boolean isCapitol) {
        super(structureName, cost, isCapitol);
    }

    @Override
    public BuildingPlacement createBuildingPlacement(Level level, BlockPos pos, Rotation rotation, String ownerName) {
        return new FarmPlacement(this, level, pos, rotation, ownerName, getCulledBlocks(getAbsoluteBlockData(getRelativeBlockData(level), level, pos, rotation), level), isCapitol);
    }
}
