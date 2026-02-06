package com.solegendary.reignofnether.unit.modelling.renderers;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.solegendary.reignofnether.unit.modelling.models.WildfireModel;
import com.solegendary.reignofnether.unit.units.piglins.WildfireUnit;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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

    @Override
    public void render(WildfireUnit mob, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight) {

        super.render(mob, yaw, partialTicks, poseStack, buffer, packedLight);

        Entity target = mob.getTarget();
        if (target != null) {
            renderFireBeam(
                    mob,
                    target,
                    partialTicks,
                    poseStack,
                    buffer
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
            MultiBufferSource buffer
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

        // Move to eye position in model space
        poseStack.translate(0.0D, source.getEyeHeight() + 2.0f, 0.0D);

        // Now rotate toward target
        float yaw = (float)Math.atan2(dx, dz);
        float pitch = (float)Math.atan2(dy, Math.sqrt(dx * dx + dz * dz));

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees((-pitch * Mth.RAD_TO_DEG) + 90));

        VertexConsumer vertexConsumer =
                buffer.getBuffer(RenderType.eyes(FIRE_BEAM_TEXTURE));

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
        float a = 1.0F;

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
                    .color(r, g, b, a)
                    .uv(0.0F, vEnd)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.pack(15, 15))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            vertexConsumer.vertex(matrix, x1, dist, z1)
                    .color(r, g, b, a)
                    .uv(0.0F, vStart)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.pack(15, 15))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            vertexConsumer.vertex(matrix, x2, dist, z2)
                    .color(r, g, b, a)
                    .uv(1.0F, vStart)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.pack(15, 15))
                    .normal(normal, 0, 1, 0)
                    .endVertex();

            vertexConsumer.vertex(matrix, x2, 0.0F, z2)
                    .color(r, g, b, a)
                    .uv(1.0F, vEnd)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.pack(15, 15))
                    .normal(normal, 0, 1, 0)
                    .endVertex();
        }
        poseStack.popPose();
    }
}
