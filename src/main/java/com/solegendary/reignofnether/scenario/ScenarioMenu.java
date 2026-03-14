package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.custombuilding.CustomBuildingAction;
import com.solegendary.reignofnether.building.custombuilding.CustomBuildingServerboundPacket;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.hud.buttons.BooleanButton;
import com.solegendary.reignofnether.hud.buttons.IntegerButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.resources.ResourceName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.reignofnether.faction.Faction.*;
import static com.solegendary.reignofnether.util.MiscUtil.fcs;

public class ScenarioMenu {

    private static Minecraft MC = Minecraft.getInstance();

    public static List<Button> renderRoleNameAndHelperButtons(ScreenEvent.Render.Post evt, ScenarioRole role, int x, int y) {
        int xr = x + 5;
        int yr = y;
        evt.getGuiGraphics().drawString(MC.font, fcs(I18n.get("sandbox.reignofnether.scenario.player_number", role.index), true), xr, yr, 0xFFFFFF);
        evt.getGuiGraphics().drawString(MC.font, fcs(role.name), xr, yr + 18, 0xFFFFFF);

        Button cycleButtonBackward = new Button("Cycle Role Backward",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/tutorial_arrow_left.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> ScenarioClientEvents.cycleRoleIndexToEdit(true),
                null,
                List.of()
        );
        Button cycleButtonForward = new Button("Cycle Role Forward",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/tutorial_arrow_right.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> ScenarioClientEvents.cycleRoleIndexToEdit(false),
                null,
                List.of()
        );
        xr += 120;
        yr -= 7;
        renderButton(cycleButtonBackward, xr, yr, evt);
        xr += Button.DEFAULT_ICON_FRAME_SIZE;
        renderButton(cycleButtonForward, xr, yr, evt);

        Button unitsButton = new Button("Role Units",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/spawn_egg.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> ScenarioClientEvents.cycleRoleUnits(false),
                () -> {
                    if (Keybindings.shiftMod.isDown() && Keybindings.ctrlMod.isDown())
                        ScenarioClientEvents.clearRoleUnits();
                    else
                        ScenarioClientEvents.cycleRoleUnits(true);
                },
                List.of(
                        fcs(I18n.get("sandbox.reignofnether.scenario.select_role_units", ScenarioClientEvents.getNumRoleUnits())),
                        fcs(I18n.get("sandbox.reignofnether.scenario.select_all_shift")),
                        fcs(I18n.get("sandbox.reignofnether.scenario.clear_all"))
                )
        );
        Button buildingsButton = new Button("Role Buildings",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/crafting_table_front.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> ScenarioClientEvents.cycleRoleBuildings(false),
                () -> {
                    if (Keybindings.shiftMod.isDown() && Keybindings.ctrlMod.isDown())
                        ScenarioClientEvents.clearRoleBuildings();
                    else
                        ScenarioClientEvents.cycleRoleBuildings(true);
                },
                List.of(
                    fcs(I18n.get("sandbox.reignofnether.scenario.select_role_buildings", ScenarioClientEvents.getNumRoleBuildings())),
                    fcs(I18n.get("sandbox.reignofnether.scenario.select_all_shift")),
                    fcs(I18n.get("sandbox.reignofnether.scenario.clear_all"))
                )
        );
        xr -= Button.DEFAULT_ICON_FRAME_SIZE;
        yr += 30;
        renderButton(unitsButton, xr, yr, evt);
        xr += Button.DEFAULT_ICON_FRAME_SIZE;
        renderButton(buildingsButton, xr, yr, evt);

        return List.of(cycleButtonBackward, cycleButtonForward, buildingsButton, unitsButton);
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

    public static Button renderHelpButton(ScreenEvent.Render.Post evt, int x, int y) {
        Button closeButton = new Button("Scenario Help",
                Button.itemIconSize,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/hud/help.png"),
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                null,
                null,
                List.of(
                    fcs("sandbox.reignofnether.scenario.help_tooltip1"),
                    fcs("sandbox.reignofnether.scenario.help_tooltip2"),
                    fcs("sandbox.reignofnether.scenario.help_tooltip3")
                )
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
                        fcs(I18n.get("sandbox.reignofnether.scenario.reset_role.tooltip1"), true)
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
        ScenarioRole role = ScenarioClientEvents.getScenarioRoleToEdit();

        if (role == null)
            return List.of();

        // TODO:
        // Scenario Name
        // Scenario opening message
        //
        // Per role:
        // - NPC role (prevents players choosing it to start)

        Button factionButton = new Button(
            "Toggle Faction",
            Button.itemIconSize,
            switch (role.faction) {
                case VILLAGERS -> ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/mobheads/villager.png");
                case MONSTERS -> ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/mobheads/creeper.png");
                case PIGLINS -> ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/mobheads/grunt.png");
                case NONE, NEUTRAL -> ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/mobheads/sheep.png");
            },
            (Keybinding) null,
            () -> false,
            () -> false,
            () -> true,
            () -> {
                switch (role.faction) {
                    case VILLAGERS -> {
                        role.faction = MONSTERS;
                        ScenarioServerboundPacket.setRoleFaction(role.index, MONSTERS);
                    }
                    case MONSTERS -> {
                        role.faction = PIGLINS;
                        ScenarioServerboundPacket.setRoleFaction(role.index, PIGLINS);
                    }
                    case PIGLINS -> {
                        role.faction = NEUTRAL;
                        ScenarioServerboundPacket.setRoleFaction(role.index, NEUTRAL);
                    }
                    case NONE, NEUTRAL -> {
                        role.faction = VILLAGERS;
                        ScenarioServerboundPacket.setRoleFaction(role.index, VILLAGERS);
                    }
                }

            },
            () -> {
                switch (role.faction) {
                    case VILLAGERS -> {
                        role.faction = NEUTRAL;
                        ScenarioServerboundPacket.setRoleFaction(role.index, NEUTRAL);
                    }
                    case MONSTERS -> {
                        role.faction = VILLAGERS;
                        ScenarioServerboundPacket.setRoleFaction(role.index, VILLAGERS);
                    }
                    case PIGLINS -> {
                        role.faction = MONSTERS;
                        ScenarioServerboundPacket.setRoleFaction(role.index, MONSTERS);
                    }
                    case NONE, NEUTRAL -> {
                        role.faction = PIGLINS;
                        ScenarioServerboundPacket.setRoleFaction(role.index, PIGLINS);
                    }
                }
            },
            List.of()
        );
        String factionStr = switch (role.faction) {
            case VILLAGERS -> I18n.get("hud.faction.reignofnether.villager");
            case MONSTERS -> I18n.get("hud.faction.reignofnether.monster");
            case PIGLINS -> I18n.get("hud.faction.reignofnether.piglin");
            case NONE, NEUTRAL -> I18n.get("hud.faction.reignofnether.neutral");
        };
        String label = I18n.get("sandbox.reignofnether.faction_button1", factionStr);
        evt.getGuiGraphics().drawString(MC.font, label, x + 27, y + 7, 0xFFFFFF);
        renderButton(factionButton, x, y, evt);

        Button setStartingFoodButton = new IntegerButton(
                I18n.get("sandbox.reignofnether.scenario.starting_food") + ": " + role.startingResources.food,
                () -> {
                    int value = Math.min(10000, role.startingResources.food + (Keybindings.shiftMod.isDown() ? 100 : 5));
                    ScenarioServerboundPacket.setStartingResources(role.index, ResourceName.FOOD, value);
                    role.startingResources.food = value;
                },
                () -> {
                    int value = Math.max(0, role.startingResources.food - (Keybindings.shiftMod.isDown() ? 100 : 5));
                    ScenarioServerboundPacket.setStartingResources(role.index, ResourceName.FOOD, value);
                    role.startingResources.food = value;
                }
        );
        setStartingFoodButton.iconResource = ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/items/wheat.png");
        buttonsCol1.add(setStartingFoodButton);

        Button setStartingWoodButton = new IntegerButton(
                I18n.get("sandbox.reignofnether.scenario.starting_wood") + ": " + role.startingResources.wood,
                () -> {
                    int value = Math.min(10000, role.startingResources.wood + (Keybindings.shiftMod.isDown() ? 100 : 5));
                    ScenarioServerboundPacket.setStartingResources(role.index, ResourceName.WOOD, value);
                    role.startingResources.wood = value;
                },
                () -> {
                    int value = Math.max(0, role.startingResources.wood - (Keybindings.shiftMod.isDown() ? 100 : 5));
                    ScenarioServerboundPacket.setStartingResources(role.index, ResourceName.WOOD, value);
                    role.startingResources.wood = value;
                }
        );
        setStartingWoodButton.iconResource = ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/items/wood.png");
        buttonsCol1.add(setStartingWoodButton);

        Button setStartingOreButton = new IntegerButton(
                I18n.get("sandbox.reignofnether.scenario.starting_ore") + ": " + role.startingResources.ore,
                () -> {
                    int value = Math.min(10000, role.startingResources.ore + (Keybindings.shiftMod.isDown() ? 100 : 5));
                    ScenarioServerboundPacket.setStartingResources(role.index, ResourceName.ORE, value);
                    role.startingResources.ore = value;
                },
                () -> {
                    int value = Math.max(0, role.startingResources.ore - (Keybindings.shiftMod.isDown() ? 100 : 5));
                    ScenarioServerboundPacket.setStartingResources(role.index, ResourceName.ORE, value);
                    role.startingResources.ore = value;
                }
        );
        setStartingOreButton.iconResource = ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/items/iron_ore.png");
        buttonsCol1.add(setStartingOreButton);

        Button setTeamButton = new IntegerButton(
                I18n.get("sandbox.reignofnether.scenario.team_number", role.teamNumber),
                () -> {
                    role.teamNumber += 1;
                    if (role.teamNumber > ScenarioClientEvents.scenarioRoles.size())
                        role.teamNumber = 1;
                    ScenarioServerboundPacket.setStartingResources(role.index, ResourceName.FOOD, role.teamNumber);
                },
                () -> {
                    role.teamNumber -= 1;
                    if (role.teamNumber < 1)
                        role.teamNumber = ScenarioClientEvents.scenarioRoles.size();
                    ScenarioServerboundPacket.setStartingResources(role.index, ResourceName.FOOD, role.teamNumber);
                },
                I18n.get("sandbox.reignofnether.scenario.team_number_tooltip")
        );
        setTeamButton.iconResource = ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/items/sweet_berries.png");
        buttonsCol1.add(setTeamButton);

        Button npcRoleButton = new BooleanButton(
                role.isNpc ? I18n.get("sandbox.reignofnether.scenario.npc_role_true") :
                            I18n.get("sandbox.reignofnether.scenario.npc_role_false"),
                role.isNpc,
                () -> {
                    role.isNpc = !role.isNpc;
                    ScenarioServerboundPacket.setRoleIsNpc(role.index, role.isNpc);
                },
                I18n.get("sandbox.reignofnether.scenario.npc_role_tooltip")
        );
        buttonsCol1.add(npcRoleButton);

        y += 20;
        for (Button button : buttonsCol1) {
            renderButton(button, x, y, evt);
            y += 18;
        }
        x += 150;
        y = origY;
        for (Button button : buttonsCol2) {
            renderButton(button, x, y, evt);
            y += 18;
        }
        buttonsCol1.addAll(buttonsCol2);
        buttonsCol1.add(factionButton);
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
