package com.solegendary.reignofnether.unit.goals;

import com.solegendary.reignofnether.ability.abilities.Possess;
import com.solegendary.reignofnether.unit.UnitAnimationAction;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import com.solegendary.reignofnether.unit.units.monsters.WraithUnit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;
import java.util.function.Consumer;

public class PossessSpellGoal extends GenericTargetedSpellGoal {

    WraithUnit wraithUnit;

    public PossessSpellGoal(WraithUnit wraith, int channelTicks, int range, UnitAnimationAction animAction,
                   Consumer<LivingEntity> onEntityCast) {
        super(wraith, channelTicks, range, animAction, onEntityCast, null, null);
        this.wraithUnit = wraith;
        this.bonusChannelingRange = 8;
        this.setOnStartChanneling((castPos) -> {
            // TODO start ambient sound
            if (targetEntity instanceof Unit unit)
                this.channelTicksMax = Possess.BASE_CHANNEL_TICKS + (unit.getCost().population * Possess.CHANNEL_TICKS_PER_POP_COST);
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (isCasting && targetEntity != null) {
            if (targetEntity instanceof Unit unit && unit.getOwnerName().equals(wraithUnit.getOwnerName()))
                Unit.fullResetBehaviours(wraithUnit);
            doPossessParticles();
        }
    }

    @Override
    public void stop() {
        super.stop();
        // TODO stop ambient sound
    }

    public void doPossessParticles() {
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        if (this.targetEntity == null) return;
        if (targetEntity.tickCount % 2 != 0) return;

        double dx = targetEntity.getX() - mob.getX();
        double dy = (targetEntity.getY() + targetEntity.getBbHeight() * 0.5) - (mob.getY() + mob.getBbHeight() * 0.5);
        double dz = targetEntity.getZ() - mob.getZ();
        double distSqr = dx * dx + dy * dy + dz * dz;
        double dist = Math.sqrt(distSqr);
        double distSqrt = Math.sqrt(dist);
        if (dist == 0) return;

        double speed = distSqrt * 0.225;
        double vx = (dx / dist) * speed;
        double vy = (dy / dist) * speed;
        double vz = (dz / dist) * speed;

        double originX = mob.getX();
        double originY = mob.getY() + mob.getBbHeight() * 0.75;
        double originZ = mob.getZ();

        RandomSource rand = mob.getRandom();
        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                originX + rand.nextGaussian() * 0.2,
                originY + rand.nextGaussian() * 0.2,
                originZ + rand.nextGaussian() * 0.2,
                0,
                vx, vy, vz,
                speed
        );
    }
}
