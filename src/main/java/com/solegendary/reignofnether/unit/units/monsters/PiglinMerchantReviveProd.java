package com.solegendary.reignofnether.unit.units.monsters;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.production.ReviveHeroProductionItem;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import net.minecraft.resources.ResourceLocation;

public class PiglinMerchantReviveProd extends ReviveHeroProductionItem {

    public final static String itemName = "Revive Piglin Merchant";

    public PiglinMerchantReviveProd() {
        super(
            new ResourceLocation(ReignOfNether.MOD_ID, "textures/mobheads/piglin_merchant.png"),
            "units.piglins.reignofnether.piglin_merchant.revive",
            EntityRegistrar.PIGLIN_MERCHANT_UNIT.get()
        );
    }

    public String getItemName() {
        return itemName;
    }
}
