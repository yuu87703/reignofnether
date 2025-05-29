package com.solegendary.reignofnether.mixin;

import com.solegendary.reignofnether.building.BuildingUtils;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import com.solegendary.reignofnether.util.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {

    @Shadow private SpawnData nextSpawnData;
    @Shadow private int spawnDelay;
    @Shadow private void delay(Level pLevel, BlockPos pPos) { }
    @Shadow private SpawnData getOrCreateNextSpawnData(@Nullable Level pLevel, RandomSource pRandom, BlockPos pPos) { return null; }
    @Shadow private boolean isNearPlayer(Level pLevel, BlockPos pPos) { return true; }
    @Shadow private int spawnRange;

    private static final int SPAWN_COUNT = 3;
    private static final int MAX_NEARBY_ENTITIES = 1;
    private static final int SPAWN_DELAY = 600;

    @Inject(
            method = "delay",
            at = @At("HEAD")
    )
    private void delayNoRandom(Level pLevel, BlockPos pPos, CallbackInfo ci) {
        // avoid using the 10-40 randomised delay
        spawnDelay = SPAWN_DELAY;
    }

    @Inject(
            method = "isNearPlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    public void isNearPlayer(Level pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
        if (reignofnether$isValidNeutralUnitSpawner(pLevel, pPos))
            cir.setReturnValue(true);
    }

    @Unique
    private boolean reignofnether$isValidNeutralUnitSpawner(Level level, BlockPos pPos) {
        return !(BuildingUtils.isPosInsideAnyBuilding(level.isClientSide(), pPos) &&
                nextSpawnData.getEntityToSpawn().getAsString().contains("reignofnether"));
    }

    @Inject(
            method = "serverTick",
            at = @At("HEAD"),
            cancellable = true
    )
    public void serverTick(ServerLevel pServerLevel, BlockPos pPos, CallbackInfo ci) {
        if (reignofnether$isValidNeutralUnitSpawner(pServerLevel, pPos)) {
            ci.cancel();

            if (this.spawnDelay == -1) {
                this.delay(pServerLevel, pPos);
            }

            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            } else {
                boolean flag = false;
                RandomSource randomsource = pServerLevel.getRandom();
                SpawnData spawndata = this.getOrCreateNextSpawnData(pServerLevel, randomsource, pPos);
                int i = 0;

                while (true) {
                    if (i >= SPAWN_COUNT) {
                        if (flag) {
                            this.delay(pServerLevel, pPos);
                        }
                        break;
                    }

                    CompoundTag compoundtag = spawndata.getEntityToSpawn();
                    Optional<EntityType<?>> optional = EntityType.by(compoundtag);
                    if (optional.isEmpty()) {
                        this.delay(pServerLevel, pPos);
                        return;
                    }

                    ListTag listtag = compoundtag.getList("Pos", 6);
                    int j = listtag.size();
                    double d0 = j >= 1 ? listtag.getDouble(0) : (double) pPos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) this.spawnRange + 0.5;
                    double d1 = j >= 2 ? listtag.getDouble(1) : (double) (pPos.getY() + randomsource.nextInt(3) - 1);
                    double d2 = j >= 3 ? listtag.getDouble(2) : (double) pPos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) this.spawnRange + 0.5;
                    if (pServerLevel.noCollision(((EntityType) optional.get()).getAABB(d0, d1, d2))) {
                        label105:
                        {
                            BlockPos blockpos = BlockPos.containing(d0, d1, d2);
                            if (spawndata.getCustomSpawnRules().isPresent()) {
                                if (!((EntityType) optional.get()).getCategory().isFriendly() && pServerLevel.getDifficulty() == Difficulty.PEACEFUL) {
                                    break label105;
                                }

                                //SpawnData.CustomSpawnRules spawndata$customspawnrules = (SpawnData.CustomSpawnRules) spawndata.getCustomSpawnRules().get();
                                //if (!spawndata$customspawnrules.blockLightLimit().isValueInRange(pServerLevel.getBrightness(LightLayer.BLOCK, blockpos)) || !spawndata$customspawnrules.skyLightLimit().isValueInRange(pServerLevel.getBrightness(LightLayer.SKY, blockpos))) {
                                //    break label105;
                                //}
                            } else if (!SpawnPlacements.checkSpawnRules((EntityType) optional.get(), pServerLevel, MobSpawnType.SPAWNER, blockpos, pServerLevel.getRandom())) {
                                break label105;
                            }

                            Entity entity = EntityType.loadEntityRecursive(compoundtag, pServerLevel, (p_151310_) -> {
                                p_151310_.moveTo(d0, d1, d2, p_151310_.getYRot(), p_151310_.getXRot());
                                return p_151310_;
                            });
                            if (entity == null) {
                                this.delay(pServerLevel, pPos);
                                return;
                            }

                            int k = pServerLevel.getEntitiesOfClass(entity.getClass(), (new AABB(pPos.getX(), pPos.getY(), pPos.getZ(), pPos.getX() + 1, pPos.getY() + 1, pPos.getZ() + 1))
                                    .inflate(Unit.ANCHOR_RETREAT_RANGE)).size();
                            if (k >= MAX_NEARBY_ENTITIES) {
                                this.delay(pServerLevel, pPos);
                                return;
                            }

                            entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), randomsource.nextFloat() * 360.0F, 0.0F);
                            if (entity instanceof Mob) {
                                Mob mob = (Mob) entity;

                                // if the mob is classed as a monster, this will check for light levels
                                if (entity instanceof Unit unit && unit.getFaction() == Faction.MONSTERS) {
                                    if (!ForgeEventFactory.checkSpawnPositionSpawner(mob, pServerLevel, MobSpawnType.SPAWNER, spawndata, (BaseSpawner)(Object)this)) {
                                        break label105;
                                    }
                                }
                                MobSpawnEvent.FinalizeSpawn event = ForgeEventFactory.onFinalizeSpawnSpawner(mob, pServerLevel, pServerLevel.getCurrentDifficultyAt(entity.blockPosition()), (SpawnGroupData) null, compoundtag, (BaseSpawner)(Object)this);
                                if (event != null && spawndata.getEntityToSpawn().size() == 1 && spawndata.getEntityToSpawn().contains("id", 8)) {
                                    ((Mob) entity).finalizeSpawn(pServerLevel, event.getDifficulty(), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());
                                    if (entity instanceof Unit unit)
                                        unit.setAnchor(entity.getOnPos());
                                }
                            }

                            if (!pServerLevel.tryAddFreshEntityWithPassengers(entity)) {
                                this.delay(pServerLevel, pPos);
                                return;
                            }

                            pServerLevel.levelEvent(2004, pPos, 0);
                            pServerLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);
                            if (entity instanceof Mob) {
                                ((Mob) entity).spawnAnim();
                            }

                            flag = true;
                        }
                    }

                    ++i;
                }
            }
        }
    }
}
