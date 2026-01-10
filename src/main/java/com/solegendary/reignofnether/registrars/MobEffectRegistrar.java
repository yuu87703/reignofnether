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

    public static final RegistryObject<MobEffect> INTENSE_FIRE = MOB_EFFECTS.register("intense_fire", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFC6203)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "06d218a6-4328-4df1-8263-5dfc23f0c65c", -0.20, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<MobEffect> ATTACK_SLOWDOWN = MOB_EFFECTS.register("attack_slowdown", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.ATTACK_SPEED, "95086ec9-c6cc-41b4-a2ce-9b5cf28011e4", -0.05, AttributeModifier.Operation.MULTIPLY_BASE));

    // used to give workers temporary efficiency enchantments
    public static final RegistryObject<MobEffect> TEMPORARY_EFFICIENCY = MOB_EFFECTS.register("temporary_efficiency", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 3402751)
            .addAttributeModifier(Attributes.LUCK, "a417cf34-dc4e-4047-8e14-89eece60c2f8", 0.05, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<MobEffect> BLOODLUST = MOB_EFFECTS.register("bloodlust", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "b9da3d7f-da19-4860-9daa-328be5911517", 0.20, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<MobEffect> FROST_DAMAGE = MOB_EFFECTS.register("frost_damage", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751));

    public static void init(FMLJavaModLoadingContext context) {
        MOB_EFFECTS.register(context.getModEventBus());
    }
}