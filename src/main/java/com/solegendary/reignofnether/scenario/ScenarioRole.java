package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.resources.Resources;

import java.util.List;

// Role that a player can take in a scenario map
public class ScenarioRole {
    Faction faction;
    Resources startingResources;
    String name;
    List<ScenarioRole> allies;

    public ScenarioRole() {

    }
}
