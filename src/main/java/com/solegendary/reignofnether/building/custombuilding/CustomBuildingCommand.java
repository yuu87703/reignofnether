package com.solegendary.reignofnether.building.custombuilding;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.GarrisonableBuilding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class CustomBuildingCommand {

    public enum TriggerCondition {
        ON_BUILD_COMPLETE,
        ON_DESTROY,
        ON_DAMAGE_TAKEN,
        ON_CAPTURE,
        OFF_COOLDOWN_IF_COMPLETE,
        OFF_COOLDOWN_IF_GARRISONED,
        NONE
    }

    private int tickCooldown = 0;
    public int tickCooldownMax = 20;
    public String commandStr = "";
    public TriggerCondition condition = TriggerCondition.NONE;
    public boolean isValid = true; // updated whenever commandStr is updated
    public Map<? extends CommandNode<?>, CommandSyntaxException> exceptions = new HashMap<>();

    public CustomBuildingCommand() { }

    public static CustomBuildingCommand getFromNbt(CompoundTag tag) {
        CustomBuildingCommand command = new CustomBuildingCommand();
        command.tickCooldown = tag.getInt("tickCooldown");
        command.tickCooldownMax = tag.getInt("tickCooldownMax");
        command.commandStr = tag.getString("commandStr");
        command.condition = TriggerCondition.valueOf(tag.getString("condition"));
        return command;
    }

    public boolean isValid() {
        return isValid;
    }

    public Map<? extends CommandNode<?>, CommandSyntaxException> getExceptions() {
        return exceptions;
    }

    public void updateValidityAndExceptions() {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener handler = client.getConnection();
        this.exceptions.clear();

        if (handler == null) {
            this.isValid = false;
            return;
        }
        String stripped = commandStr.startsWith("/") ? commandStr.substring(1) : commandStr;
        ParseResults<?> parsed = handler.getCommands()
                .parse(new StringReader(stripped), handler.getSuggestionsProvider());

        if (!parsed.getExceptions().isEmpty() || parsed.getReader().canRead()) {
            this.isValid = false;
            this.exceptions = parsed.getExceptions();
            return;
        }
        this.isValid = true;
    }

    public int getTickCooldown() {
        return tickCooldown;
    }

    public void tick(BuildingPlacement bpl) {
        if (bpl.tickAge % 20 == 0 && tickCooldown > 0)
            tickCooldown -= 20;
        if (tickCooldown <= 0 && checkTickingCondition(bpl)) {
            tickCooldown = tickCooldownMax;
            run(bpl);
        }
    }

    public boolean checkTickingCondition(BuildingPlacement bpl) {
        return switch (condition) {
            case OFF_COOLDOWN_IF_COMPLETE -> bpl.isBuilt;
            case OFF_COOLDOWN_IF_GARRISONED -> bpl instanceof GarrisonableBuilding garr && !garr.getOccupants().isEmpty();
            default -> false;
        };
    }

    public void run(BuildingPlacement bpl) {
        if (bpl.level instanceof ServerLevel level) {
            CommandSourceStack source = level.getServer()
                    .createCommandSourceStack()
                    .withPosition(Vec3.atCenterOf(bpl.originPos))
                    .withLevel(level)
                    .withSuppressedOutput();
            level.getServer().getCommands().performPrefixedCommand(source, commandStr);
        }
    }
}
