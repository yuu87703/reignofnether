package com.solegendary.reignofnether.hud;

import com.solegendary.reignofnether.guiscreen.TopdownGui;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.orthoview.OrthoviewClientEvents;
import com.solegendary.reignofnether.player.PlayerClientEvents;
import com.solegendary.reignofnether.player.RTSPlayer;
import com.solegendary.reignofnether.resources.Resources;
import com.solegendary.reignofnether.resources.ResourcesClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObserverDisplayEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    private static ArrayList<ObserverPlayerDisplay> obsPlayerDisplays = new ArrayList<>();
    private static ArrayList<RTSPlayerDisplay> rtsPlayerDisplays = new ArrayList<>();

    @SubscribeEvent
    public static void onDrawScreen(ScreenEvent.Render.Post evt) {
        if (!OrthoviewClientEvents.isEnabled() ||
            !(evt.getScreen() instanceof TopdownGui) ||
            !(Keybindings.tab.isDown())) {
            return;
        }
        if (MC.level == null) {
            return;
        }

        if (PlayerClientEvents.isRTSPlayer()) {
            renderRtsPlayerDisplays(evt);
        } else {
            renderObsPlayerDisplays(evt);
        }
    }

    private static void renderObsPlayerDisplays(ScreenEvent.Render.Post evt) {
        int screenWidth = MC.getWindow().getGuiScaledWidth();

        int blitX = screenWidth / 2 - ObserverPlayerDisplay.DISPLAY_WIDTH / 2;
        int blitY = 70;

        GuiGraphics guiGraphics = evt.getGuiGraphics();

        obsPlayerDisplays.removeIf(r -> !ResourcesClientEvents.resourcesList.contains(r.resources));

        List<Resources> trackedResources = obsPlayerDisplays.stream().map(d -> d.resources).collect(Collectors.toCollection(ArrayList::new));
        for (Resources resources : ResourcesClientEvents.resourcesList) {
            if (!trackedResources.contains(resources)) {
                obsPlayerDisplays.add(new ObserverPlayerDisplay(resources));
            }
        }
        for (ObserverPlayerDisplay playerDisplay : obsPlayerDisplays) {
            playerDisplay.render(guiGraphics, blitX, blitY);
            blitY += Button.DEFAULT_ICON_FRAME_SIZE;
        }
    }

    private static void renderRtsPlayerDisplays(ScreenEvent.Render.Post evt) {
        int screenWidth = MC.getWindow().getGuiScaledWidth();

        int blitX = screenWidth / 2 - RTSPlayerDisplay.DISPLAY_WIDTH / 2;
        int blitY = 70;

        GuiGraphics guiGraphics = evt.getGuiGraphics();

        rtsPlayerDisplays.removeIf(r -> !PlayerClientEvents.rtsPlayers.contains(r.rtsPlayer));

        List<RTSPlayer> trackedPlayers = rtsPlayerDisplays.stream().map(d -> d.rtsPlayer).collect(Collectors.toCollection(ArrayList::new));
        for (RTSPlayer rtsPlayer : PlayerClientEvents.rtsPlayers) {
            if (!trackedPlayers.contains(rtsPlayer)) {// && !rtsPlayer.name.equals(MC.player.getName().getString())) {
                rtsPlayerDisplays.add(new RTSPlayerDisplay(rtsPlayer));
            }
        }
        for (RTSPlayerDisplay playerDisplay : rtsPlayerDisplays) {
            playerDisplay.render(guiGraphics, blitX, blitY, evt.getMouseX(), evt.getMouseY());
            blitY += Button.DEFAULT_ICON_FRAME_SIZE;
        }
    }
}
