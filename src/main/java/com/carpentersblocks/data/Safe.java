package com.carpentersblocks.data;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.EntityLivingUtil;

public class Safe {

    /**
     * 16-bit data components:
     *
     * [0000000000] [00] [0] [0] [00] Unused Automation Lock State Facing
     */
    public static final byte STATE_CLOSED = 0;

    public static final byte STATE_OPEN = 1;

    public static final byte LOCK_SET = 0;
    public static final byte LOCK_UNSET = 1;

    public static final byte AUTOMATION_DISABLED = 0;
    public static final byte AUTOMATION_SEND = 1;
    public static final byte AUTOMATION_RECEIVE = 2;
    public static final byte AUTOMATION_ALL = 3;

    /**
     * Returns facing.
     */
    public static ForgeDirection getFacing(TEBase TE) {
        return EntityLivingUtil.getRotationFacing(TE.getData() & 0x3).getOpposite();
    }

    /**
     * Sets facing. Stored as player facing from 0 to 3.
     */
    public static void setFacing(TEBase TE, int facing) {
        int temp = (TE.getData() & ~0x3) | facing;
        TE.setData(temp);
    }

    /**
     * Returns state.
     */
    public static int getState(TEBase TE) {
        return (TE.getData() & 0x4) >> 2;
    }

    /**
     * Sets state.
     */
    public static void setState(TEBase TE, int state) {
        int temp = (TE.getData() & ~0x4) | (state << 2);
        TE.setData(temp);
    }

    /**
     * Returns whether safe is locked.
     */
    public static boolean isLocked(TEBase TE) {
        return (TE.getData() & 0x8) == LOCK_SET;
    }

    /**
     * Sets lock state.
     */
    public static void setLocked(TEBase TE, boolean isLocked) {
        int temp = (TE.getData() & ~0x8) | ((isLocked ? LOCK_SET : LOCK_UNSET) << 3);
        TE.setData(temp);
    }

    /**
     * Returns automation permissions for safe.
     */
    public static int getAutoPerm(TEBase TE) {
        return (TE.getData() & 0x30) >> 4;
    }

    /**
     * Sets automation permissions for safe.
     */
    public static void setAutoPerm(TEBase TE, int autoPerm) {
        int temp = (TE.getData() & ~0x30) | (autoPerm << 4);
        TE.setData(temp);
    }

    /**
     * Returns whether safe is open.
     */
    public static boolean isOpen(TEBase TE) {
        if (getState(TE) == STATE_OPEN) {
            // Validate safe is open by checking nearby players
            float f = 5.0F;
            boolean isOpen = false;
            List list = TE.getWorldObj().getEntitiesWithinAABB(
                    EntityPlayer.class,
                    AxisAlignedBB.getBoundingBox(
                            (double) ((float) TE.xCoord - f),
                            (double) ((float) TE.yCoord - f),
                            (double) ((float) TE.zCoord - f),
                            (double) ((float) (TE.xCoord + 1) + f),
                            (double) ((float) (TE.yCoord + 1) + f),
                            (double) ((float) (TE.zCoord + 1) + f)));
            for (EntityPlayer entityPlayer : (List<EntityPlayer>) list) {
                if (entityPlayer.openContainer instanceof ContainerChest) {
                    IInventory iinventory = ((ContainerChest) entityPlayer.openContainer).getLowerChestInventory();
                    if (iinventory.equals(TE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns whether items can be inserted into safe through automation.
     */
    public static boolean allowsInsertion(TEBase TE) {
        int autoPerm = getAutoPerm(TE);
        return autoPerm == AUTOMATION_RECEIVE || autoPerm == AUTOMATION_ALL;
    }

    /**
     * Returns whether items can be extracted from safe through automation.
     */
    public static boolean allowsExtraction(TEBase TE) {
        int autoPerm = getAutoPerm(TE);
        return autoPerm == AUTOMATION_SEND || autoPerm == AUTOMATION_ALL;
    }
}
