package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.resources.Resources;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class ScenarioRoleSaveData extends SavedData {

    public final ArrayList<ScenarioRole> scenarioRoleSaves = new ArrayList<>();

    private static ScenarioRoleSaveData create() {
        return new ScenarioRoleSaveData();
    }

    @Nonnull
    public static ScenarioRoleSaveData getInstance(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return create();
        }
        return server.overworld()
            .getDataStorage()
            .computeIfAbsent(ScenarioRoleSaveData::load, ScenarioRoleSaveData::create, "saved-scenario-data");
    }

    public static ScenarioRoleSaveData load(CompoundTag tag) {
        ReignOfNether.LOGGER.info("ScenarioSaveData.load");

        ScenarioRoleSaveData data = create();
        ListTag ltag = (ListTag) tag.get("scenarioRoles");

        if (ltag != null) {
            for (Tag ctag : ltag) {
                CompoundTag rtag = (CompoundTag) ctag;
                int index = rtag.getInt("index");
                String name = rtag.getString("name");
                Faction faction = Faction.valueOf(rtag.getString("faction"));
                int startingFood = rtag.getInt("startingFood");
                int startingWood = rtag.getInt("startingWood");
                int startingOre = rtag.getInt("startingOre");
                Resources resources = new Resources("", startingFood, startingWood, startingOre);
                int teamNumber = rtag.getInt("teamNumber");
                boolean isNpc = rtag.getBoolean("isNpc");
                data.scenarioRoleSaves.add(ScenarioRole.getFromSave(
                    index,
                    name,
                    faction,
                    resources,
                    teamNumber,
                    isNpc
                ));
                ReignOfNether.LOGGER.info("ScenarioSaveData.load: " + name);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        this.scenarioRoleSaves.forEach(r -> {
            CompoundTag cTag = new CompoundTag();
            cTag.putInt("index", r.index);
            cTag.putString("name", r.name);
            cTag.putString("faction", r.faction.name());
            cTag.putInt("startingFood", r.startingResources.food);
            cTag.putInt("startingWood", r.startingResources.wood);
            cTag.putInt("startingOre", r.startingResources.ore);
            cTag.putInt("teamNumber", r.teamNumber);
            cTag.putBoolean("isNpc", r.isNpc);
            list.add(cTag);
        });
        tag.put("scenarioRoles", list);
        return tag;
    }

    public void save() {
        this.setDirty();
    }
}
