package com.solegendary.reignofnether.building.production;

import com.solegendary.reignofnether.building.BuildingServerboundPacket;
import com.solegendary.reignofnether.building.buildings.placements.ProductionPlacement;
import com.solegendary.reignofnether.hero.HeroClientEvents;
import com.solegendary.reignofnether.hero.HeroServerEvents;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.research.ResearchClient;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.resources.Resources;
import com.solegendary.reignofnether.resources.ResourcesServerEvents;
import com.solegendary.reignofnether.unit.HeroUnitSave;
import com.solegendary.reignofnether.unit.interfaces.HeroUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.solegendary.reignofnether.util.MiscUtil.fcs;

// units and/or research tech that a ProductionBuilding can produce
public abstract class ReviveHeroProductionItem extends ProductionItem {

    private final ResourceLocation iconRl;
    private final String tooltipI18n;

    public ReviveHeroProductionItem(ResourceLocation iconRl, String tooltipI18n) {
        super(ResourceCost.Unit(0,0,0,0,0));
        this.iconRl = iconRl;
        this.tooltipI18n = tooltipI18n;
        this.onComplete = (Level level, ProductionPlacement placement) -> {
            if (!level.isClientSide()) {
                HeroUnitSave oldHero = getFallenHero(false, placement.ownerName);
                EntityType<? extends HeroUnit> entityType = getHeroEntityType();
                Entity entity = null;
                if (entityType != null)
                    entity = placement.produceUnit((ServerLevel) level, entityType, placement.ownerName, true);
                if (entity instanceof HeroUnit newHero && oldHero != null) {
                    newHero.setExperience(oldHero.experience);
                    newHero.setSkillPoints(oldHero.skillPoints);
                    newHero.setStatsForLevel();
                    if (newHero.getHeroAbilities().size() > 0)
                        newHero.getHeroAbilities().get(0).rank = oldHero.ability1Rank;
                    if (newHero.getHeroAbilities().size() > 1)
                        newHero.getHeroAbilities().get(1).rank = oldHero.ability2Rank;
                    if (newHero.getHeroAbilities().size() > 2)
                        newHero.getHeroAbilities().get(2).rank = oldHero.ability3Rank;
                    if (newHero.getHeroAbilities().size() > 3)
                        newHero.getHeroAbilities().get(3).rank = oldHero.ability4Rank;
                }
                HeroServerEvents.fallenHeroes.remove(oldHero);
            } else {
                HeroUnitSave oldHero = getFallenHero(true, placement.ownerName);
                HeroClientEvents.fallenHeroes.remove(oldHero);
            }
        };
    }

    // can't make this a member as we can't refer to other registered objects at init time
    protected EntityType<? extends HeroUnit> getHeroEntityType() {
        return null;
    }

    private String getTooltip(String ownerName) {
        HeroUnitSave heroSave = getFallenHero(true, ownerName);
        if (heroSave != null) {
            return I18n.get(tooltipI18n, HeroUnit.getHeroLevel(heroSave.experience));
        }
        return I18n.get(tooltipI18n);
    }

    @Nullable
    private HeroUnitSave getFallenHero(boolean isClientSide, String ownerName) {
        ArrayList<HeroUnitSave> heroUnits = isClientSide ? HeroClientEvents.fallenHeroes : HeroServerEvents.fallenHeroes;
        for (HeroUnitSave heroUnit : heroUnits) {
            if (heroUnit.ownerName.equals(ownerName) && heroUnit.name.equals(getHeroEntityType().getDescriptionId()))
                return heroUnit;
        }
        return null;
    }

    private ResourceCost getReviveCost(boolean isClientSide, String ownerName) {
        ArrayList<HeroUnitSave> heroSaves = isClientSide ? HeroClientEvents.fallenHeroes : HeroServerEvents.fallenHeroes;
        HeroUnitSave heroSave = getFallenHero(isClientSide, ownerName);
        if (heroSave != null)
            return HeroUnit.getReviveCost(HeroUnit.getHeroLevel(heroSave.experience));
        return HeroUnit.getReviveCost(1);
    }

    public Button getStartButton(ProductionPlacement prodBuilding, Keybinding hotkey) {
        return new Button(
                itemName,
                14,
                iconRl,
                hotkey,
                () -> false,
                () -> this.itemIsBeingProduced(prodBuilding.ownerName) ||
                        ResearchClient.hasResearch(this) ||
                        getFallenHero(true, prodBuilding.ownerName) == null,
                () -> true,
                () -> BuildingServerboundPacket.startProduction(prodBuilding.originPos, this),
                null,
                List.of(
                        fcs(getTooltip(prodBuilding.ownerName)),
                        ResourceCosts.getFormattedCost(getReviveCost(prodBuilding.level.isClientSide(), prodBuilding.ownerName)),
                        ResourceCosts.getFormattedPopAndTime(getReviveCost(prodBuilding.level.isClientSide(), prodBuilding.ownerName))
                )
        );
    }

    @Override
    public boolean canAfford(Level level, String ownerName) {
        for (Resources resources : ResourcesServerEvents.resourcesList)
            if (resources.ownerName.equals(ownerName))
                return (resources.food >= getReviveCost(level.isClientSide(), ownerName).food &&
                        resources.wood >= getReviveCost(level.isClientSide(), ownerName).wood &&
                        resources.ore >= getReviveCost(level.isClientSide(), ownerName).ore &&
                        canAffordPopulation(level, ownerName));
        return false;
    }

    public Button getCancelButton(ProductionPlacement prodBuilding, boolean first) {
        return new Button(
                itemName,
                14,
                iconRl,
                (Keybinding) null,
                () -> false,
                () -> false,
                () -> true,
                () -> BuildingServerboundPacket.cancelProduction(prodBuilding.originPos, this, first),
                null,
                null
        );
    }
}
