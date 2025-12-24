package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.registrars.MobEffectRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class WraithSnowLayerBlock extends SnowLayerBlock {

    private static final int MOVEMENT_SLOWDOWN_AMP_PER_LAYER = 2;
    private static final int ARMOR_REDUCTION_AMP_PER_LAYER = 2;
    private static final int ATTACK_SLOWDOWN_AMP_PER_LAYER = 2;

    public WraithSnowLayerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LAYER[0];
    }

    @Override
    public void entityInside(@NotNull BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        int movementSlowdownAmp = (pState.getValue(LAYERS) * MOVEMENT_SLOWDOWN_AMP_PER_LAYER) - 1;
        int armorReductionAmp = (pState.getValue(LAYERS) * ARMOR_REDUCTION_AMP_PER_LAYER) - 1;
        int attackSlowdownAmp = (pState.getValue(LAYERS) * ATTACK_SLOWDOWN_AMP_PER_LAYER) - 1;
        if (pEntity instanceof LivingEntity livingEntity && pEntity.tickCount % 5 == 0) {
            MobEffectInstance existingMovementSlowdown = livingEntity.getEffect(MobEffectRegistrar.MINOR_MOVEMENT_SLOWDOWN.get());
            if (existingMovementSlowdown == null || existingMovementSlowdown.getAmplifier() < movementSlowdownAmp) {
                livingEntity.addEffect(new MobEffectInstance(MobEffectRegistrar.MINOR_MOVEMENT_SLOWDOWN.get(), 10, movementSlowdownAmp, true, false));
            }
            MobEffectInstance existingArmorReduction = livingEntity.getEffect(MobEffectRegistrar.DAMAGE_TAKEN_INCREASE.get());
            if (existingArmorReduction == null || existingArmorReduction.getAmplifier() < armorReductionAmp) {
                livingEntity.addEffect(new MobEffectInstance(MobEffectRegistrar.DAMAGE_TAKEN_INCREASE.get(), 10, armorReductionAmp, true, false));
            }
            MobEffectInstance existingAttackSlowdown = livingEntity.getEffect(MobEffectRegistrar.MINOR_MOVEMENT_SLOWDOWN.get());
            if (existingAttackSlowdown == null || existingAttackSlowdown.getAmplifier() < attackSlowdownAmp) {
                livingEntity.addEffect(new MobEffectInstance(MobEffectRegistrar.MINOR_MOVEMENT_SLOWDOWN.get(), 10, attackSlowdownAmp, true, false));
            }
        }
    }
}