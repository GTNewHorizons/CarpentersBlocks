package com.carpentersblocks.api.compat;

import cpw.mods.fml.common.Loader;

public enum Mods {

    CHISEL("chisel");

    public final String ID;

    private Boolean modLoaded;

    Mods(String ID) {
        this.ID = ID;
    }

    public boolean isModLoaded() {
        if (this.modLoaded == null) {
            this.modLoaded = Loader.isModLoaded(ID);
        }
        return this.modLoaded;
    }
}
