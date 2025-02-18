package com.solegendary.reignofnether.startpos;

import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.survival.SurvivalClientEvents;
import com.solegendary.reignofnether.survival.SurvivalSyncAction;
import com.solegendary.reignofnether.survival.WaveDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class StartPosClientboundPacket {

    StartPosAction action;
    BlockPos blockPos;
    int colorId;

    public static void addPos(BlockPos pos, int colorId) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                new StartPosClientboundPacket(StartPosAction.ADD, pos, colorId));
    }

    public static void removePos(BlockPos pos) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                new StartPosClientboundPacket(StartPosAction.REMOVE, pos, 0));
    }

    public static void reservePos(BlockPos pos) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                new StartPosClientboundPacket(StartPosAction.RESERVE, pos, 0));
    }

    public static void unreservePos(BlockPos pos) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                new StartPosClientboundPacket(StartPosAction.UNRESERVE, pos, 0));
    }

    public StartPosClientboundPacket(StartPosAction action, BlockPos blockPos, int colorId) {
        this.action = action;
        this.blockPos = blockPos;
        this.colorId = colorId;
    }

    public StartPosClientboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(StartPosAction.class);
        this.blockPos = buffer.readBlockPos();
        this.colorId = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeBlockPos(this.blockPos);
        buffer.writeInt(this.colorId);
    }

    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);

        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        switch (action) {
                            case ADD -> {
                                StartPosClient.startPoses.removeIf(sp -> sp.pos.equals(blockPos));
                                StartPosClient.startPoses.add(new StartPos(blockPos, colorId));
                            }
                            case REMOVE -> {
                                StartPosClient.startPoses.removeIf(sp -> sp.pos.equals(blockPos));
                            }
                            case RESERVE -> {
                                for (StartPos startPos : StartPosClient.startPoses) {
                                    if (startPos.pos.equals(blockPos)) {
                                        startPos.reserved = true;
                                        break;
                                    }
                                }
                            }
                            case UNRESERVE -> {
                                for (StartPos startPos : StartPosClient.startPoses) {
                                    if (startPos.pos.equals(blockPos)) {
                                        startPos.reserved = false;
                                        break;
                                    }
                                }
                            }
                        }
                        success.set(true);
                    });
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
