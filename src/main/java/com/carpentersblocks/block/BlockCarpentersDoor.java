package com.carpentersblocks.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.carpentersblocks.CarpentersBlocks;
import com.carpentersblocks.data.Hinge;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.registry.BlockRegistry;
import com.carpentersblocks.util.registry.IconRegistry;
import com.carpentersblocks.util.registry.ItemRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCarpentersDoor extends BlockHinged {

    public static final String type[] = { "glassTop", "glassTall", "panel", "screenTall", "french", "hidden" };

    public BlockCarpentersDoor(Material material) {
        super(material);
    }

    @SideOnly(Side.CLIENT)
    @Override
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons(IIconRegister iconRegister) {
        IconRegistry.icon_door_screen_tall = iconRegister
                .registerIcon(CarpentersBlocks.MODID + ":" + "door/door_screen_tall");
        IconRegistry.icon_door_glass_tall_top = iconRegister
                .registerIcon(CarpentersBlocks.MODID + ":" + "door/door_glass_tall_top");
        IconRegistry.icon_door_glass_tall_bottom = iconRegister
                .registerIcon(CarpentersBlocks.MODID + ":" + "door/door_glass_tall_bottom");
        IconRegistry.icon_door_glass_top = iconRegister
                .registerIcon(CarpentersBlocks.MODID + ":" + "door/door_glass_top");
        IconRegistry.icon_door_french_glass_top = iconRegister
                .registerIcon(CarpentersBlocks.MODID + ":" + "door/door_french_glass_top");
        IconRegistry.icon_door_french_glass_bottom = iconRegister
                .registerIcon(CarpentersBlocks.MODID + ":" + "door/door_french_glass_bottom");
    }

    @Override
    /**
     * Alters hinge type and redstone behavior.
     */
    protected boolean onHammerRightClick(TEBase TE, EntityPlayer entityPlayer) {
        if (!entityPlayer.isSneaking()) {

            int temp = Hinge.getType(TE);

            if (++temp >= type.length) {
                temp = 0;
            }

            setHingeType(TE, temp);
            super.onHammerRightClick(TE, entityPlayer);
            return true;
        }

        return super.onHammerRightClick(TE, entityPlayer);
    }

    @Override
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public Item getItemDropped(int metadata, Random random, int par3) {
        return ItemRegistry.itemCarpentersDoor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public Item getItem(World world, int x, int y, int z) {
        return ItemRegistry.itemCarpentersDoor;
    }

    @Override
    /**
     * The type of render function that is called for this block
     */
    public int getRenderType() {
        return BlockRegistry.carpentersDoorRenderID;
    }

    @Override
    public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z) {
        ForgeDirection[] axises = { ForgeDirection.UP, ForgeDirection.DOWN };
        return axises;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        // to correctly support archimedes' ships mod:
        // if Axis is DOWN, block rotates to the left, north -> west -> south -> east
        // if Axis is UP, block rotates to the right: north -> east -> south -> west

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TEBase) {
            TEBase cbTile = (TEBase) tile;
            int direction = Hinge.getFacing(cbTile);
            switch (axis) {
                case UP: {
                    switch (direction) {
                        case 0: {
                            Hinge.setFacing(cbTile, 1);
                            break;
                        }
                        case 1: {
                            Hinge.setFacing(cbTile, 2);
                            break;
                        }
                        case 2: {
                            Hinge.setFacing(cbTile, 3);
                            break;
                        }
                        case 3: {
                            Hinge.setFacing(cbTile, 0);
                            break;
                        }
                        default:
                            return false;
                    }
                    break;
                }
                case DOWN: {
                    switch (direction) {
                        case 0: {
                            Hinge.setFacing(cbTile, 3);
                            break;
                        }
                        case 1: {
                            Hinge.setFacing(cbTile, 0);
                            break;
                        }
                        case 2: {
                            Hinge.setFacing(cbTile, 1);
                            break;
                        }
                        case 3: {
                            Hinge.setFacing(cbTile, 2);
                            break;
                        }
                        default:
                            return false;
                    }
                    break;
                }
                default:
                    return false;
            }
            return true;
        }
        return false;
    }
}
