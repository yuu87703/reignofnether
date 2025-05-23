package com.solegendary.reignofnether.unit.units.monsters;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.production.ReviveHeroProductionItem;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import net.minecraft.resources.ResourceLocation;

public class RoyalGuardReviveProd extends ReviveHeroProductionItem {

    public final static String itemName = "Revive Royal Guard";

    public RoyalGuardReviveProd() {
        super(
            new ResourceLocation(ReignOfNether.MOD_ID, "textures/mobheads/royal_guard.png"),
            "units.villagers.reignofnether.royal_guard.revive",
            EntityRegistrar.ROYAL_GUARD_UNIT.get()
        );
    }

    public String getItemName() {
        return itemName;
    }
}
