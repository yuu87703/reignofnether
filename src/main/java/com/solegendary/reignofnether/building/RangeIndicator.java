package com.solegendary.reignofnether.building;

import net.minecraft.core.BlockPos;

import java.util.Set;

public interface RangeIndicator {
    public void updateHighlightBps();
    public Set<BlockPos> getHighlightBps();
    public boolean showOnlyWhenSelected();
}