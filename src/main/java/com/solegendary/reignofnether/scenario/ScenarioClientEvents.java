package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.guiscreen.TopdownGui;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.hud.RectZone;
import com.solegendary.reignofnether.util.MyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ScenarioClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    public static boolean isMenuOpen = false;

    private static final List<ScenarioRole> scenarioRoles = List.of(
            new ScenarioRole(),
            new ScenarioRole(),
            new ScenarioRole(),
            new ScenarioRole(),
            new ScenarioRole(),
            new ScenarioRole(),
            new ScenarioRole(),
            new ScenarioRole()
    );

    private static int scenarioRoleIndexToEdit = 0;
    private static final ArrayList<Button> renderedButtons = new ArrayList<>();
    private static final ArrayList<RectZone> hudZones = new ArrayList<>();

    public static ScenarioRole getScenarioRoleToEdit() {
        return scenarioRoles.get(scenarioRoleIndexToEdit);
    }

    @SubscribeEvent
    public static void onDrawScreen(ScreenEvent.Render.Post evt) {
        hudZones.clear();
        renderedButtons.clear();
        if (MC.screen instanceof TopdownGui) {
            int blitX = 100;
            int blitY = 40;
            int width = 310;
            int height = 200;
            MyRenderer.renderFrameWithBg(evt.getGuiGraphics(), blitX, blitY, width, height, 0xA0000000);

            renderedButtons.addAll(ScenarioMenu.renderRoleNameAndHelperButtons(evt, getScenarioRoleToEdit(), blitX + 18, blitY + 18));
            renderedButtons.add(ScenarioMenu.renderCloseButton(evt, blitX + width - Button.itemIconSize - 12, blitY + 4));
            renderedButtons.add(ScenarioMenu.renderResetRoleButton(evt, blitX + width - Button.itemIconSize - 12, blitY + height - 26));
            renderedButtons.addAll(ScenarioMenu.renderCustomisationButtons(evt, blitX + 6, blitY + 38));

            evt.getGuiGraphics().drawString(MC.font, "More options coming soon!", blitX + 10, blitY + height - 18, 0xFFFFFF);

            hudZones.add(new RectZone(blitX, blitY, blitX + width, blitY + height));
        }
    }

    public static boolean isMouseOverHud(int mouseX, int mouseY) {
        for (RectZone hudZone : hudZones)
            if (hudZone.isMouseOver(mouseX, mouseY))
                return true;
        return false;
    }

    @SubscribeEvent
    public static void onMousePress(ScreenEvent.MouseButtonPressed.Post evt) {
        for (Button button : renderedButtons) {
            if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
                button.checkClicked((int) evt.getMouseX(), (int) evt.getMouseY(), true);
            } else if (evt.getButton() == GLFW.GLFW_MOUSE_BUTTON_2) {
                button.checkClicked((int) evt.getMouseX(), (int) evt.getMouseY(), false);
            }
        }
    }
}
