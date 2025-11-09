package com.solegendary.reignofnether.building.custombuilding;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.hud.HudClientEvents;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.sandbox.SandboxServerboundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;

import java.util.List;

import static com.solegendary.reignofnether.building.custombuilding.CustomBuildingClientEvents.getCustomBuildingToEdit;
import static com.solegendary.reignofnether.building.custombuilding.CustomBuildingClientEvents.setCustomBuildingToEdit;
import static com.solegendary.reignofnether.util.MiscUtil.fcs;

public class CustomBuildingMenu {


    public static Button renderCloseButton(ScreenEvent.Render.Post evt, int x, int y) {
        Button closeButton = new Button("Close Custom Building Menu",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/cross_square.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> setCustomBuildingToEdit(null),
                null,
                List.of()
        );
        closeButton.frameResource = null;
        closeButton.render(evt.getGuiGraphics(), x, y, evt.getMouseX(), evt.getMouseY());
        return closeButton;
    }

    public static Button renderDeregisterButton(ScreenEvent.Render.Post evt, int x, int y) {
        CustomBuilding building = getCustomBuildingToEdit();
        Button deregisterButton = new Button("Deregister Custom Building",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/items/barrier.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> {
                    CustomBuildingServerboundPacket.deregisterBuilding(building.name);
                    CustomBuildingClientEvents.customBuildings.removeIf(b -> b.name.equals(building.name));
                    setCustomBuildingToEdit(null);
                },
                null,
                List.of(
                        fcs(I18n.get("sandbox.reignofnether.custom_buildings.deregister.tooltip1"), true),
                        fcs(I18n.get("sandbox.reignofnether.custom_buildings.deregister.tooltip2"))
                )
        );
        deregisterButton.frameResource = null;
        renderButton(deregisterButton, x, y, evt, "");
        return deregisterButton;
    }

    private static void renderButton(Button button, int x, int y, ScreenEvent.Render.Post evt, String description) {
        if (!button.isHidden.get()) {
            button.render(evt.getGuiGraphics(), x, y, evt.getMouseX(), evt.getMouseY());
            if (button.isMouseOver(evt.getMouseX(), evt.getMouseY()) && button.tooltipLines != null)
                button.renderTooltip(evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY());
            if (!description.isBlank()) {
                evt.getGuiGraphics().drawString(
                        Minecraft.getInstance().font,
                        description, x, y,
                        0xFFFFFFFF
                );
            }
        }
    }
}
