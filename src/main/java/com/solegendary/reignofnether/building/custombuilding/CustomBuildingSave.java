package com.solegendary.reignofnether.building.custombuilding;

import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.Resources;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;

// registry saves only - placements are saved as a regular BuildingSaves
public class CustomBuildingSave {

    public CompoundTag structureNbt;
    public String buildingName;
    public Vec3i structureSize;
    public String portraitBlockRegistryKey;
    public boolean capturable;
    public boolean invulnerable;
    public int nightRadius;
    public int netherRadius;
    public boolean buildableByVillagers;
    public boolean buildableByMonsters;
    public boolean buildableByPiglins;
    public ResourceCost cost;

    public CustomBuildingSave(CompoundTag structureNbt, String buildingName, Vec3i structureSize, String portraitBlockRegistryKey,
                              boolean capturable, boolean invulnerable, int nightRadius, int netherRadius,
                              boolean buildableByVillagers, boolean buildableByMonsters, boolean buildableByPiglins, ResourceCost cost) {
        this.structureNbt = structureNbt;
        this.buildingName = buildingName;
        this.structureSize = structureSize;
        this.portraitBlockRegistryKey = portraitBlockRegistryKey;
        this.capturable = capturable;
        this.invulnerable = invulnerable;
        this.nightRadius = nightRadius;
        this.netherRadius = netherRadius;
        this.buildableByVillagers = buildableByVillagers;
        this.buildableByMonsters = buildableByMonsters;
        this.buildableByPiglins = buildableByPiglins;
        this.cost = cost;
    }
}
