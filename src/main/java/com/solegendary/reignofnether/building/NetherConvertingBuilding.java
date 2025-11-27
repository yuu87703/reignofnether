package com.solegendary.reignofnether.building;

import javax.annotation.Nullable;

public interface NetherConvertingBuilding {

    double getMaxRange();

    double getStartingRange();

    @Nullable NetherZone getZone();

    void setNetherZone(NetherZone nz, boolean save);
}
