package com.solegendary.reignofnether.unit.units.monsters;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.production.ReviveHeroProductionItem;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import net.minecraft.resources.ResourceLocation;

public class NecromancerReviveProd extends ReviveHeroProductionItem {

    public final static String itemName = "Revive Necromancer";

    public NecromancerReviveProd() {
        super(
            new ResourceLocation(ReignOfNether.MOD_ID, "textures/mobheads/necromancer.png"),
            "units.monsters.reignofnether.necromancer.revive",
            EntityRegistrar.NECROMANCER_UNIT.get()
        );
    }

    public String getItemName() {
        return itemName;
    }
}
