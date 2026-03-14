package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.resources.Resources;

// Role that a player can take in a scenario map
public class ScenarioRole {
    final int index;
    public String name;
    Faction faction = Faction.NONE;
    Resources startingResources = new Resources("", 0,0,0);
    public int teamNumber;
    public boolean isNpc = false;

    public ScenarioRole(int index) {
        this.index = index;
        this.name = "Player " + index;
        this.teamNumber = index;
    }

    public static ScenarioRole getFromSave(
            int index,
            String name,
            Faction faction,
            Resources resources,
            int teamNumber,
            boolean isNpc
    ) {
        ScenarioRole role = new ScenarioRole(index);
        role.name = name;
        role.faction = faction;
        role.startingResources = resources;
        role.teamNumber = teamNumber;
        role.isNpc = isNpc;
        return role;
    }
}
