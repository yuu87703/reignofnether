package com.solegendary.reignofnether.entities;

import com.solegendary.reignofnether.ability.heroAbilities.wildfire.MoltenBomb;
import com.solegendary.reignofnether.blocks.BlockServerEvents;
import com.solegendary.reignofnether.registrars.BlockRegistrar;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import com.solegendary.reignofnether.unit.units.piglins.WildfireUnit;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

    protected void detonate() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2, false, Level.ExplosionInteraction.NONE);

        HashMap<BlockPos, Double> bpAndDists = new HashMap<>();

        if (!(getOwner() instanceof WildfireUnit))
            return;

        MoltenBomb moltenBomb = ((WildfireUnit) getOwner()).getMoltenBomb();
        int radius = (int) moltenBomb.radius;

        for (int x = -radius; x < radius; x++)
            for (int y = -radius; y < radius; y++)
                for (int z = -radius; z < radius; z++) {
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
                        random.nextInt(MoltenBomb.MIN_MAGMA_DURATION, MoltenBomb.MAX_MAGMA_DURATION),
                        true,
                        random.nextBoolean() && !isSolidBlocking(level(), bp.above()) ? Blocks.FIRE.defaultBlockState() : null
                );
            }
        }
        List<Mob> mobs = MiscUtil.getEntitiesWithinRange(position(), moltenBomb.radius, Mob.class, level());
        for (Mob mob : mobs) {
            mob.hurt(damageSources().explosion(this.getOwner(), this), moltenBomb.damage);
        }
        discard();
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }
}
