package com.solegendary.reignofnether.resources;

import com.solegendary.reignofnether.registrars.BlockRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.*;
import java.util.stream.Collectors;

public class BlockUtils {

    private static final Random RANDOM = new Random();

    public static boolean isLogBlock(BlockState bs) {
        return List.of(Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.JUNGLE_LOG, Blocks.MANGROVE_LOG, Blocks.SPRUCE_LOG,
                        Blocks.OAK_WOOD, Blocks.BIRCH_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.JUNGLE_WOOD, Blocks.MANGROVE_WOOD, Blocks.SPRUCE_WOOD,
                        Blocks.CRIMSON_STEM, Blocks.WARPED_STEM, Blocks.MUSHROOM_STEM, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.CRIMSON_HYPHAE, Blocks.WARPED_HYPHAE)
                .contains(bs.getBlock()) || bs.getTags().collect(Collectors.toSet()).contains(BlockTags.LOGS);
    }
    public static boolean isFallingLogBlock(BlockState bs) {
        return List.of(
                        BlockRegistrar.FALLING_OAK_LOG.get(),
                        BlockRegistrar.FALLING_BIRCH_LOG.get(),
                        BlockRegistrar.FALLING_ACACIA_LOG.get(),
                        BlockRegistrar.FALLING_DARK_OAK_LOG.get(),
                        BlockRegistrar.FALLING_JUNGLE_LOG.get(),
                        BlockRegistrar.FALLING_MANGROVE_LOG.get(),
                        BlockRegistrar.FALLING_SPRUCE_LOG.get(),
                        BlockRegistrar.FALLING_CRIMSON_STEM.get(),
                        BlockRegistrar.FALLING_WARPED_STEM.get())
                .contains(bs.getBlock());
    }

    public static BlockState getNonFallingLog(BlockState bs) {
        BlockState nonFallingLogBlock = bs;
        if (bs.getBlock() == BlockRegistrar.FALLING_OAK_LOG.get()) nonFallingLogBlock = Blocks.OAK_LOG.defaultBlockState();
        else if (bs.getBlock() == BlockRegistrar.FALLING_BIRCH_LOG.get()) nonFallingLogBlock = Blocks.BIRCH_LOG.defaultBlockState();
        else if (bs.getBlock() == BlockRegistrar.FALLING_ACACIA_LOG.get()) nonFallingLogBlock = Blocks.ACACIA_LOG.defaultBlockState();
        else if (bs.getBlock() == BlockRegistrar.FALLING_DARK_OAK_LOG.get()) nonFallingLogBlock = Blocks.DARK_OAK_LOG.defaultBlockState();
        else if (bs.getBlock() == BlockRegistrar.FALLING_JUNGLE_LOG.get()) nonFallingLogBlock = Blocks.JUNGLE_LOG.defaultBlockState();
        else if (bs.getBlock() == BlockRegistrar.FALLING_MANGROVE_LOG.get()) nonFallingLogBlock = Blocks.MANGROVE_LOG.defaultBlockState();
        else if (bs.getBlock() == BlockRegistrar.FALLING_SPRUCE_LOG.get()) nonFallingLogBlock = Blocks.SPRUCE_LOG.defaultBlockState();
        else if (bs.getBlock() == BlockRegistrar.FALLING_CRIMSON_STEM.get()) nonFallingLogBlock = Blocks.CRIMSON_STEM.defaultBlockState();
        else if (bs.getBlock() == BlockRegistrar.FALLING_WARPED_STEM.get()) nonFallingLogBlock = Blocks.WARPED_STEM.defaultBlockState();
        return nonFallingLogBlock;
    }

    public static boolean isLeafBlock(BlockState bs) {
        if (bs.is(BlockTags.LEAVES))
            return true;
        return List.of(Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.MANGROVE_LEAVES, Blocks.SPRUCE_LEAVES,
                        BlockRegistrar.DECAYABLE_NETHER_WART_BLOCK.get(), BlockRegistrar.DECAYABLE_WARPED_WART_BLOCK.get(),
                        Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK)
                .contains(bs.getBlock());
    }

    public static boolean isBottomSlab(BlockState bs) {
        if (bs.getBlock() instanceof SlabBlock) {
            SlabType type = bs.getValue(SlabBlock.TYPE);
            return type == SlabType.BOTTOM;
        }
        return false;
    }


    public static final BlockState WRAITH_SNOW_BS = BlockRegistrar.WRAITH_SNOW_LAYER.get().defaultBlockState();

    private static boolean isWraithSnow(BlockState bs) {
        return bs.getBlock() == BlockRegistrar.WRAITH_SNOW_LAYER.get();
    }
    private static boolean isAirOrSnow(BlockState bs) {
        return bs.getBlock() == Blocks.SNOW || bs.isAir();
    }
    private static int getWraithSnowLayers(BlockState bs) {
        return bs.getBlock() == BlockRegistrar.WRAITH_SNOW_LAYER.get() ? bs.getValue(BlockStateProperties.LAYERS) : 0;
    }

    private static HashMap<BlockPos, Integer> getAdjPoses(ServerLevel level, BlockPos pos) {
        ArrayList<BlockPos> poses = new ArrayList<>(List.of(pos.north(), pos.south(), pos.east(), pos.west()));
        HashMap<BlockPos, Integer> posesAndLayers = new HashMap<>();
        for (BlockPos pose : poses) {
            BlockState bs = level.getBlockState(pose);
            BlockState bsAbove = level.getBlockState(pose.above());
            BlockState bsBelow = level.getBlockState(pose.below());
            BlockState bsBelow2 = level.getBlockState(pose.below().below());
            if ((isAirOrSnow(bs) || isWraithSnow(bs)) &&
                !bsBelow.isAir()) {
                posesAndLayers.put(pose, getWraithSnowLayers(bs));
            } else if (!bsBelow2.isAir()) {
                posesAndLayers.put(pose.below(), getWraithSnowLayers(bsBelow));
            } else if (bsAbove.isAir() || isWraithSnow(bsAbove)) {
                posesAndLayers.put(pose.above(), getWraithSnowLayers(bsAbove));
            }
        }
        return posesAndLayers;
    }

    // places wraith snow at pos - if there is already snow there, then first check if it is the highest nearby snow
    // and if so, place it at a random adjacent pos instead
    // TODO: spread out to 2 taxicab distance
    public static void placeWraithSnow(ServerLevel level, BlockPos pos) {
        BlockState bsExisting = level.getBlockState(pos);

        int layers = getWraithSnowLayers(bsExisting);

        int lowestAdjLayer = 8;
        BlockPos targetPos = pos;
        HashMap<BlockPos, Integer> posesAndLayers = getAdjPoses(level, pos);
        for (BlockPos adjPos : posesAndLayers.keySet()) {
            int adjLayers = posesAndLayers.get(adjPos);
            if (adjLayers < lowestAdjLayer) {
                lowestAdjLayer = adjLayers;
                targetPos = adjPos;
            } else if (adjLayers == lowestAdjLayer && RANDOM.nextBoolean()) {
                targetPos = adjPos;
            }
        }
        if (layers <= lowestAdjLayer && layers < 8)
            targetPos = pos;

        BlockState targetBs = level.getBlockState(targetPos);
        int targetLayers = getWraithSnowLayers(targetBs);

        if (targetLayers <= 0) {
            level.setBlockAndUpdate(pos, WRAITH_SNOW_BS);
        } else if (targetLayers < 8) {
            targetBs.setValue(BlockStateProperties.LAYERS, layers + 1);
        }
    }
}
