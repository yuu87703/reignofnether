package com.solegendary.reignofnether.entities;

import com.solegendary.reignofnether.registrars.EntityRegistrar;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;

public class WindcallerProjectile extends AbstractMagicProjectile {

    public WindcallerProjectile(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, ParticleTypes.CLOUD);
    }

    public WindcallerProjectile(Level pLevel, LivingEntity pShooter, double offsetX, double offsetY, double offsetZ) {
        super(EntityRegistrar.WINDCALLER_PROJECTILE.get(), pShooter, offsetX, offsetY, offsetZ, pLevel, ParticleTypes.CLOUD);
    }
}
