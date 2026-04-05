package com.solegendary.reignofnether.hud;

import com.solegendary.reignofnether.util.MyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TextInputClientEvents {

    private static final List<MyEditBox> registeredInputs = new ArrayList<>();

    public static void registerEditBox(MyEditBox box) {
        registeredInputs.add(box);
    }

    public static void deregisterEditBox(MyEditBox input) {
        registeredInputs.remove(input);
    }

    public static boolean isAnyInputFocused() {
        for (MyEditBox input : registeredInputs)
            if (input.isFocused())
                return true;
        return false;
    }

    @SubscribeEvent
    public static void onDrawScreen(ScreenEvent.Render.Post evt) {
        if (Minecraft.getInstance().isPaused())
            return;
        for (MyEditBox input : registeredInputs) {
            input.render(evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY(), 0f);
            if (input.isHovered()) {
                evt.getGuiGraphics().fill(
                        input.getX() - 1, input.getY() - 1,
                        input.getX() + input.getWidth() + 1, input.getY() + input.getHeight() + 1,
                        0x30FFFFFF
                );
                MyRenderer.renderTooltip(evt.getGuiGraphics(), input.tooltipLines, evt.getMouseX(), evt.getMouseY());
            }
            if (input.isFocused() && input.commandSuggestions != null) {
                input.commandSuggestions.updateCommandInfo();
                input.commandSuggestions.render(evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY());
            }
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Post evt) {
        for (MyEditBox input : registeredInputs) {
            input.keyPressed(evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
            if (evt.getKeyCode() == GLFW.GLFW_KEY_ENTER)
                input.setFocused(false);
        }
    }

    @SubscribeEvent
    public static void onCharTyped(ScreenEvent.CharacterTyped.Post evt) {
        for (MyEditBox input : registeredInputs)
            if (input.isFocused())
                input.charTyped(evt.getCodePoint(), evt.getModifiers());
    }

    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Post evt) {
        for (MyEditBox input : registeredInputs) {
            input.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton());
            boolean isMouseOver = input.isMouseOver(evt.getMouseX(), evt.getMouseY());
            boolean wasFocused = input.isFocused();
            input.setFocused(isMouseOver);
            if (wasFocused && !input.isFocused()) {
                if (input.onDefocus != null)
                    input.onDefocus.accept(input.getValue());
            }
        }
    }
}