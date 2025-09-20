package com.solegendary.reignofnether.resources;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ResourcesServerboundPacket {

    // pos is used to identify the building object serverside
    ResourcesAction action;
    public String senderName;
    public String receiverName;
    public int food;
    public int wood;
    public int ore;

    public static void sendResources(Resources resources, String receiverName) {
        PacketHandler.INSTANCE.sendToServer(new ResourcesServerboundPacket(
                ResourcesAction.SEND_RESOURCES,
                resources.ownerName,
                receiverName,
                resources.food,
                resources.wood,
                resources.ore
        ));
    }

    public ResourcesServerboundPacket(ResourcesAction action, String senderName, String receiverName, int food, int wood, int ore) {
        this.action = action;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.food = food;
        this.wood = wood;
        this.ore = ore;
    }

    public ResourcesServerboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(ResourcesAction.class);
        this.senderName = buffer.readUtf();
        this.receiverName = buffer.readUtf();
        this.food = buffer.readInt();
        this.wood = buffer.readInt();
        this.ore = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeUtf(this.senderName);
        buffer.writeUtf(this.receiverName);
        buffer.writeInt(this.food);
        buffer.writeInt(this.wood);
        buffer.writeInt(this.ore);
    }

    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                ReignOfNether.LOGGER.warn("AbilityServerboundPacket: Sender was null");
                success.set(false);
                return;
            }
            if (!player.getName().getString().equals(senderName)) {
                ReignOfNether.LOGGER.warn("AbilityServerboundPacket: Tried to process packet from " + player.getName() + " for: " + senderName);
                success.set(false);
                return;
            }
            if (action == ResourcesAction.SEND_RESOURCES) {
                ResourcesServerEvents.trySendingAnyResources(this.receiverName, new Resources(this.senderName, this.food, this.wood, this.ore));
            }
            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
