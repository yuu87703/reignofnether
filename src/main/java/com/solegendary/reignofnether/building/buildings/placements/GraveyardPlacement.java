package com.solegendary.reignofnether.building.buildings.placements;

import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.building.buildings.piglins.PortalCivilian;
import com.solegendary.reignofnether.building.buildings.piglins.PortalMilitary;
import com.solegendary.reignofnether.building.buildings.piglins.PortalTransport;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import com.solegendary.reignofnether.resources.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.reignofnether.building.BuildingUtils.getAbsoluteBlockData;

public class GraveyardPlacement extends ProductionPlacement {

    public boolean autoRelease = true;

    public GraveyardPlacement(Building building, Level level, BlockPos originPos, Rotation rotation, String ownerName, ArrayList<BuildingBlock> blocks, boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    public List<EntityType<? extends Mob>> getUnitsFromSkulls() {
        ArrayList<EntityType<? extends Mob>> mobs = new ArrayList<>();
        for (int x = originPos.getX() + 1; x < maxCorner.getX(); x++) {
            for (int z = originPos.getZ() + 1; z < maxCorner.getZ(); z++) {
                BlockPos bp = new BlockPos(x,originPos.getY() + 1, z);
                Block block = level.getBlockState(bp).getBlock();
                if (block == Blocks.ZOMBIE_HEAD)
                    mobs.add(EntityRegistrar.ZOMBIE_UNIT.get());
                else if (block == Blocks.SKELETON_SKULL)
                    mobs.add(EntityRegistrar.SKELETON_UNIT.get());
            }
        }
        return mobs;
    }

    public void checkAndReleaseUnits() {

    }

    @Override
    public boolean canDestroyBlock(BlockPos relativeBp) {
        BlockPos worldBp = relativeBp.offset(this.originPos);
        Block block = this.getLevel().getBlockState(worldBp).getBlock();
        return !(block instanceof SkullBlock);
    }
}
