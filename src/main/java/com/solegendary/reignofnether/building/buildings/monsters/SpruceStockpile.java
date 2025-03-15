package com.solegendary.reignofnether.building.buildings.monsters;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.api.ReignOfNetherRegistries;
import com.solegendary.reignofnether.building.BuildingBlock;
import com.solegendary.reignofnether.building.BuildingBlockData;
import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.building.Buildings;
import com.solegendary.reignofnether.building.buildings.shared.AbstractStockpile;
import com.solegendary.reignofnether.building.buildings.villagers.TownCentre;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.resources.ResourceName;
import com.solegendary.reignofnether.util.Faction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.reignofnether.building.BuildingUtils.getAbsoluteBlockData;

public class SpruceStockpile extends AbstractStockpile {

    public final static String buildingName = "Dark Stockpile";
    public final static String structureName = "stockpile_spruce";
    public final static ResourceCost cost = ResourceCosts.STOCKPILE;
    public SpruceStockpile() {
        super(structureName);
        this.startingBlockTypes.add(Blocks.SPRUCE_LOG);
    }

    public Faction getFaction() {return Faction.MONSTERS;}

    public AbilityButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = ReignOfNetherRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new AbilityButton(
                name,
                new ResourceLocation(ReignOfNether.MOD_ID, "textures/icons/blocks/chest.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.SPRUCE_STOCKPILE,
                () -> false,
                () -> BuildingClientEvents.hasFinishedBuilding(Buildings.TOWN_CENTRE) ||
                        BuildingClientEvents.hasFinishedBuilding(Buildings.MAUSOLEUM) ||
                        ResearchClient.hasCheat("modifythephasevariance"),
                () -> BuildingClientEvents.setBuildingToPlace(Buildings.SPRUCE_STOCKPILE),
                null,
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.reignofnether.spruce_stockpile"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.monsters.reignofnether.spruce_stockpile.tooltip1"), Style.EMPTY)
                ),
                null
        );
    }
}
