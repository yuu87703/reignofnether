package com.solegendary.reignofnether.hero;

import com.solegendary.reignofnether.unit.interfaces.HeroUnit;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class HeroClientEvents {

    public static ArrayList<HeroUnit> fallenHeroes = new ArrayList<>();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent evt) {
        // save killed hero unit for revival
        if (evt.getEntity() instanceof HeroUnit heroUnit) {
            fallenHeroes.add(heroUnit);
        }
    }
}
