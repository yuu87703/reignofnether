package com.solegendary.reignofnether.registrars;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class MobEffectRegistrar {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ReignOfNether.MOD_ID);

    // prevents any actions or movement from happening
    public static final RegistryObject<MobEffect> STUN = MOB_EFFECTS.register("stun",  () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));

    // similar to STUN but also prevents the mob from being knocked back
    public static final RegistryObject<MobEffect> FREEZE = MOB_EFFECTS.register("freeze",  () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x000000));

    // prevents players from issuing any new commands
    // usually used in conjunction with a force-attack command for a taunt effect, or a move command for a fear effect
    public static final RegistryObject<MobEffect> UNCONTROLLABLE = MOB_EFFECTS.register("uncontrollable", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFF0000));

    public static final RegistryObject<MobEffect> ZOMBIE_INFECTED = MOB_EFFECTS.register("zombie_infected", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x000000));

    public static final RegistryObject<MobEffect> MINOR_MOVEMENT_SPEED = MOB_EFFECTS.register("minor_speed", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 3402751)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "e6b9720b-131d-4c17-b029-ab8161e8da97", 0.05, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<MobEffect> MINOR_MOVEMENT_SLOWDOWN = MOB_EFFECTS.register("minor_slowdown", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "eb256076-43e6-470e-a907-434a389da860", -0.05, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<MobEffect> DAMAGE_TAKEN_INCREASE = MOB_EFFECTS.register("damage_taken_increase", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.LUCK, "e0772108-0408-4fa3-ad55-f90f5595d610", -0.05, AttributeModifier.Operation.ADDITION));

    public static double getDamageTakenIncrease(Mob mob) {
        MobEffectInstance mei = mob.getEffect(DAMAGE_TAKEN_INCREASE.get());
        double value = mei == null ? 0 : (mei.getAmplifier() + 1) * 0.05d;
        return Math.round(value / 0.05d) * 0.05d;
    }

    public static final RegistryObject<MobEffect> ATTACK_SLOWDOWN = MOB_EFFECTS.register("attack_slowdown", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.LUCK, "95086ec9-c6cc-41b4-a2ce-9b5cf28011e4", -0.05, AttributeModifier.Operation.MULTIPLY_BASE));

    public static float getPercentAttackSlowdown(Mob mob) {
        MobEffectInstance mei = mob.getEffect(ATTACK_SLOWDOWN.get());
        return mei == null ? 0 : (mei.getAmplifier() + 1) * 0.05f;
    }

    public static void init(FMLJavaModLoadingContext context) {
        MOB_EFFECTS.register(context.getModEventBus());
    }
}