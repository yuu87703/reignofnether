package com.solegendary.reignofnether.resources;

import com.solegendary.reignofnether.registrars.BlockRegistrar;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
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
    private static int getWraithSnowLayers(BlockState bs) {
        return isWraithSnow(bs) ? bs.getValue(BlockStateProperties.LAYERS) : 0;
    }
    private static boolean canPlaceSnow(Level level, BlockPos pos) {
        return !MiscUtil.isSolidBlocking(level, pos) && MiscUtil.isSolidBlocking(level, pos.below());
    }

    private static BlockPos pickWeighted(Map<BlockPos, Integer> weights) {
        int totalWeight = 0;

        for (int w : weights.values()) {
            if (w > 0) {
                totalWeight += w;
            }
        }
        if (totalWeight <= 0) {
            return null;
        }
        int roll = RANDOM.nextInt(totalWeight);

        for (Map.Entry<BlockPos, Integer> entry : weights.entrySet()) {
            int w = entry.getValue();
            if (w <= 0) continue;

            roll -= w;
            if (roll < 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static HashMap<BlockPos, Integer> getPosesAndWeights(ServerLevel level, BlockPos pos, int centrePosLayers) {
        int baseWeight = 4;
        List<BlockPos> adjPoses = List.of(
                pos.north(), pos.south(), pos.east(), pos.west(),
                pos.north().north(), pos.south().south(), pos.east().east(), pos.west().west(),
                pos.north().east(), pos.south().east(), pos.north().west(), pos.south().west()
        );
        HashMap<BlockPos, Integer> posesAndLayers = new HashMap<>();
        for (BlockPos adjPos : adjPoses) {

            List<BlockPos> verticalPosesShort = List.of(adjPos, adjPos.below(), adjPos.above());
            List<BlockPos> verticalPosesLong = List.of(adjPos, adjPos.below(), adjPos.above(), adjPos.below().below(), adjPos.above().above());
            int horizDistance = adjPos.atY(0).distManhattan(new Vec3i(adjPos.getX(), 0, adjPos.getZ()));
            for (BlockPos verticalPos : horizDistance <= 1 ? verticalPosesShort : verticalPosesLong) {
                if (canPlaceSnow(level, verticalPos)) {
                    int layers = getWraithSnowLayers(level.getBlockState(verticalPos));
                    int layerDiffWeight = (centrePosLayers - layers) * 3;
                    int distanceWeight = - horizDistance * 6;
                    int weight = baseWeight + layerDiffWeight + distanceWeight;
                    if (layers == 0 && layerDiffWeight > 0) {
                        weight += baseWeight * 2;
                    }
                    posesAndLayers.put(adjPos, Math.max(0, weight));
                    break;
                }
            }
        }
        // extra chance for centre pos:
        if (centrePosLayers < 8)
            posesAndLayers.put(pos, baseWeight * 2);
        return posesAndLayers;
    }

    // places wraith snow at pos - if there is already snow there, then first check if it is the highest nearby snow
    // and if so, place it at a random adjacent pos instead
    public static void placeWraithSnow(ServerLevel level, BlockPos pos) {
        BlockState bsExisting = level.getBlockState(pos);

        if (!canPlaceSnow(level, pos))
            return;

        int layers = getWraithSnowLayers(bsExisting);

        BlockPos targetPos = pos;

        if (layers > 1) {
            HashMap<BlockPos, Integer> posesAndWeights = getPosesAndWeights(level, pos, layers);
            targetPos = pickWeighted(posesAndWeights);
        }

        if (targetPos != null ) {
            BlockState targetBs = level.getBlockState(targetPos);
            int targetLayers = getWraithSnowLayers(targetBs);

            if (targetLayers <= 0) {
                level.setBlockAndUpdate(targetPos, WRAITH_SNOW_BS);
            } else if (targetLayers < 8) {
                BlockState newLayer = targetBs.setValue(BlockStateProperties.LAYERS, targetLayers + 1);
                level.setBlockAndUpdate(targetPos, newLayer);
            }
        }

    }
}
