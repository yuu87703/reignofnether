package com.solegendary.reignofnether.startpos;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.research.researchItems.ResearchAdvancedPortals;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static com.solegendary.reignofnether.util.MiscUtil.fcs;

public class StartPosClient {

    public static ArrayList<StartPos> startPoses = new ArrayList<>();
    public static int startBlockIndex = 0;
    public static boolean reserved = false;

    public static Button getButton() {
        return new Button(ResearchAdvancedPortals.itemName,
            14,
            new ResourceLocation(ReignOfNether.MOD_ID, "textures/icons/blocks/portal.png"),
            new ResourceLocation(ReignOfNether.MOD_ID, "textures/hud/icon_frame_bronze.png"),
            Keybindings.stop,
            () -> false,
            () -> startPoses.isEmpty(),
            () -> true,
            StartPosClient::cycleStartBlock,
            null,
            List.of(
                fcs(I18n.get("research.reignofnether.advanced_portals"), true)
            )
        );
    }

    private static void cycleStartBlock() {
        startBlockIndex += 1;
        if (startBlockIndex >= startPoses.size())
            startBlockIndex = 0;
    }
}
