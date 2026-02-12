package com.solegendary.reignofnether.unit.units.monsters;

import com.solegendary.reignofnether.unit.interfaces.AttackerUnit;
import com.solegendary.reignofnether.unit.interfaces.RangedAttackerUnit;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class BoggedUnit extends SkeletonUnit implements Unit, AttackerUnit, RangedAttackerUnit {

    public BoggedUnit(EntityType<? extends Skeleton> entityType, Level level) {
        super(entityType, level);
    }
}
