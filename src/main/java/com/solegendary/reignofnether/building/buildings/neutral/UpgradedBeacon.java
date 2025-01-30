package com.solegendary.reignofnether.building.buildings.neutral;

import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.tutorial.TutorialClientEvents;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

public class UpgradedBeacon extends Beacon {

    public final static String buildingName = "Upgraded Beacon";
    public final static String structureName = "beacon_t5";
    public final static ResourceCost cost = ResourceCost.Building(0,0,0,0);

    public UpgradedBeacon(Level level, BlockPos originPos, Rotation rotation, String ownerName) {
        super(level, originPos, rotation, ownerName);

        this.foodCost = cost.food;
        this.woodCost = cost.wood;
        this.oreCost = cost.ore;
        this.popSupply = cost.population;

        this.capturable = true;
        this.invulnerable = true;
        this.shouldDestroyOnReset = false;
    }

    public static AbilityButton getBuildButton(Keybinding hotkey) {
        return new AbilityButton(
                buildingName,
                new ResourceLocation("minecraft", "textures/item/nether_star.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Beacon.class,
                () -> TutorialClientEvents.isEnabled() || !beaconsAllowed(),
                () -> BuildingClientEvents.getBuildings().stream().filter(b -> b instanceof Beacon).toList().isEmpty(),
                () -> BuildingClientEvents.setBuildingToPlace(Beacon.class),
                null,
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.reignofnether.upgraded_beacon"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.reignofnether.upgraded_beacon.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.reignofnether.upgraded_beacon.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.reignofnether.upgraded_beacon.tooltip3"), Style.EMPTY),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.neutral.reignofnether.beacon.tooltip3"), Style.EMPTY)
                ),
                null
        );
    }
}











