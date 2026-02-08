package com.solegendary.reignofnether.unit.modelling.renderers;

import com.solegendary.reignofnether.time.TimeClientEvents;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeUnitRenderer extends BlazeRenderer {

    private static final ResourceLocation BLAZE_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/blaze.png");
    private static final ResourceLocation SOULFIRE_BLAZE_LOCATION = ResourceLocation.fromNamespaceAndPath("reignofnether", "textures/entities/soulfire_blaze_unit.png");

    public BlazeUnitRenderer(EntityRendererProvider.Context p_173933_) {
        super(p_173933_);
    }

    public ResourceLocation getTextureLocation(Blaze pEntity) {
        if (TimeClientEvents.isSoulsAflameActive())
            return SOULFIRE_BLAZE_LOCATION;
        else
            return BLAZE_LOCATION;
    }
}
