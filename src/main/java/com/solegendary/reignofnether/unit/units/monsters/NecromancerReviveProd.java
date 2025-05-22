package com.solegendary.reignofnether.unit.units.monsters;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.BuildingServerboundPacket;
import com.solegendary.reignofnether.building.buildings.placements.ProductionPlacement;
import com.solegendary.reignofnether.building.production.ProductionItem;
import com.solegendary.reignofnether.building.production.ProductionItems;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.reignofnether.util.MiscUtil.fcs;

public class NecromancerReviveProd extends ProductionItem {

    public final static String itemName = "Revive Necromancer";
    public final static ResourceCost cost = ResourceCosts.NECROMANCER;//HeroUnit.getReviveCost();

    public NecromancerReviveProd() {
        super(cost);
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide())
                placement.produceUnit((ServerLevel) level, EntityRegistrar.NECROMANCER_UNIT.get(), placement.ownerName, true);
        };
    }

    public String getItemName() {
        return NecromancerReviveProd.itemName;
    }

    public Button getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new Button(
            NecromancerReviveProd.itemName,
            14,
            new ResourceLocation(ReignOfNether.MOD_ID, "textures/mobheads/necromancer.png"),
            hotkey,
            () -> false,
            () -> ProductionItems.NECROMANCER_REVIVE.itemIsBeingProduced(prodBuilding.ownerName) ||
                    ResearchClient.hasResearch(ProductionItems.NECROMANCER_REVIVE),
            () -> true,
            () -> BuildingServerboundPacket.startProduction(prodBuilding.originPos, this),
            null,
            List.of(
                fcs(I18n.get("units.monsters.reignofnether.necromancer.revive")),
                ResourceCosts.getFormattedCost(cost),
                ResourceCosts.getFormattedPopAndTime(cost)
            )
        );
    }

    public Button getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new Button(
            NecromancerReviveProd.itemName,
            14,
            new ResourceLocation(ReignOfNether.MOD_ID, "textures/mobheads/necromancer.png"),
            (Keybinding) null,
            () -> false,
            () -> false,
            () -> true,
            () -> BuildingServerboundPacket.cancelProduction(prodBuilding.originPos, this, first),
            null,
            null
        );
    }
}
