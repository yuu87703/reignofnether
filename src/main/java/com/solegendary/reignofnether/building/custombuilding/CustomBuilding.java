package com.solegendary.reignofnether.building.custombuilding;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.util.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.solegendary.reignofnether.util.MiscUtil.fcs;

public class CustomBuilding extends Building {

    public Vec3i structureSize;
    public final CompoundTag structureNbt;

    public CustomBuilding(String structureName, Vec3i structureSize, Block portraitBlock, CompoundTag nbt) {
        super(structureName, ResourceCost.Building(0,0,0,0), false);
        this.name = WordUtils.capitalize(structureName
                .replace("minecraft:", "")
                .replace("reignofnether:", "")
                .replace("_", " "));
        this.structureSize = structureSize;
        this.structureNbt = nbt;
        this.portraitBlock = portraitBlock;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/command_block.png");
        this.startingBlockTypes.addAll(BuildingBlockData.getBuildingBlocksFromNbt(structureNbt)
                .stream().filter(bb -> bb.getBlockPos().getY() == 0)
                .map(bb -> bb.getBlockState().getBlock()).toList());
    }

    @Override
    public ArrayList<BuildingBlock> getRelativeBlockData(LevelAccessor level) {
        return BuildingBlockData.getBuildingBlocksFromNbt(structureNbt);
    }

    public Faction getFaction() {return Faction.NONE;}

    @Override
    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        String blockName = portraitBlock.getName().getString().replace(" ", "_").toLowerCase();
        return new BuildingPlaceButton(
                this.name,
                ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/icons/blocks/command_block_side.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == this,
                () -> false,
                () -> true,
                List.of(
                        fcs(this.name, true)
                ),
                this
        );
    }
}
