package com.solegendary.reignofnether.startpos;

import com.solegendary.reignofnether.blocks.RTSStartBlock;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class StartPosServerEvents {

    public static final int MAX_START_POSES = 16;

    public static ArrayList<StartPos> startPoses = new ArrayList<>();

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent evt) {
        if (evt.getPlacedBlock().getBlock() instanceof RTSStartBlock rtsStartBlock) {
            if (startPoses.size() < MAX_START_POSES) {
                startPoses.add(new StartPos(evt.getPos(), rtsStartBlock.defaultMaterialColor().id));
                StartPosClientboundPacket.addPos(evt.getPos(), rtsStartBlock.defaultMaterialColor().id);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent evt) {
        startPoses.removeIf(sp -> {
            if (sp.pos.equals(evt.getPos())) {
                StartPosClientboundPacket.removePos(evt.getPos());
                return true;
            }
            return false;
        });
    }
}
