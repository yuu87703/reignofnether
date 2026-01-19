package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.registrars.BlockEntityRegistrar;
import com.solegendary.reignofnether.resources.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Random;

public class WraithSnowBlockEntity extends BlockEntity {

    private static final Random RANDOM = new Random();

    private int ownerId = -1;
    private int ticksToNextMelt = 200;

    public WraithSnowBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistrar.WRAITH_SNOW_BLOCK_ENTITY.get(), pos, state);
        randomiseLifeTicks();
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
        setChanged();
    }

    // melt snow faster the more layers there are
    public void randomiseLifeTicks() {
        int layers = this.getBlockState().getValue(BlockStateProperties.LAYERS);
        this.ticksToNextMelt = RANDOM.nextInt(200 - (20 * layers),300 - (20 * layers));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WraithSnowBlockEntity be) {
        if (level.isClientSide) return;

        be.ticksToNextMelt -= 1;
        if (be.ticksToNextMelt <= 0) {
            be.melt(level, pos, state);
        }
    }


    private void melt(Level level, BlockPos pos, BlockState state) {
        int layers = BlockUtils.getWraithSnowLayers(level.getBlockState(pos));
        if (layers <= 1) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        } else {
            BlockState newLayers = state.setValue(BlockStateProperties.LAYERS, layers - 1);
            level.setBlockAndUpdate(pos, newLayers);
            randomiseLifeTicks();
        }
    }

    private void forceChunk(ServerLevel level) {
        ChunkPos chunkPos = new ChunkPos(worldPosition);
        level.getChunkSource().addRegionTicket(
                TicketType.FORCED,
                chunkPos,
                1,
                chunkPos
        );
    }

    private void unforceChunk(ServerLevel level) {
        ChunkPos chunkPos = new ChunkPos(worldPosition);
        level.getChunkSource().removeRegionTicket(
                TicketType.FORCED,
                chunkPos,
                1,
                chunkPos
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            forceChunk(serverLevel);
        }
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            unforceChunk(serverLevel);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ownerId", ownerId);
        tag.putInt("ticksToNextMelt", ticksToNextMelt);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ownerId = tag.getInt("ownerId");
        ticksToNextMelt = tag.getInt("ticksToNextMelt");
    }
}