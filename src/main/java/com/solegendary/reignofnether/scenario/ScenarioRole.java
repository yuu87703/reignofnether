package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.resources.Resources;

import java.util.List;

// Role that a player can take in a scenario map
public class ScenarioRole {
    Faction faction = Faction.NONE;
    Resources startingResources = new Resources("", 0,0,0);
    public String name;
    int index;
    int teamNumber;

    public ScenarioRole(int index) {
        this.index = index;
        this.name = "Player " + index;
        this.teamNumber = index;
    }
}
