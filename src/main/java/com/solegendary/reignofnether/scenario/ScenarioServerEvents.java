package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.player.PlayerServerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScenarioServerEvents {

    // index 0 is treated as neutral
    public static final ArrayList<ScenarioRole> scenarioRoles = new ArrayList<>(List.of(
            new ScenarioRole(1),
            new ScenarioRole(2),
            new ScenarioRole(3),
            new ScenarioRole(4),
            new ScenarioRole(5),
            new ScenarioRole(6),
            new ScenarioRole(7),
            new ScenarioRole(8)
    ));

    @Nullable
    public static ScenarioRole getScenarioRole(int index) {
        try {
            return scenarioRoles.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent evt) {
        ServerLevel level = evt.getServer().getLevel(Level.OVERWORLD);
        if (level != null) {
            saveScenarioRoles(level);
        }
    }

    public static void saveScenarioRoles() {
        saveScenarioRoles(PlayerServerEvents.serverLevel);
    }

    public static void saveScenarioRoles(ServerLevel level) {
        ScenarioRoleSaveData scenarioRoleSaveData = ScenarioRoleSaveData.getInstance(level);
        scenarioRoleSaveData.scenarioRoleSaves.clear();
        scenarioRoleSaveData.scenarioRoleSaves.addAll(scenarioRoles);
        scenarioRoleSaveData.save();
        level.getDataStorage().save();
    }

    @SubscribeEvent
    public static void loadScenarioRoles(ServerStartedEvent evt) {
        ServerLevel level = evt.getServer().getLevel(Level.OVERWORLD);
        if (level != null) {
            ScenarioRoleSaveData scenarioRoleSaveData = ScenarioRoleSaveData.getInstance(level);
            scenarioRoles.clear();
            scenarioRoles.addAll(scenarioRoleSaveData.scenarioRoleSaves);
            ReignOfNether.LOGGER.info("loaded scenario roles in serverevents");

            // TODO: send to clientside
        }
    }
}

