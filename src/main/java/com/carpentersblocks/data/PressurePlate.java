package com.carpentersblocks.data;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.BlockProperties;

public class PressurePlate implements ISided {

    /**
     * 16-bit data components:
     *
     * [000000000] [00] [0] [0] [000] Unused Trigger Polarity State Dir
     */
    public static final byte POLARITY_POSITIVE = 0;

    public static final byte POLARITY_NEGATIVE = 1;

    public static final byte STATE_OFF = 0;
    public static final byte STATE_ON = 1;

    public static final byte TRIGGER_PLAYER = 0;
    public static final byte TRIGGER_MONSTER = 1;
    public static final byte TRIGGER_ANIMAL = 2;
    public static final byte TRIGGER_ALL = 3;

    /**
     * Returns direction.
     */
    @Override
    public ForgeDirection getDirection(TEBase TE) {
        return ForgeDirection.getOrientation(TE.getData() & 0x7);
    }

    /**
     * Sets direction.
     */
    @Override
    public boolean setDirection(TEBase TE, ForgeDirection dir) {
        int temp = (TE.getData() & ~0x7) | dir.ordinal();
        return TE.setData(temp);
    }

    /**
     * Returns state.
     */
    public int getState(TEBase TE) {
        return (TE.getData() & 0x8) >> 3;
    }

    /**
     * Sets state.
     */
    public void setState(TEBase TE, int state, boolean playSound) {
        int temp = (TE.getData() & ~0x8) | (state << 3);
        World world = TE.getWorldObj();

        if (!world.isRemote && BlockProperties.toBlock(BlockProperties.getCover(TE, 6)).getMaterial() != Material.cloth
                && playSound
                && getState(TE) != state) {
            world.playSoundEffect(
                    TE.xCoord + 0.5D,
                    TE.yCoord + 0.1D,
                    TE.zCoord + 0.5D,
                    "random.click",
                    0.3F,
                    getState(TE) == STATE_ON ? 0.5F : 0.6F);
        }

        TE.setData(temp);
    }

    /**
     * Returns polarity.
     */
    public int getPolarity(TEBase TE) {
        return (TE.getData() & 0x10) >> 4;
    }

    /**
     * Sets polarity.
     */
    public void setPolarity(TEBase TE, int polarity) {
        int temp = (TE.getData() & ~0x10) | (polarity << 4);
        TE.setData(temp);
    }

    /**
     * Returns trigger entity.
     */
    public int getTriggerEntity(TEBase TE) {
        return (TE.getData() & 0x60) >> 5;
    }

    /**
     * Sets trigger entity.
     */
    public void setTriggerEntity(TEBase TE, int trigger) {
        int temp = (TE.getData() & ~0x60) | (trigger << 5);
        TE.setData(temp);
    }
}
