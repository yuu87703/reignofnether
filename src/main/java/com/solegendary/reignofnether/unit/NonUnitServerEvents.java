package com.solegendary.reignofnether.unit;

import com.mojang.datafixers.util.Pair;
import com.solegendary.reignofnether.research.ResearchServerEvents;
import com.solegendary.reignofnether.sandbox.SandboxServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NonUnitServerEvents {

    // list of units to cancel specific goals for so they don't interfere with player commands
    public static final List<PathfinderMob> attackSuppressedNonUnits = Collections.synchronizedList(new ArrayList<>());
    public static final List<PathfinderMob> moveSuppressedNonUnits = Collections.synchronizedList(new ArrayList<>());

    public static final List<Pair<PathfinderMob, BlockPos>> nonUnitMoveTargets = Collections.synchronizedList(new ArrayList<>());

    public static boolean canControlNonUnits(Level level, String playerName) {
        return SandboxServer.isSandboxPlayer(playerName) || ResearchServerEvents.playerHasCheat(playerName, "wouldyoukindly");
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END || evt.level.isClientSide() || evt.level.dimension() != Level.OVERWORLD) {
            return;
        }

        synchronized (nonUnitMoveTargets) {
            nonUnitMoveTargets.removeIf(pair -> {
                PathfinderMob mob = pair.getFirst();
                BlockPos finalTargetBp = pair.getSecond();
                Path navPath = mob.getNavigation().getPath();
                if (mob.distanceToSqr(finalTargetBp.getCenter()) < 4) {
                    return true;
                } else if (mob.tickCount % 20 == 0 && navPath == null ||
                    (navPath != null && navPath.getEndNode() != null && mob.distanceToSqr(navPath.getEndNode().asBlockPos().getCenter()) < 4)) {
                    Path path = mob.getNavigation().createPath(finalTargetBp.getX(), finalTargetBp.getY(), finalTargetBp.getZ(), 0);
                    mob.getNavigation().moveTo(path, 1);
                }
                return false;
            });
        }
        synchronized (attackSuppressedNonUnits) {
            attackSuppressedNonUnits.removeIf(mob -> (mob.isDeadOrDying() || mob.isRemoved() || (mob.getNavigation().isDone() && mob.getTarget() == null)));
        }
        synchronized (moveSuppressedNonUnits) {
            moveSuppressedNonUnits.removeIf(mob -> (mob.isDeadOrDying() || mob.isRemoved() || (mob.getNavigation().isDone() && mob.getTarget() == null)));
        }
    }
}
