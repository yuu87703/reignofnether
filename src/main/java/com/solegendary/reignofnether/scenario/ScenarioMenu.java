package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.keybinds.Keybinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.reignofnether.util.MiscUtil.fcs;

public class ScenarioMenu {

    private static Minecraft MC = Minecraft.getInstance();

    public static List<Button> renderRoleNameAndHelperButtons(ScreenEvent.Render.Post evt, ScenarioRole building, int x, int y) {
        evt.getGuiGraphics().drawString(MC.font, fcs(building.name, true), x + 52, y, 0xFFFFFF);
        return List.of(); // TODO: left/right buttons to cycle through scenario roles
        // TODO: buttons to show how many units and buildings are tied to this role, clicking them goes to those units
    }

    public static Button renderCloseButton(ScreenEvent.Render.Post evt, int x, int y) {
        Button closeButton = new Button("Close Scenario Menu",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/cross_square.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> ScenarioClientEvents.isMenuOpen = false,
                null,
                List.of()
        );
        closeButton.frameResource = null;
        renderButton(closeButton, x, y, evt);
        return closeButton;
    }

    public static Button renderResetRoleButton(ScreenEvent.Render.Post evt, int x, int y) {
        Button deregisterButton = new Button("Reset Scenario Role",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/items/barrier.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> {
                    // TODO: reset the currently selected role to default and remove it from all units/buildings
                },
                null,
                List.of(
                        fcs(I18n.get("sandbox.reignofnether.scenario.reset_role.tooltip1"), true),
                        fcs(I18n.get("sandbox.reignofnether.scenario.reset_role.tooltip2"))
                )
        );
        deregisterButton.frameResource = null;
        renderButton(deregisterButton, x, y, evt);
        return deregisterButton;
    }

    public static List<Button> renderCustomisationButtons(ScreenEvent.Render.Post evt, int x, int y) {
        int origX = x;
        int origY = y;
        ArrayList<Button> buttonsCol1 = new ArrayList<>();
        ArrayList<Button> buttonsCol2 = new ArrayList<>();
        ScenarioRole scenarioRole = ScenarioClientEvents.getScenarioRoleToEdit();

        // TODO:
        // Scenario Name
        // Scenario opening message
        //
        // Per role:
        // - Starting resources
        // - Team number
        // - Faction

        buttonsCol1.addAll(buttonsCol2);
        return buttonsCol1;
    }

    private static void renderButton(Button button, int x, int y, ScreenEvent.Render.Post evt) {
        if (!button.isHidden.get()) {
            button.render(evt.getGuiGraphics(), x, y, evt.getMouseX(), evt.getMouseY());
            if (button.isMouseOver(evt.getMouseX(), evt.getMouseY()) && button.tooltipLines != null)
                button.renderTooltip(evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY());
        }
    }
}
