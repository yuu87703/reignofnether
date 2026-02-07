package com.solegendary.reignofnether.entities;

import com.solegendary.reignofnether.ability.heroAbilities.wildfire.MoltenBomb;
import com.solegendary.reignofnether.blocks.BlockServerEvents;
import com.solegendary.reignofnether.registrars.BlockRegistrar;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import com.solegendary.reignofnether.unit.units.piglins.WildfireUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.solegendary.reignofnether.util.MiscUtil.isSolidBlocking;

public class MoltenBombProjectile extends Fireball {

    private int maxTicks = 200;

    public MoltenBombProjectile(EntityType<? extends Fireball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MoltenBombProjectile(Level pLevel, LivingEntity pShooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityRegistrar.MOLTEN_BOMB_PROJECTILE.get(), pShooter, offsetX, offsetY, offsetZ, pLevel);
    }

    public void setMaxTicks(int ticks) {
        this.maxTicks = ticks;
    }

    @Override
    public void tick() {
        setDeltaMovement(getDeltaMovement().normalize().scale(0.5f));
        super.tick();
        if (tickCount > maxTicks)
            this.detonate();
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);

        if (level().isClientSide())
            return;

        detonate();
    }

    protected void detonate() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2, false, Level.ExplosionInteraction.NONE);

        HashMap<BlockPos, Double> bpAndDists = new HashMap<>();

        if (!(getOwner() instanceof WildfireUnit))
            return;

        MoltenBomb moltenBomb = ((WildfireUnit) getOwner()).getMoltenBomb();

        for (int x = -4; x < 4; x++)
            for (int y = -4; y < 4; y++)
                for (int z = -4; z < 4; z++) {
                    BlockPos bp = blockPosition().offset(x, y, z);
                    double distSqr = bp.distSqr(blockPosition());
                    double radiusSqr = moltenBomb.radius * moltenBomb.radius;
                    if (distSqr <= radiusSqr && level().getBlockState(bp).isSolid())
                        bpAndDists.put(bp, distSqr / radiusSqr);
                }

        for (BlockPos bp : bpAndDists.keySet()) {
            if (random.nextFloat() > bpAndDists.get(bp)) {
                BlockServerEvents.addTempBlock(
                        (ServerLevel) level(),
                        bp,
                        BlockRegistrar.WALKABLE_MAGMA_BLOCK.get().defaultBlockState(),
                        level().getBlockState(bp),
                        random.nextInt(MoltenBomb.MIN_MAGMA_DURATION, MoltenBomb.MAX_MAGMA_DURATION)
                );
                // TODO: fire immediately disappears, maybe due to the temp block being added?
                if (random.nextBoolean() && !isSolidBlocking(level(), bp.above())) {
                    level().setBlockAndUpdate(bp, Blocks.FIRE.defaultBlockState());
                }
            }
        }
        discard();
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }
}
