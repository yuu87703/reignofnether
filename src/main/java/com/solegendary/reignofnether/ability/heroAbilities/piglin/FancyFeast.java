package com.solegendary.reignofnether.ability.heroAbilities.piglin;

//Throws out a pile of food - friendly units automatically pick up this food and take a few seconds to eat it to instantly heal
//Higher levels raise the quality of food thrown
//Greed is Good raises the amount of food thrown

// TODO: make piglin units stop to eat vanilla food items if they are damaged

import com.solegendary.reignofnether.ability.HeroAbility;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.unit.UnitAction;
import com.solegendary.reignofnether.unit.interfaces.HeroUnit;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.reignofnether.unit.UnitClientEvents.sendUnitCommand;
import static com.solegendary.reignofnether.util.MiscUtil.fcs;
import static com.solegendary.reignofnether.util.MiscUtil.fcsIcons;

public class FancyFeast extends HeroAbility {

    private static final int CD_MAX_SECONDS = 45 * ResourceCost.TICKS_PER_SECOND;
    private static final float BASE_ITEMS = 6;
    private static final float BONUS_ITEMS_PER_RESOURCES = 3;

    public FancyFeast(HeroUnit hero) {
        super(hero, 3, UnitAction.FANCY_FEAST, CD_MAX_SECONDS, 10, 0, false);
    }

    private ResourceLocation getIcon() {
        if (rank == 3)
            return new ResourceLocation("minecraft", "textures/item/cooked_beef.png");
        else if (rank == 2)
            return new ResourceLocation("minecraft", "textures/item/cooked_chicken.png");
        else
            return new ResourceLocation("minecraft", "textures/item/bread.png");
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey) {
        return new AbilityButton("Fancy Feast",
            getIcon(),
            hotkey,
            () -> false,
            () -> rank == 0,
            () -> true,
            () -> sendUnitCommand(UnitAction.FANCY_FEAST),
            null,
            getTooltipLines(),
            this
        );
    }

    public List<FormattedCharSequence> getTooltipLines() {
        return List.of(
                fcs(I18n.get("abilities.reignofnether.fancy_feast"), true),
                fcs(""),
                fcsIcons(I18n.get("abilities.reignofnether.fancy_feast.stats", CD_MAX_SECONDS / 20)),
                fcs(I18n.get("abilities.reignofnether.fancy_feast.tooltip1")),
                fcs(I18n.get("abilities.reignofnether.fancy_feast.tooltip2", BASE_ITEMS, BONUS_ITEMS_PER_RESOURCES))
        );
    }

    public List<FormattedCharSequence> getRankUpTooltipLines() {
        return List.of(
                fcs(I18n.get("abilities.reignofnether.fancy_feast"), true),
                fcs(I18n.get("abilities.reignofnether.fancy_feast.tooltip1")),
                fcs(""),
                fcs(I18n.get("abilities.reignofnether.fancy_feast.rank1"), rank == 0),
                fcs(I18n.get("abilities.reignofnether.fancy_feast.rank2"), rank == 1),
                fcs(I18n.get("abilities.reignofnether.fancy_feast.rank3"), rank == 2)
        );
    }

    public void use(Level level, Unit unitUsing, BlockPos targetBp) {

    }
}
