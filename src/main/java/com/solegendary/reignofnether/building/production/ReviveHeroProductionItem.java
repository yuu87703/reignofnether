package com.solegendary.reignofnether.building.production;

import com.solegendary.reignofnether.building.BuildingServerboundPacket;
import com.solegendary.reignofnether.building.buildings.placements.ProductionPlacement;
import com.solegendary.reignofnether.hero.HeroClientEvents;
import com.solegendary.reignofnether.hero.HeroClientboundPacket;
import com.solegendary.reignofnether.hero.HeroServerEvents;
import com.solegendary.reignofnether.hud.Button;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
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
                HeroUnitSave oldHero = HeroUnit.getFallenHero(false, placement.ownerName, getHeroEntityType().getDescriptionId());
                EntityType<? extends HeroUnit> entityType = getHeroEntityType();
                Entity entity = null;
                if (entityType != null)
                    entity = placement.produceUnit((ServerLevel) level, entityType, placement.ownerName,
                            getHeroEntityType() != EntityRegistrar.PIGLIN_MERCHANT_UNIT.get());
                if (entity instanceof HeroUnit newHero && oldHero != null) {
                    newHero.setExperience(oldHero.experience);
                    HeroClientboundPacket.setExperience(entity.getId(), oldHero.experience);
                    newHero.setSkillPoints(oldHero.skillPoints);
                    HeroClientboundPacket.setSkillPoints(entity.getId(), oldHero.skillPoints);
                    newHero.setStatsForLevel(true);
                    if (newHero.getHeroAbilities().size() > 0) {
                        newHero.getHeroAbilities().get(0).rank = oldHero.ability1Rank;
                        HeroClientboundPacket.setAbilityRank(entity.getId(), oldHero.ability1Rank, 0);
                    } if (newHero.getHeroAbilities().size() > 1) {
                        newHero.getHeroAbilities().get(1).rank = oldHero.ability2Rank;
                        HeroClientboundPacket.setAbilityRank(entity.getId(), oldHero.ability2Rank, 1);
                    } if (newHero.getHeroAbilities().size() > 2) {
                        newHero.getHeroAbilities().get(2).rank = oldHero.ability3Rank;
                        HeroClientboundPacket.setAbilityRank(entity.getId(), oldHero.ability3Rank, 2);
                    } if (newHero.getHeroAbilities().size() > 3) {
                        newHero.getHeroAbilities().get(3).rank = oldHero.ability4Rank;
                        HeroClientboundPacket.setAbilityRank(entity.getId(), oldHero.ability4Rank, 3);
                    }
                }
                HeroServerEvents.fallenHeroes.remove(oldHero);
            } else {
                HeroUnitSave oldHero = HeroUnit.getFallenHero(true, placement.ownerName, getHeroEntityType().getDescriptionId());
                HeroClientEvents.fallenHeroes.remove(oldHero);
            }
        };
    }

    // can't make this a member as we can't refer to other registered objects at init time
    protected EntityType<? extends HeroUnit> getHeroEntityType() {
        return null;
    }

    private String getTooltip(String ownerName) {
        HeroUnitSave heroSave = HeroUnit.getFallenHero(true, ownerName, getHeroEntityType().getDescriptionId());
        if (heroSave != null) {
            return I18n.get(tooltipI18n, HeroUnit.getHeroLevel(heroSave.experience));
        }
        return I18n.get(tooltipI18n);
    }

    private ResourceCost getReviveCost(boolean isClientSide, String ownerName) {
        ArrayList<HeroUnitSave> heroSaves = isClientSide ? HeroClientEvents.fallenHeroes : HeroServerEvents.fallenHeroes;
        HeroUnitSave heroSave = HeroUnit.getFallenHero(isClientSide, ownerName, getHeroEntityType().getDescriptionId());
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
                        HeroUnit.getFallenHero(true, prodBuilding.ownerName, getHeroEntityType().getDescriptionId()) == null,
                () -> true,
                () -> BuildingServerboundPacket.startProduction(prodBuilding.originPos, this),
                null,
                List.of(
                        fcs(getTooltip(prodBuilding.ownerName), true),
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
