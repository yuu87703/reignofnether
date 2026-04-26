package com.solegendary.reignofnether.ability.abilities;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.ability.Ability;
import com.solegendary.reignofnether.ability.BuildingAbilityClientboundPacket;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.buildings.monsters.Laboratory;
import com.solegendary.reignofnether.building.buildings.placements.GraveyardPlacement;
import com.solegendary.reignofnether.cursor.CursorClientEvents;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.unit.UnitAction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

public class ToggleGraveyardRelease extends Ability {

    public ToggleGraveyardRelease() {
        super(
            UnitAction.TOGGLE_GRAVEYARD_RELEASE,
            0,
            0,
            0,
            false,
            true
        );
        this.defaultHotkey = Keybindings.keyL;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        if (!(placement instanceof GraveyardPlacement gy)) return null;

        return new AbilityButton(
            "Graveyard Release",
            gy.autoRelease ? ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/tick.png") :
                            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/cross.png"),
            hotkey,
            () -> CursorClientEvents.getLeftClickAction() == UnitAction.TOGGLE_GRAVEYARD_RELEASE,
            () -> placement.getUpgradeLevel() == 0,
            () -> true,
            () -> CursorClientEvents.setLeftClickAction(UnitAction.TOGGLE_GRAVEYARD_RELEASE),
            null,
            List.of(
                    FormattedCharSequence.forward(I18n.get("abilities.reignofnether.toggle_graveyard_release"), Style.EMPTY.withBold(true)),
                    FormattedCharSequence.forward("", Style.EMPTY),
                    FormattedCharSequence.forward(I18n.get("abilities.reignofnether.toggle_graveyard_release.tooltip1"), Style.EMPTY)
            ),
            this,
            placement
        );
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, BlockPos targetBp) {
        if (!(buildingUsing instanceof GraveyardPlacement gy))
            return;
        if (gy.getUpgradeLevel() < 1)
            return;

        if (!level.isClientSide()) {
            gy.autoRelease = !gy.autoRelease;
            BuildingAbilityClientboundPacket.doAbility(
                    gy.autoRelease ? UnitAction.SET_GRAVEYARD_RELEASE_ON :
                                    UnitAction.SET_GRAVEYARD_RELEASE_OFF,
                    buildingUsing.originPos);
        }
    }
}
