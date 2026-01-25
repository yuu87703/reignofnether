package com.solegendary.reignofnether.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;

public class BigEnchantParticleProvider
        implements ParticleProvider<SimpleParticleType> {

    @Override
    public Particle createParticle(
            SimpleParticleType type,
            ClientLevel level,
            double x, double y, double z,
            double xd, double yd, double zd
    ) {
        return new BigEnchantParticle(level, x, y, z, xd, yd, zd);
    }
}