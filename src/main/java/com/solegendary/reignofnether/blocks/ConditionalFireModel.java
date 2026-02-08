package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.time.TimeClientEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.List;

public class ConditionalFireModel implements BakedModel {
    private final BakedModel normal;
    private final BakedModel soul;

    public ConditionalFireModel(BakedModel normal, BakedModel soul) {
        this.normal = normal;
        this.soul = soul;
    }

    private BakedModel active() {
        return TimeClientEvents.isSoulsAflameActive() ? soul : normal;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return active().getQuads(blockState, direction, randomSource);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                    RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        return active().getQuads(state, side, rand, data, renderType);
    }

    // Everything else delegates too (particle, transforms, AO, etc.)
    @Override public boolean useAmbientOcclusion() { return active().useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return active().isGui3d(); }
    @Override public boolean usesBlockLight() { return active().usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return active().isCustomRenderer(); }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleIcon(ModelData data) {
        return active().getParticleIcon(data);
    }
    @Override public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() { return active().getOverrides(); }
}