package com.solegendary.reignofnether.hero;

import com.solegendary.reignofnether.unit.HeroUnitSave;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class HeroClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    public static ArrayList<HeroUnitSave> fallenHeroes = new ArrayList<>();

    public static void addFallenHero(HeroUnitSave heroUnitSave) {
        if (MC.player != null && heroUnitSave.ownerName.equals(MC.player.getName().getString()))
            fallenHeroes.add(heroUnitSave);
    }
}
