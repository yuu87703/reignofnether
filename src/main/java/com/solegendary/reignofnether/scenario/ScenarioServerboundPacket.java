package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.BuildingServerEvents;
import com.solegendary.reignofnether.building.BuildingUtils;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.sandbox.SandboxServer;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ScenarioServerboundPacket {

    public ScenarioAction action;
    public int x;
    public int y;
    public int z;
    public boolean boolValue;
    public int intValue;
    public String strValue;

    public static void setUnitRole(int unitId, String roleIndex) {
        PacketHandler.INSTANCE.sendToServer(new ScenarioServerboundPacket(ScenarioAction.SET_UNIT_ROLE, unitId,0,0, false, 0, ""));
    }

    public ScenarioServerboundPacket(ScenarioAction action, int x, int y, int z,
                                   boolean boolValue, int intValue, String strValue) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.boolValue = boolValue;
        this.intValue = intValue;
        this.strValue = strValue;
    }

    public ScenarioServerboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(ScenarioAction.class);
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.boolValue = buffer.readBoolean();
        this.intValue = buffer.readInt();
        this.strValue = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeBoolean(this.boolValue);
        buffer.writeInt(this.intValue);
        buffer.writeUtf(this.strValue);
    }

    // server-side packet-consuming functions
    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            if (!SandboxServer.isAnyoneASandboxPlayer())
                return;

            Unit unit = null;
            for (LivingEntity le : UnitServerEvents.getAllUnits())
                if (le instanceof Unit unit1 && le.getId() == x)
                    unit = unit1;
            BuildingPlacement buildingPlacement = BuildingUtils.findBuilding(false, new BlockPos(x,y,z));

            switch (this.action) {
                case SET_ROLE_STARTING_FOOD -> {
                }
                case SET_ROLE_STARTING_WOOD -> {
                }
                case SET_ROLE_STARTING_ORE -> {
                }
                case SET_ROLE_NAME -> {
                }
                case SET_ROLE_FACTION -> {
                }
                case SET_ROLE_TEAM_NUMBER -> {
                }
                case SET_UNIT_ROLE -> {
                }
                case SET_BUILDING_ROLE -> {
                }
                case SET_SCENARIO_NAME -> {
                }
            }
            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
