package com.solegendary.reignofnether.unit.modelling.renderers;


import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.solegendary.reignofnether.unit.goals.GenericTargetedSpellGoal;
import com.solegendary.reignofnether.unit.modelling.models.WildfireModel;
import com.solegendary.reignofnether.unit.units.piglins.WildfireUnit;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

@OnlyIn(Dist.CLIENT)
public class WildfireRenderer extends MobRenderer<WildfireUnit, WildfireModel<WildfireUnit>> {

    public static final float SCALE_MULT = 1.0f;

    private static final ResourceLocation WILDFIRE_LOCATION = ResourceLocation.fromNamespaceAndPath("reignofnether", "textures/entities/wildfire_unit.png");

    public WildfireRenderer(EntityRendererProvider.Context context) {
        super(context, new WildfireModel<>(context.bakeLayer(WildfireModel.LAYER_LOCATION)), 0.5F);
    }

    protected int getBlockLightLevel(WildfireUnit pEntity, BlockPos pPos) {
        return 15;
    }

    public ResourceLocation getTextureLocation(WildfireUnit pEntity) {
        return WILDFIRE_LOCATION;
    }

    private static final Map<ResourceLocation, RenderType> EYES_ALPHA_CACHE = new ConcurrentHashMap<>();

    public static RenderType eyesWithAlpha(ResourceLocation texture) {
        return EYES_ALPHA_CACHE.computeIfAbsent(texture, tex -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEyesShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    .setOverlayState(new RenderStateShard.OverlayStateShard(true))
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .createCompositeState(true);

            return RenderType.create(
                    "ron_eyes_alpha",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    256,
                    false,
                    true,
                    state
            );
        });
    }

    @Override
    public void render(WildfireUnit wildfire, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight) {

        super.render(wildfire, yaw, partialTicks, poseStack, buffer, packedLight);

        GenericTargetedSpellGoal scorchingGazeGoal = wildfire.getCastScorchingGazeGoal();
        LivingEntity target = scorchingGazeGoal.getTargetEntity();
        if (scorchingGazeGoal.isCasting() && target != null) {
            float alpha = Math.min(1.0f, scorchingGazeGoal.getChannelTicks() / 20f);
            renderFireBeam(
                    wildfire,
                    target,
                    partialTicks,
                    poseStack,
                    buffer,
                    alpha
            );
        }
    }

    private static final ResourceLocation FIRE_BEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("reignofnether", "textures/misc/wildfire_laser.png");

    public static void renderFireBeam(
            LivingEntity source,
            Entity target,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            float alpha
    ) {
        float time = source.tickCount + partialTick;

        Vec3 sourceEye = source.getEyePosition(partialTick);
        Vec3 targetEye = target.getEyePosition(partialTick);

        Vec3 dir = targetEye.subtract(sourceEye).subtract(0,2.0f,0);

        float dx = (float) dir.x;
        float dy = (float) dir.y;
        float dz = (float) dir.z;

        float dist = Mth.sqrt(dx * dx + dy * dy + dz * dz);

        poseStack.pushPose();
        poseStack.translate(0.0D, source.getEyeHeight() + 2.0f, 0.0D);
        float yaw = (float)Math.atan2(dx, dz);
        float pitch = (float)Math.atan2(dy, Math.sqrt(dx * dx + dz * dz));

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees((-pitch * Mth.RAD_TO_DEG) + 90));

        VertexConsumer vertexConsumer =
                buffer.getBuffer(eyesWithAlpha(FIRE_BEAM_TEXTURE));

        float scroll = time * 0.02F;
        float vStart = -1.0F + scroll;
        float vEnd = dist * 0.5F + vStart;

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        float pulse = MiscUtil.getOscillatingFloat(0, 1.0f);
        pulse = (float) Math.pow(pulse, 0.4f);
        // Deep red
        float r1 = 0.6F;
        float g1 = 0.1F;
        float b1 = 0.0F;
        // Yellow-hot
        float r2 = 1.0F;
        float g2 = 0.9F;
        float b2 = 0.1F;
        float r = r1 + (r2 - r1) * pulse;
        float g = g1 + (g2 - g1) * pulse;
        float b = b1 + (b2 - b1) * pulse;

        float radius = 0.2F;
        int sides = 4;

        for (int i = 0; i < sides; ++i) {
            float angle1 = (float)(Math.PI * 2 * i / sides);
            float angle2 = (float)(Math.PI * 2 * (i + 1) / sides);

            float x1 = Mth.cos(angle1) * radius;
            float z1 = Mth.sin(angle1) * radius;
            float x2 = Mth.cos(angle2) * radius;
            float z2 = Mth.sin(angle2) * radius;

            vertexConsumer.vertex(matrix, x1, 0.0F, z1)
                    .color(r, g, b, alpha)
                    .uv(0.0F, vEnd)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.pack(15, 15))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            vertexConsumer.vertex(matrix, x1, dist, z1)
                    .color(r, g, b, alpha)
                    .uv(0.0F, vStart)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.pack(15, 15))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            vertexConsumer.vertex(matrix, x2, dist, z2)
                    .color(r, g, b, alpha)
                    .uv(1.0F, vStart)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.pack(15, 15))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            vertexConsumer.vertex(matrix, x2, 0.0F, z2)
                    .color(r, g, b, alpha)
                    .uv(1.0F, vEnd)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.pack(15, 15))
                    .normal(normal, 0, 1, 0)
                    .endVertex();
        }
        poseStack.popPose();
    }
}
