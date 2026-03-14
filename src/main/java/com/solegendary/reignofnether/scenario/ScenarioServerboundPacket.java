package com.solegendary.reignofnether.scenario;

import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.BuildingServerEvents;
import com.solegendary.reignofnether.building.BuildingUtils;
import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.resources.ResourceName;
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
    public int roleIndex;
    public int x;
    public int y;
    public int z;
    public boolean boolValue;
    public int intValue;
    public String strValue;

    public static void setUnitRole(int roleIndex, int unitId) {
        PacketHandler.INSTANCE.sendToServer(new ScenarioServerboundPacket(ScenarioAction.SET_UNIT_ROLE, roleIndex, unitId,0,0, false, 0, ""));
    }

    public static void setBuildingRole(int roleIndex, BlockPos bp) {
        PacketHandler.INSTANCE.sendToServer(new ScenarioServerboundPacket(ScenarioAction.SET_BUILDING_ROLE, roleIndex, bp.getX(), bp.getY(), bp.getZ(), false, 0, ""));
    }

    public static void setStartingResources(int roleIndex, ResourceName resName, int amount) {
        ScenarioAction scenarioAction = switch (resName) {
            case FOOD -> ScenarioAction.SET_ROLE_STARTING_FOOD;
            case WOOD -> ScenarioAction.SET_ROLE_STARTING_WOOD;
            case ORE -> ScenarioAction.SET_ROLE_STARTING_ORE;
            case NONE -> null;
        };
        if (scenarioAction != null)
            PacketHandler.INSTANCE.sendToServer(new ScenarioServerboundPacket(scenarioAction, roleIndex, 0,0,0, false, amount, ""));
    }

    public static void setTeamNumber(int roleIndex, int teamNumber) {
        PacketHandler.INSTANCE.sendToServer(new ScenarioServerboundPacket(ScenarioAction.SET_ROLE_TEAM_NUMBER, roleIndex, 0,0,0, false, teamNumber, ""));
    }

    public static void setRoleFaction(int roleIndex, Faction faction) {
        ScenarioAction scenarioAction = switch (faction) {
            case VILLAGERS -> ScenarioAction.SET_ROLE_FACTION_VILLAGER;
            case MONSTERS -> ScenarioAction.SET_ROLE_FACTION_MONSTER;
            case PIGLINS -> ScenarioAction.SET_ROLE_FACTION_PIGLIN;
            case NONE, NEUTRAL -> ScenarioAction.SET_ROLE_FACTION_NEUTRAL;
        };
        PacketHandler.INSTANCE.sendToServer(new ScenarioServerboundPacket(scenarioAction, roleIndex, 0,0,0, false, 0, ""));
    }

    public static void setRoleIsNpc(int roleIndex, boolean isNpc) {
        PacketHandler.INSTANCE.sendToServer(new ScenarioServerboundPacket(ScenarioAction.SET_ROLE_NPC, roleIndex, 0,0,0, isNpc, 0, ""));
    }

    public static void setRoleName(int roleIndex, String name) {
        PacketHandler.INSTANCE.sendToServer(new ScenarioServerboundPacket(ScenarioAction.SET_ROLE_NAME, roleIndex, 0,0,0, false, 0, name));
    }

    public ScenarioServerboundPacket(ScenarioAction action, int roleIndex, int x, int y, int z,
                                   boolean boolValue, int intValue, String strValue) {
        this.action = action;
        this.roleIndex = roleIndex;
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


            ScenarioRole role = ScenarioServerEvents.getScenarioRole(roleIndex);
            if (role == null && this.action != ScenarioAction.SET_SCENARIO_NAME) {
                return;
            }

            switch (this.action) {
                case SET_ROLE_STARTING_FOOD -> role.startingResources.food = intValue;
                case SET_ROLE_STARTING_WOOD -> role.startingResources.wood = intValue;
                case SET_ROLE_STARTING_ORE -> role.startingResources.ore = intValue;
                case SET_ROLE_FACTION_VILLAGER -> role.faction = Faction.VILLAGERS;
                case SET_ROLE_FACTION_MONSTER -> role.faction = Faction.MONSTERS;
                case SET_ROLE_FACTION_PIGLIN -> role.faction = Faction.PIGLINS;
                case SET_ROLE_FACTION_NEUTRAL -> role.faction = Faction.NEUTRAL;
                case SET_ROLE_NAME -> role.name = strValue;
                case SET_ROLE_TEAM_NUMBER -> role.teamNumber = intValue;
                case SET_ROLE_NPC -> role.isNpc = boolValue;
                case SET_UNIT_ROLE -> {
                    for (LivingEntity le : UnitServerEvents.getAllUnits())
                        if (le instanceof Unit unit && le.getId() == intValue)
                            unit.setScenarioRoleIndex(intValue);
                }
                case SET_BUILDING_ROLE -> {
                    BuildingPlacement buildingPlacement = BuildingUtils.findBuilding(false, new BlockPos(x,y,z));
                    if (buildingPlacement != null)
                        buildingPlacement.scenarioRoleIndex = roleIndex;
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
