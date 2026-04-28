package com.solegendary.reignofnether.building.buildings.placements;

import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.building.buildings.monsters.Graveyard;
import com.solegendary.reignofnether.building.production.ProductionItems;
import com.solegendary.reignofnether.registrars.BlockRegistrar;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.research.ResearchServerEvents;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SkullBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.solegendary.reignofnether.building.BuildingUtils.getAbsoluteBlockData;

public class GraveyardPlacement extends ProductionPlacement {

    public boolean autoRelease = true;

    private static List<BlockPos> skullPoses = List.of(
            new BlockPos(2,1,2),
            new BlockPos(4,1,2),
            new BlockPos(5,1,4),
            new BlockPos(5,1,6),
            new BlockPos(2,1,5)
    );

    private static HashMap<EntityType<? extends Unit>, Block> ENTITY_SKULL_MAP = new HashMap<>();

    static {
        ENTITY_SKULL_MAP.put(EntityRegistrar.ZOMBIE_UNIT.get(), Blocks.ZOMBIE_HEAD);
        ENTITY_SKULL_MAP.put(EntityRegistrar.DROWNED_UNIT.get(), BlockRegistrar.DROWNED_HEAD.get());
        ENTITY_SKULL_MAP.put(EntityRegistrar.HUSK_UNIT.get(), BlockRegistrar.HUSK_HEAD.get());
        ENTITY_SKULL_MAP.put(EntityRegistrar.SKELETON_UNIT.get(), Blocks.SKELETON_SKULL);
        ENTITY_SKULL_MAP.put(EntityRegistrar.STRAY_UNIT.get(), BlockRegistrar.STRAY_SKULL.get());
        ENTITY_SKULL_MAP.put(EntityRegistrar.BOGGED_UNIT.get(), BlockRegistrar.BOGGED_SKULL.get());
    }

    public GraveyardPlacement(Building building, Level level, BlockPos originPos, Rotation rotation, String ownerName, ArrayList<BuildingBlock> blocks, boolean isCapitol) {
        super(building, level, originPos, rotation, ownerName, blocks, isCapitol);
    }

    private EntityType<? extends Unit> getEntityType(Block block) {
        for (var entry : ENTITY_SKULL_MAP.entrySet()) {
            if (entry.getValue() == block) return entry.getKey();
        }
        return null;
    }

    private Block getSkullBlock(EntityType<? extends Mob> entityType) {
        if (ENTITY_SKULL_MAP.containsKey(entityType)) {
            return ENTITY_SKULL_MAP.get(entityType);
        }
        return null;
    }

    public int getTotalSkulls() {
        int totalSkulls = 0;
        for (BlockPos relativeBase : skullPoses) {
            BlockPos worldBase = relativeBase.offset(originPos);
            for (int y = 0; y < 4; y++) {
                if (level.getBlockState(worldBase.above(y)).getBlock() instanceof SkullBlock)
                    totalSkulls++;
                else
                    break;
            }
        }
        return totalSkulls;
    }

    public int getMaxSkulls() {
        if (this.level.isClientSide()) {
            return ResearchClient.hasResearch(ProductionItems.RESEARCH_MASS_BURIAL) ?
                    Graveyard.OVERFLOW_AMOUNT_UPGRADED : Graveyard.OVERFLOW_AMOUNT;
        } else {
            return ResearchServerEvents.playerHasResearch(this.ownerName, ProductionItems.RESEARCH_MASS_BURIAL) ?
                    Graveyard.OVERFLOW_AMOUNT_UPGRADED : Graveyard.OVERFLOW_AMOUNT;
        }
    }

    public void createSkull(EntityType<? extends Mob> entityType) {
        Block skullBlock = getSkullBlock(entityType);
        if (skullBlock == null) return;
        if (getTotalSkulls() >= getMaxSkulls()) return;

        // Find the slot with the lowest stack height and place there
        BlockPos skullPos = null;
        outerloop:
        for (int y = 1; y < 4; y++) {
            for (BlockPos relativeBase : skullPoses) {
                BlockPos worldBase = relativeBase.offset(originPos);
                if (!(level.getBlockState(worldBase.above(y)).getBlock() instanceof SkullBlock)) {
                    skullPos = worldBase.above(y);
                    break outerloop;
                }
            }
        }
        if (skullPos != null)
            level.setBlockAndUpdate(skullPos, skullBlock.defaultBlockState());
    }

    public void releaseNextUnit() {
        if (this.level.isClientSide()) return;

        // Cycle through skullPoses in reverse so release is LIFO relative to createSkull
        for (int i = skullPoses.size() - 1; i >= 0; i--) {
            BlockPos worldBase = skullPoses.get(i).offset(originPos);

            // Find the highest occupied skull in this slot
            BlockPos highestPos = null;
            Block highestBlock = null;
            for (int y = 1; y < 4; y++) {
                BlockPos candidate = worldBase.above(y);
                Block block = level.getBlockState(candidate).getBlock();
                if (block instanceof SkullBlock) {
                    highestPos = candidate;
                    highestBlock = block;
                } else if (level.getBlockState(candidate).isAir()) {
                    break; // Stack ends here
                }
            }

            if (highestPos != null) {
                EntityType<? extends Unit> entityType = getEntityType(highestBlock);
                level.removeBlock(highestPos, false);
                if (entityType != null) {
                    this.produceUnit((ServerLevel) level, entityType, this.ownerName, true);
                }
                return;
            }
        }

    }

    // for general building damage
    @Override
    public boolean canDestroyBlock(BlockPos relativeBp) {
        BlockPos worldBp = relativeBp.offset(this.originPos);
        Block block = this.getLevel().getBlockState(worldBp).getBlock();
        return !(block instanceof SkullBlock);
    }
}
