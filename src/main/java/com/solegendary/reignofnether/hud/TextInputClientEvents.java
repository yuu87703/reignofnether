package com.solegendary.reignofnether.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class TextInputClientEvents {

    private static final List<EditBox> registeredInputs = new ArrayList<>();

    public static EditBox register(int x, int y, int width, int height) {
        EditBox input = new EditBox(Minecraft.getInstance().font, x, y, width, height, Component.literal(""));
        registeredInputs.add(input);
        return input;
    }

    public static void unregister(EditBox input) {
        registeredInputs.remove(input);
    }

    public static boolean isAnyInputFocused() {
        for (EditBox input : registeredInputs)
            if (input.isFocused())
                return true;
        return false;
    }

    @SubscribeEvent
    public static void onDrawScreen(ScreenEvent.Render.Post evt) {
        for (EditBox input : registeredInputs) {
            if (input.isHovered()) {
                evt.getGuiGraphics().fill(
                        input.getX() - 1, input.getY() - 1,
                        input.getX() + input.getWidth() + 1, input.getY() + input.getHeight() + 1,
                        0x30FFFFFF
                );
            }
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Post evt) {
        for (EditBox input : registeredInputs)
            input.keyPressed(evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
    }

    @SubscribeEvent
    public static void onCharTyped(ScreenEvent.CharacterTyped.Post evt) {
        for (EditBox input : registeredInputs)
            if (input.isFocused())
                input.charTyped(evt.getCodePoint(), evt.getModifiers());
    }

    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Post evt) {
        for (EditBox input : registeredInputs) {
            input.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton());
            boolean isMouseOver = input.isMouseOver(evt.getMouseX(), evt.getMouseY());
            input.setFocused(isMouseOver);
        }
    }
}