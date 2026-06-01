package com.solegendary.reignofnether.startpos;

import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.hud.ButtonBuilder;
import com.solegendary.reignofnether.player.PlayerColors;
import com.solegendary.reignofnether.util.MiscUtil;
import com.solegendary.reignofnether.util.MyRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.reignofnether.util.MiscUtil.fcs;

public class StartPos {
    public BlockPos pos;
    public Faction faction = Faction.NONE; // if != NONE, is reserved by a player
    public String playerName = ""; // name of player who has reserved this spot
    public int colorId;
    public boolean isFromStartBlock = true;
    public boolean enabled = true;
    public boolean ready = false; // player on this spot is ready to start the game

    public StartPos(BlockPos pos, int colorId) {
        this.pos = pos;
        this.colorId = colorId;
    }

    public StartPos(BlockPos pos, Faction faction, String playerName, int colorId) {
        this.pos = pos;
        this.faction = faction;
        this.playerName = playerName;
        this.colorId = colorId;
    }

    public void reset() {
        this.faction = Faction.NONE;
        this.playerName = "";
        this.ready = false;
    }

    public ResourceLocation getIcon() {
             if (colorId == MapColor.COLOR_MAGENTA.id     || colorId == 0xBD44B3) return getIcon("magenta");
        else if (colorId == MapColor.COLOR_LIGHT_BLUE.id  || colorId == 0x3AAFD9) return getIcon("light_blue");
        else if (colorId == MapColor.COLOR_ORANGE.id      || colorId == 0xF07613) return getIcon("orange");
        else if (colorId == MapColor.COLOR_YELLOW.id      || colorId == 0xF8C627) return getIcon("yellow");
        else if (colorId == MapColor.COLOR_LIGHT_GREEN.id || colorId == 0x70B919) return getIcon("lime");
        else if (colorId == MapColor.COLOR_PINK.id        || colorId == 0xED8DAC) return getIcon("pink");
        else if (colorId == MapColor.COLOR_GRAY.id        || colorId == 0x3E4447) return getIcon("gray");
        else if (colorId == MapColor.COLOR_LIGHT_GRAY.id  || colorId == 0x8E8E86) return getIcon("light_gray");
        else if (colorId == MapColor.COLOR_CYAN.id        || colorId == 0x158991) return getIcon("cyan");
        else if (colorId == MapColor.COLOR_PURPLE.id      || colorId == 0x792AAC) return getIcon("purple");
        else if (colorId == MapColor.COLOR_BLUE.id        || colorId == 0x35399D) return getIcon("blue");
        else if (colorId == MapColor.COLOR_BROWN.id       || colorId == 0x724728) return getIcon("brown");
        else if (colorId == MapColor.COLOR_GREEN.id       || colorId == 0x546D1B) return getIcon("green");
        else if (colorId == MapColor.COLOR_RED.id         || colorId == 0xA12722) return getIcon("red");
        else if (colorId == MapColor.COLOR_BLACK.id       || colorId == 0x141519) return getIcon("black");

        else return getIcon("white");
    }

    private ResourceLocation getIcon(String colorName) {
        return ResourceLocation.fromNamespaceAndPath("reignofnether", "textures/block/rts_start_block_" + colorName + ".png");
    }

    public Button getButton(String localPlayerName) {
        ArrayList<FormattedCharSequence> fcsList = new ArrayList<>();

        if (enabled) {
            if (playerName.isBlank()) {
                fcsList.add(fcs(I18n.get("startpos.reignofnether.not_reserved"), true));
                fcsList.add(fcs(I18n.get("startpos.reignofnether.disable")));
            } else {
                fcsList.add(fcs(I18n.get("startpos.reignofnether.reserved", playerName), true));
            }
        } else {
            fcsList.add(fcs(I18n.get("startpos.reignofnether.disabled"), true));
            fcsList.add(fcs(I18n.get("startpos.reignofnether.enable")));
        }

        return new ButtonBuilder("Reserve Pos")
                .iconResource(enabled ? getIcon() : null)
                .isSelected(() -> StartPosClientEvents.getPos() == this)
                .isEnabled(() -> playerName.isBlank() || localPlayerName.equals(playerName))
                .onLeftClick(() -> {
                    if (!enabled || !playerName.isBlank())
                        return;
                    if (StartPosClientEvents.getPos() == this)
                        StartPosServerboundPacket.unreservePos(pos);
                    else
                        StartPosServerboundPacket.reservePos(pos, StartPosClientEvents.selectedFaction, localPlayerName);
                })
                .onRightClick(() -> {
                    if (!playerName.isBlank())
                        return;
                    if (enabled)
                        StartPosServerboundPacket.disablePos(pos);
                    else
                        StartPosServerboundPacket.enablePos(pos);
                })
                .tooltipLines(fcsList)
                .iconSize(8)
                .build();
    }
}
