package com.solegendary.reignofnether.unit.packets;

import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.unit.UnitClientEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UnitSyncAttributeClientboundPacket {

    private final int entityId;
    private final String attrDesc;
    private final double attrValue;

    // TODO: find a good place to call this from...
    public static void updateAttribute(LivingEntity entity, AttributeInstance ai) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                new UnitSyncAttributeClientboundPacket(entity.getId(), ai.getAttribute().getDescriptionId(), ai.getBaseValue())
        );
    }

    // packet-handler functions
    public UnitSyncAttributeClientboundPacket(
        int entityId,
        String attrDesc,
        double attrValue
    ) {
        this.entityId = entityId;
        this.attrDesc = attrDesc;
        this.attrValue = attrValue;
    }

    public UnitSyncAttributeClientboundPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.attrDesc = buffer.readUtf();
        this.attrValue = buffer.readDouble();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeUtf(this.attrDesc);
        buffer.writeDouble(this.attrValue);
    }

    // client-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);

        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> UnitClientEvents.syncUnitAttribute(this.entityId, this.attrDesc, this.attrValue));
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
