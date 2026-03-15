package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.building.BuildingClientEvents;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.guiscreen.TopdownGui;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.hud.RectZone;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.unit.UnitClientEvents;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import com.solegendary.reignofnether.util.MyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScenarioClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    public static boolean isMenuOpen = false;

    // index 0 is treated as neutral
    public static final ArrayList<ScenarioRole> scenarioRoles = new ArrayList<>(List.of(
            new ScenarioRole(0),
            new ScenarioRole(1),
            new ScenarioRole(2),
            new ScenarioRole(3),
            new ScenarioRole(4),
            new ScenarioRole(5),
            new ScenarioRole(6),
            new ScenarioRole(7)
    ));
    private static int roleIndexToEdit = 0; // list index, not role.index
    private static final ArrayList<Button> renderedButtons = new ArrayList<>();
    private static final ArrayList<RectZone> hudZones = new ArrayList<>();

    public static void cycleRoleIndexToEdit(boolean reverse) {
        roleIndexToEdit += reverse ? -1 : 1;
        if (roleIndexToEdit < 0)
            roleIndexToEdit = scenarioRoles.size() - 1;
        else if (roleIndexToEdit >= scenarioRoles.size())
            roleIndexToEdit = 0;
    }

    @Nullable
    public static ScenarioRole getScenarioRoleToEdit() {
        try {
            return scenarioRoles.get(roleIndexToEdit);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static void cycleRoleUnits(boolean reverse) {
        int id1 = 0;
        LivingEntity selUnit = null;
        List<LivingEntity> selUnits = UnitClientEvents.getSelectedUnits();
        if (selUnits.size() > 0) {
            selUnit = selUnits.get(0);
            id1 = selUnits.get(0).getId();
        }
        UnitClientEvents.clearSelectedUnits();
        for (LivingEntity le : UnitClientEvents.getAllUnits()) {
            ScenarioRole role = ScenarioClientEvents.getScenarioRoleToEdit();
            if (role != null && le instanceof Unit unit && unit.getScenarioRoleIndex() == role.index) {
                UnitClientEvents.addSelectedUnit(le);
                int id2 = le.getId();
                if (!Keybindings.shiftMod.isDown() &&
                        ((id2 < id1 && reverse) || (id2 > id1 && !reverse)))
                    return;
            }
        }
        if (selUnit != null && UnitClientEvents.getSelectedUnits().isEmpty())
            UnitClientEvents.addSelectedUnit(selUnit);
    }

    public static void cycleRoleBuildings(boolean reverse) {
        int totalCoords1 = 0;
        BuildingPlacement selBuilding = null;
        List<BuildingPlacement> selBuildings = BuildingClientEvents.getSelectedBuildings();
        if (selBuildings.size() > 0) {
            selBuilding = selBuildings.get(0);
            BlockPos bp1 = selBuilding.originPos;
            totalCoords1 += bp1.getX() + bp1.getY() + bp1.getZ();
        }
        BuildingClientEvents.clearSelectedBuildings();
        for (BuildingPlacement bpl : BuildingClientEvents.getBuildings()) {
            ScenarioRole role = ScenarioClientEvents.getScenarioRoleToEdit();
            if (role != null && bpl.scenarioRoleIndex == role.index) {
                BuildingClientEvents.addSelectedBuilding(bpl);
                BlockPos bp2 = bpl.originPos;
                int totalCoords2 = bp2.getX() + bp2.getY() + bp2.getZ();
                if (!Keybindings.shiftMod.isDown() &&
                        ((totalCoords2 < totalCoords1 && reverse) || (totalCoords2 > totalCoords1 && !reverse)))
                    return;
            }
        }
        if (selBuilding != null && BuildingClientEvents.getSelectedBuildings().isEmpty())
            BuildingClientEvents.addSelectedBuilding(selBuilding);
    }

    public static void clearRoleUnits() {
        for (LivingEntity le : UnitClientEvents.getAllUnits()) {
            if (le instanceof Unit) {
                ScenarioServerboundPacket.setUnitRole(0, le.getId());
            }
        }
    }

    public static void clearRoleBuildings() {
        for (BuildingPlacement bpl : BuildingClientEvents.getBuildings()) {
            ScenarioServerboundPacket.setBuildingRole(0, bpl.originPos);
        }
    }

    public static int getNumRoleUnits() {
        int count = 0;
        for (LivingEntity le : UnitClientEvents.getAllUnits()) {
            ScenarioRole role = ScenarioClientEvents.getScenarioRoleToEdit();
            if (role != null && le instanceof Unit unit && unit.getScenarioRoleIndex() == role.index) {
                count++;
            }
        }
        return count;
    }

    public static int getNumRoleBuildings() {
        int count = 0;
        for (BuildingPlacement bpl : BuildingClientEvents.getBuildings()) {
            ScenarioRole role = ScenarioClientEvents.getScenarioRoleToEdit();
            if (role != null && bpl.scenarioRoleIndex == role.index) {
                count++;
            }
        }
        return count;
    }

    @SubscribeEvent
    public static void onDrawScreen(ScreenEvent.Render.Post evt) {
        hudZones.clear();
        renderedButtons.clear();
        if (MC.screen instanceof TopdownGui && isMenuOpen) {
            int width = 254;
            int height = 154;
            int blitX = MC.screen.width - width - 120;
            int blitY = 5;
            MyRenderer.renderFrameWithBg(evt.getGuiGraphics(), blitX, blitY, width, height, 0xA0000000);

            ScenarioRole role = getScenarioRoleToEdit();
            if (role != null) {
                renderedButtons.addAll(ScenarioMenu.renderRoleNameAndCycleButtons(evt, getScenarioRoleToEdit(), blitX + 14, blitY + 14));
                renderedButtons.add(ScenarioMenu.renderHelpButton(evt, blitX + width - Button.itemIconSize - 32, blitY + 4));
                renderedButtons.add(ScenarioMenu.renderCloseButton(evt, blitX + width - Button.itemIconSize - 12, blitY + 4));
                renderedButtons.addAll(ScenarioMenu.renderCustomisationButtonsColumn1(evt, blitX + 16, blitY + 50));
                renderedButtons.addAll(ScenarioMenu.renderCustomisationButtonsColumn2(evt, blitX + 140, blitY + 50));
                renderedButtons.add(ScenarioMenu.renderResetRoleButton(evt, blitX + width - Button.itemIconSize - 12, blitY + height - 26));
            }
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
