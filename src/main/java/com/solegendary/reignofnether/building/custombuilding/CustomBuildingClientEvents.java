package com.solegendary.reignofnether.building.custombuilding;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.blocks.RTSStructureBlockEntity;
import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.util.MyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class CustomBuildingClientEvents {

    // since every custom building has a different structure, we need to maintain a list of them here
    public static final ArrayList<CustomBuilding> customBuildings = new ArrayList<>();

    public static final ArrayList<BlockPos> rtsStructuresToRenderBB = new ArrayList<>();

    private static final Minecraft MC = Minecraft.getInstance();

    public static Building getCustomBuilding(String name) {
        for (CustomBuilding customBuilding : customBuildings)
            if (customBuilding.name.equals(name))
                return customBuilding;
        return null;
    }

    public static void registerCustomBuilding(String name, Vec3i structureSize, CompoundTag structureNbt) {
        for (CustomBuilding customBuilding : customBuildings) {
            if (customBuilding.name.equals(name)) {
                MC.player.sendSystemMessage(Component.literal("ERROR: custom building " + name + " already exists"));
            }
        }
        ArrayList<BuildingBlock> blocks = BuildingBlockData.getBuildingBlocksFromNbt(structureNbt);
        Block portraitBlock = Blocks.COMMAND_BLOCK;
        for (BuildingBlock bb : blocks) {
            BlockState bs = bb.getBlockState();
            if (!bs.isAir() && bs.getFluidState().isEmpty()) {
                portraitBlock = bs.getBlock();
            }
        }
        CustomBuilding building = new CustomBuilding(name, structureSize, portraitBlock, structureNbt);
        customBuildings.add(building);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent evt) throws NoSuchFieldException {
        if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || MC.level == null) {
            return;
        }

        for (BlockPos bp : rtsStructuresToRenderBB) {
            if (MC.level.getBlockEntity(bp) instanceof RTSStructureBlockEntity be) {
                BlockPos pos = be.getStructurePos();
                Vec3i size = be.getStructureSize();
                MyRenderer.drawLineBox(evt.getPoseStack(),
                        new AABB(bp.getX() + pos.getX(),
                                bp.getY() + pos.getY(),
                                bp.getZ() + pos.getZ(),
                                bp.getX() + pos.getX() + size.getX(),
                                bp.getY() + pos.getY() + size.getY(),
                                bp.getZ() + pos.getZ() + size.getZ()),
                        1f, 1f, 1f, 1f);
            }
        }
    }


}