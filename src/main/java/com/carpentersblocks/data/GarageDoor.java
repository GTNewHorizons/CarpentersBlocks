package com.carpentersblocks.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.registry.BlockRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GarageDoor extends AbstractMultiBlock implements ISided {

    /**
     * 16-bit data components:
     *
     * [000000] [0] [0] [0] [000] [0000] Unused Host Rigid State Dir Type
     */
    public static final GarageDoor INSTANCE = new GarageDoor();

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_GLASS_TOP = 1;
    public static final int TYPE_GLASS = 2;
    public static final int TYPE_SIDING = 3;
    public static final int TYPE_HIDDEN = 4;

    public static final int STATE_CLOSED = 0;
    public static final int STATE_OPEN = 1;

    public static final byte DOOR_NONRIGID = 0;
    public static final byte DOOR_RIGID = 1;

    /**
     * Returns type.
     */
    public int getType(TEBase TE) {
        return TE.getData() & 0xf;
    }

    /**
     * Sets type.
     */
    public void setType(TEBase TE, int type) {
        int temp = (TE.getData() & ~0xf) | type;
        TE.setData(temp);
    }

    /**
     * Returns direction.
     */
    @Override
    public ForgeDirection getDirection(TEBase TE) {
        int side = (TE.getData() & 0x70) >> 4;
        return ForgeDirection.getOrientation(side);
    }

    /**
     * Sets direction.
     */
    @Override
    public boolean setDirection(TEBase TE, ForgeDirection dir) {
        int temp = (TE.getData() & ~0x70) | (dir.ordinal() << 4);
        return TE.setData(temp);
    }

    /**
     * Returns state (open or closed).
     */
    public int getState(TEBase TE) {
        return (TE.getData() & 0x80) >> 7;
    }

    /**
     * Sets state (open or closed).
     */
    public void setState(TEBase TE, int state) {
        int temp = (TE.getData() & ~0x80) | (state << 7);
        TE.setData(temp);
    }

    /**
     * Whether garage door is rigid (requires redstone).
     */
    public boolean isRigid(TEBase TE) {
        return getRigidity(TE) == DOOR_RIGID;
    }

    /**
     * Returns rigidity (requiring redstone).
     */
    public int getRigidity(TEBase TE) {
        return (TE.getData() & 0x100) >> 8;
    }

    /**
     * Sets rigidity (requiring redstone).
     */
    public void setRigidity(TEBase TE, int rigidity) {
        int temp = (TE.getData() & ~0x100) | (rigidity << 8);
        TE.setData(temp);
    }

    /**
     * Sets host door (the topmost).
     */
    public void setHost(TEBase TE) {
        int temp = TE.getData() | 0x200;
        TE.setData(temp);
    }

    /**
     * Returns true if door is host (the topmost).
     */
    public boolean isHost(TEBase TE) {
        return (TE.getData() & 0x200) > 0;
    }

    /**
     * Compares door pieces by distance from player.
     */
    @SideOnly(Side.CLIENT)
    class DoorPieceDistanceComparator implements Comparator<TEBase> {

        @Override
        public int compare(TEBase tileEntity1, TEBase tileEntity2) {
            double dist1 = Minecraft.getMinecraft().thePlayer
                    .getDistance(tileEntity1.xCoord, tileEntity1.yCoord, tileEntity1.zCoord);
            double dist2 = Minecraft.getMinecraft().thePlayer
                    .getDistance(tileEntity2.xCoord, tileEntity2.yCoord, tileEntity2.zCoord);
            return dist1 < dist2 ? -1 : 1;
        }
    }

    /**
     * When passed a TEBase, will play state change sound if piece is nearest to player out of all connecting door
     * pieces.
     * <p>
     * The server would normally handle this and send notification to all nearby players, but sound source will be
     * dependent on each player's location.
     *
     * @param list         an {@link ArrayList<TEBase>} of door pieces
     * @param entityPlayer the source {@link EntityPlayer}
     * @return the {@link TEBase} nearest to {@link EntityPlayer}
     */
    @SideOnly(Side.CLIENT)
    public void playStateChangeSound(TEBase TE) {
        Set<TEBase> set = getBlocks(TE, BlockRegistry.blockCarpentersGarageDoor);
        List<TEBase> list = new ArrayList<TEBase>(set); // For sorting

        // Only play sound if piece is nearest to player
        Collections.sort(list, new DoorPieceDistanceComparator());
        if (list.get(0).equals(TE)) {
            TE.getWorldObj().playAuxSFXAtEntity((EntityPlayer) null, 1003, TE.xCoord, TE.yCoord, TE.zCoord, 0);
        }
    }

    /**
     * Helper function for determining properties based on a nearby garage door piece around the given coordinates.
     *
     * @param world the {@link World}
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return a {@link TEBase} with relevant properties
     */
    public TEBase findReferencePiece(World world, int x, int y, int z, ForgeDirection axis) {
        ForgeDirection dir = axis.getRotation(ForgeDirection.UP);
        do {
            TEBase temp1 = BlockProperties
                    .getTileEntity(BlockRegistry.blockCarpentersGarageDoor, world, x + dir.offsetX, y, z + dir.offsetZ);
            TEBase temp2 = BlockProperties
                    .getTileEntity(BlockRegistry.blockCarpentersGarageDoor, world, x - dir.offsetX, y, z - dir.offsetZ);
            if (temp1 != null && getDirection(temp1).equals(axis)) {
                return temp1;
            } else if (temp2 != null && getDirection(temp2).equals(axis)) {
                return temp2;
            }
        } while (y > 1 && world.getBlock(x, --y, z).equals(Blocks.air));

        return null;
    }

    /**
     * Copies relevant properties and owner from source tile entity to destination tile entity.
     *
     * @param src  the source {@link TEBase}
     * @param dest the destination {@link TEBase}
     */
    public void replicate(final TEBase src, TEBase dest) {
        setDirection(dest, getDirection(src));
        setRigidity(dest, getRigidity(src));
        setState(dest, getState(src));
        setType(dest, getType(src));
        dest.copyOwner(src);
    }

    /**
     * Whether garage door is open.
     *
     * @param TE the {@link TEBase}
     * @return <code>true</code> if garage door is open
     */
    public boolean isOpen(TEBase TE) {
        return getState(TE) == STATE_OPEN;
    }

    /**
     * Weather panel is the bottommost.
     *
     * @param TE the {@link TEBase}
     * @return true if panel is the bottommost
     */
    public boolean isBottommost(TEBase TE) {
        return !TE.getWorldObj().getBlock(TE.xCoord, TE.yCoord - 1, TE.zCoord)
                .equals(BlockRegistry.blockCarpentersGarageDoor);
    }

    /**
     * Gets the topmost garage door tile entity.
     *
     * @param TE the {@link TEBase}
     * @return the {@link TEBase}
     */
    public TEBase getTopmost(World world, int x, int y, int z) {
        do {
            ++y;
        } while (world.getBlock(x, y, z).equals(BlockRegistry.blockCarpentersGarageDoor));

        return (TEBase) world.getTileEntity(x, y - 1, z);
    }

    /**
     * Gets the bottommost garage door tile entity.
     *
     * @param TE the {@link TEBase}
     * @return the {@link TEBase}
     */
    public TEBase getBottommost(IBlockAccess world, int x, int y, int z) {
        do {
            --y;
        } while (world.getBlock(x, y, z).equals(BlockRegistry.blockCarpentersGarageDoor));

        return (TEBase) world.getTileEntity(x, y + 1, z);
    }

    /**
     * Whether block is visible.
     * <p>
     * If a block is open and not in the topmost position, it cannot be selected or collided with.
     *
     * @param TE the {@link TEBase}
     * @return true if visible
     */
    public boolean isVisible(TEBase TE) {
        if (isOpen(TE)) {
            return isHost(TE);
        } else {
            return true;
        }
    }

    @Override
    public int getMatchingDataPattern(TEBase TE) {
        return TE.getData() & 0x70;
    }

    @Override
    public ForgeDirection[] getLocateDirs(TEBase TE) {
        ForgeDirection dirPlane = getDirection(TE).getRotation(ForgeDirection.UP);
        ForgeDirection[] dirs = { ForgeDirection.UP, ForgeDirection.DOWN, dirPlane, dirPlane.getOpposite() };

        return dirs;
    }
}
