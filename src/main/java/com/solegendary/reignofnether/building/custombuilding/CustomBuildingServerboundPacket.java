package com.solegendary.reignofnether.building.custombuilding;

import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.sandbox.SandboxServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.solegendary.reignofnether.building.BuildingUtils.findBuilding;

public class CustomBuildingServerboundPacket {

    public CustomBuildingAction action;
    public BlockPos buildingPos;
    public boolean boolValue;
    public int intValue;
    public String strValue;

    public static void deregisterBuilding(String buildingName) {
        PacketHandler.INSTANCE.sendToServer(new CustomBuildingServerboundPacket(CustomBuildingAction.DEREGISTER, new BlockPos(0,0,0), false, 0, buildingName));
    }

    public static void customiseBuilding(CustomBuildingAction action, BlockPos pos, boolean boolValue) {
        PacketHandler.INSTANCE.sendToServer(new CustomBuildingServerboundPacket(action, pos, boolValue, 0, ""));
    }

    public static void customiseBuilding(CustomBuildingAction action, BlockPos pos, int intValue) {
        PacketHandler.INSTANCE.sendToServer(new CustomBuildingServerboundPacket(action, pos, false, intValue, ""));
    }

    public static void customiseBuilding(CustomBuildingAction action, BlockPos pos, String strValue) {
        PacketHandler.INSTANCE.sendToServer(new CustomBuildingServerboundPacket(action, pos, false, 0, strValue));
    }

    public CustomBuildingServerboundPacket(CustomBuildingAction action,
                                           BlockPos buildingPos,
                                           boolean boolValue,
                                           int intValue,
                                           String strValue) {
        this.action = action;
        this.buildingPos = buildingPos;
        this.boolValue = boolValue;
        this.intValue = intValue;
        this.strValue = strValue;
    }

    public CustomBuildingServerboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(CustomBuildingAction.class);
        this.buildingPos = buffer.readBlockPos();
        this.boolValue = buffer.readBoolean();
        this.intValue = buffer.readInt();
        this.strValue = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeBlockPos(this.buildingPos);
        buffer.writeBoolean(this.boolValue);
        buffer.writeInt(this.intValue);
        buffer.writeUtf(this.strValue);
    }

    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            BuildingPlacement building = findBuilding(false, this.buildingPos);
            if (building == null || !SandboxServer.isAnyoneASandboxPlayer())
                return;

            switch (this.action) {
                case DEREGISTER -> CustomBuildingServerEvents.deregisterCustomBuilding(this.strValue);
            }
            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
