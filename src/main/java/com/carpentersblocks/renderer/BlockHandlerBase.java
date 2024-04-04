package com.carpentersblocks.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.carpentersblocks.block.BlockCoverable;
import com.carpentersblocks.data.Slope;
import com.carpentersblocks.renderer.helper.LightingHelper;
import com.carpentersblocks.renderer.helper.RenderHelper;
import com.carpentersblocks.renderer.helper.RoutableFluidsHelper;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.handler.DesignHandler;
import com.carpentersblocks.util.handler.DyeHandler;
import com.carpentersblocks.util.handler.OptifineHandler;
import com.carpentersblocks.util.handler.OverlayHandler;
import com.carpentersblocks.util.handler.OverlayHandler.Overlay;
import com.carpentersblocks.util.registry.FeatureRegistry;
import com.carpentersblocks.util.registry.IconRegistry;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRHFactory;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Optional.Interface(modid = "angelica", iface = "com.gtnewhorizons.angelica.api.ThreadSafeISBRHFactory")
public abstract class BlockHandlerBase implements ISimpleBlockRenderingHandler, ThreadSafeISBRHFactory {

    public static final int PASS_OPAQUE = 0;
    public static final int PASS_ALPHA = 1;

    public static final int DOWN = 0;
    public static final int UP = 1;
    public static final int NORTH = 2;
    public static final int SOUTH = 3;
    public static final int WEST = 4;
    public static final int EAST = 5;

    public RenderBlocks renderBlocks;
    public LightingHelper lightingHelper;
    public Block srcBlock;
    public TEBase TE;
    public boolean suppressOverlay;
    public boolean suppressChiselDesign;
    public boolean suppressDyeColor;
    public boolean disableAO;
    public boolean hasDyeOverride;
    public int dyeOverride;
    public boolean[] hasIconOverride = new boolean[6];
    public IIcon[] iconOverride = new IIcon[6];
    public int renderPass;

    /** 0-5 are side covers, with 6 being the block itself. */
    public int coverRendering = 6;

    public RenderHelper renderHelper = new RenderHelper();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderBlocks) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();

        if (block instanceof BlockCoverable) {

            IIcon icon = renderBlocks.getIconSafe(((BlockCoverable) block).getIcon());
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);

        } else {

            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getIconSafe(block.getIcon(0, metadata)));
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getIconSafe(block.getIcon(1, metadata)));
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getIconSafe(block.getIcon(2, metadata)));
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getIconSafe(block.getIcon(3, metadata)));
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getIconSafe(block.getIcon(4, metadata)));
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getIconSafe(block.getIcon(5, metadata)));
        }

        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(90.0F, 0.0F, -1.0F, 0.0F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelID,
            RenderBlocks renderBlocks) {
        renderHelper.vertexCount = 0;
        renderPass = ForgeHooksClient.getWorldRenderPass();
        TileEntity TE_default = blockAccess.getTileEntity(x, y, z);

        if (TE_default instanceof TEBase) {

            TE = (TEBase) TE_default;
            srcBlock = block;
            this.renderBlocks = renderBlocks;
            lightingHelper = new LightingHelper(renderBlocks);

            renderCarpentersBlock(x, y, z);
            renderSideBlocks(x, y, z);

            if (FeatureRegistry.enableRoutableFluids) {
                renderHelper.vertexCount += RoutableFluidsHelper.render(TE, renderBlocks, x, y, z) ? 4 : 0;
            }

            if (FeatureRegistry.enableRailSlopes) {
                Block blockYP = blockAccess.getBlock(x, y + 1, z);
                if (blockAccess.isSideSolid(x, y, z, ForgeDirection.UP, false) && blockYP instanceof BlockRailBase) {
                    int metadata = ((BlockRailBase) blockYP).getBasicRailMetadata(blockAccess, null, x, y + 1, z);

                    BlockHandlerCarpentersSlope slopeHandler = new BlockHandlerCarpentersSlope();
                    slopeHandler.renderBlocks = this.renderBlocks;
                    slopeHandler.TE = TE;
                    slopeHandler.lightingHelper = lightingHelper;
                    slopeHandler.srcBlock = srcBlock;
                    slopeHandler.suppressOverlay = true;

                    switch (metadata) {
                        case 2: // Sloping down -X (West)
                            slopeHandler.renderSlope(getCoverForRendering(TE), Slope.WEDGE_POS_W, x, y + 1, z, true);
                            break;
                        case 3: // Sloping down +X (East)
                            slopeHandler.renderSlope(getCoverForRendering(TE), Slope.WEDGE_POS_E, x, y + 1, z, true);
                            break;
                        case 4: // Sloping down +Z (South)
                            slopeHandler.renderSlope(getCoverForRendering(TE), Slope.WEDGE_POS_S, x, y + 1, z, true);
                            break;
                        case 5: // Sloping down -Z (North)
                            slopeHandler.renderSlope(getCoverForRendering(TE), Slope.WEDGE_POS_N, x, y + 1, z, true);
                            break;
                        default: {}
                    }
                }
            }
        }

        return renderHelper.vertexCount > 0;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return 0;
    }

    /**
     * For South-facing block, sets render bounds, rotates them and renders.
     */
    protected void renderBlockWithRotation(ItemStack itemStack, int x, int y, int z, double xMin, double yMin,
            double zMin, double xMax, double yMax, double zMax, ForgeDirection... dir) {
        renderBlocks.setRenderBounds(xMin, yMin, zMin, xMax, yMax, zMax);

        for (ForgeDirection rot : dir) {
            rotateBounds(renderBlocks, rot);
        }

        renderBlock(itemStack, x, y, z);
    }

    /**
     * For South-facing blocks, rotates bounds for a single directional input.
     */
    protected void rotateBounds(RenderBlocks renderBlocks, ForgeDirection dir) {
        switch (dir) {
            case DOWN:
                renderBlocks.setRenderBounds(
                        renderBlocks.renderMinX,
                        1.0D - renderBlocks.renderMaxZ,
                        renderBlocks.renderMinY,
                        renderBlocks.renderMaxX,
                        1.0D - renderBlocks.renderMinZ,
                        renderBlocks.renderMaxY);
                break;
            case UP:
                renderBlocks.setRenderBounds(
                        renderBlocks.renderMinX,
                        renderBlocks.renderMinZ,
                        renderBlocks.renderMinY,
                        renderBlocks.renderMaxX,
                        renderBlocks.renderMaxZ,
                        renderBlocks.renderMaxY);
                break;
            case NORTH:
                renderBlocks.setRenderBounds(
                        1.0D - renderBlocks.renderMaxX,
                        renderBlocks.renderMinY,
                        1.0D - renderBlocks.renderMaxZ,
                        1.0D - renderBlocks.renderMinX,
                        renderBlocks.renderMaxY,
                        1.0D - renderBlocks.renderMinZ);
                break;
            case EAST:
                renderBlocks.setRenderBounds(
                        renderBlocks.renderMinZ,
                        renderBlocks.renderMinY,
                        1.0D - renderBlocks.renderMaxX,
                        renderBlocks.renderMaxZ,
                        renderBlocks.renderMaxY,
                        1.0D - renderBlocks.renderMinX);
                break;
            case WEST:
                renderBlocks.setRenderBounds(
                        1.0D - renderBlocks.renderMaxZ,
                        renderBlocks.renderMinY,
                        renderBlocks.renderMinX,
                        1.0D - renderBlocks.renderMinZ,
                        renderBlocks.renderMaxY,
                        renderBlocks.renderMaxX);
                break;
            default: {}
        }
    }

    /**
     * Gets cover {@link ItemStack} suitable for pulling block textures from, regardless if it has an
     * {@link NBTTagCompound}.
     *
     * @param TE [optional] the {@link TEBase}, if not {@link #srcBlock}
     * @return the {@link ItemStack}
     */
    protected ItemStack getCoverForRendering(TEBase... TE) {
        TEBase temp = TE.length == 0 ? this.TE : TE[0];
        return BlockProperties.getCoverSafe(temp, coverRendering);
    }

    /**
     * Sets texture rotation for side.
     */
    protected void setTextureRotation(int side, int rotation) {
        switch (side) {
            case DOWN:
                renderBlocks.uvRotateBottom = rotation;
                break;
            case UP:
                renderBlocks.uvRotateTop = rotation;
                break;
            case NORTH:
                renderBlocks.uvRotateNorth = rotation;
                break;
            case SOUTH:
                renderBlocks.uvRotateSouth = rotation;
                break;
            case WEST:
                renderBlocks.uvRotateWest = rotation;
                break;
            default:
                renderBlocks.uvRotateEast = rotation;
                break;
        }
    }

    /**
     * Sets directional block side rotation in RenderBlocks for directional blocks like pillars and logs.
     */
    protected void setTextureRotationForDirectionalBlock(int side) {
        int metadata = getCoverForRendering().getItemDamage();
        int dir = metadata & 12;

        switch (side) {
            case DOWN:
                if (metadata == 3 || dir == 4) {
                    renderBlocks.uvRotateBottom = 1;
                }
                break;
            case UP:
                if (metadata == 3 || dir == 4) {
                    renderBlocks.uvRotateTop = 1;
                }
                break;
            case NORTH:
                if (metadata == 3 || dir == 4) {
                    renderBlocks.uvRotateNorth = 1;
                }
                break;
            case SOUTH:
                if (metadata == 3 || dir == 4) {
                    renderBlocks.uvRotateSouth = 1;
                }
                break;
            case WEST:
                if (metadata == 3 || dir == 8) {
                    renderBlocks.uvRotateWest = 1;
                }
                break;
            case EAST:
                if (metadata == 3 || dir == 8) {
                    renderBlocks.uvRotateEast = 1;
                }
                break;
        }
    }

    /**
     * Resets side rotation in RenderBlocks back to default values.
     */
    protected void resetTextureRotation(int side) {
        switch (side) {
            case DOWN:
                renderBlocks.uvRotateBottom = 0;
                break;
            case UP:
                renderBlocks.uvRotateTop = 0;
                break;
            case NORTH:
                renderBlocks.uvRotateNorth = 0;
                break;
            case SOUTH:
                renderBlocks.uvRotateSouth = 0;
                break;
            case WEST:
                renderBlocks.uvRotateWest = 0;
                break;
            case EAST:
                renderBlocks.uvRotateEast = 0;
                break;
        }
    }

    /**
     * Returns texture rotation for side.
     */
    protected int getTextureRotation(int side) {
        int[] rotations = { renderBlocks.uvRotateBottom, renderBlocks.uvRotateTop, renderBlocks.uvRotateNorth,
                renderBlocks.uvRotateSouth, renderBlocks.uvRotateWest, renderBlocks.uvRotateEast };

        return rotations[side];
    }

    /**
     * Sets dye override.
     */
    protected void setDyeOverride(int color) {
        hasDyeOverride = true;
        dyeOverride = color;
    }

    /**
     * Clears dye override.
     */
    protected void clearDyeOverride() {
        hasDyeOverride = false;
    }

    /**
     * Sets icon override. Using side 6 overrides all sides. RenderBlocks' icon override will override this one when
     * breaking animation is played.
     */
    protected void setIconOverride(int side, IIcon icon) {
        if (side == 6) {
            for (int count = 0; count < 6; ++count) {
                hasIconOverride[count] = true;
                iconOverride[count] = icon;
            }
        } else {
            hasIconOverride[side] = true;
            iconOverride[side] = icon;
        }
    }

    /**
     * Clears icon override.
     */
    protected void clearIconOverride(int side) {
        if (side == 6) {
            for (int count = 0; count < 6; ++count) {
                hasIconOverride[count] = false;
            }
        } else {
            hasIconOverride[side] = false;
        }
    }

    /**
     * Sets up side cover rendering bounds. Will return block location where side cover should be rendered.
     */
    protected int[] getSideCoverRenderBounds(int x, int y, int z, int side) {
        double offset = 1.0D / 16.0D;

        /*
         * Make snow match vanilla snow depth for continuity.
         */
        if (side == UP) {
            Block block = BlockProperties.toBlock(getCoverForRendering());
            if (block.equals(Blocks.snow) || block.equals(Blocks.snow_layer)) {
                offset = 1.0D / 8.0D;
            }
        }

        /*
         * Adjust bounds of side cover to accommodate partial rendered blocks (slabs, stair steps)
         */
        switch (side) {
            case DOWN:
                if (renderBlocks.renderMinY > 0.0D) {
                    renderBlocks.renderMaxY = renderBlocks.renderMinY;
                    renderBlocks.renderMinY -= offset;
                } else {
                    renderBlocks.renderMaxY = 1.0D;
                    renderBlocks.renderMinY = renderBlocks.renderMaxY - offset;
                    y -= 1;
                }
                break;
            case UP:
                if (renderBlocks.renderMaxY < 1.0D) {
                    renderBlocks.renderMinY = renderBlocks.renderMaxY;
                    renderBlocks.renderMaxY += offset;
                } else {
                    renderBlocks.renderMaxY = offset;
                    renderBlocks.renderMinY = 0.0D;
                    y += 1;
                }
                break;
            case NORTH:
                if (renderBlocks.renderMinZ > 0.0D) {
                    renderBlocks.renderMaxZ = renderBlocks.renderMinZ;
                    renderBlocks.renderMinZ -= offset;
                } else {
                    renderBlocks.renderMaxZ = 1.0D;
                    renderBlocks.renderMinZ = renderBlocks.renderMaxZ - offset;
                    z -= 1;
                }
                break;
            case SOUTH:
                if (renderBlocks.renderMaxZ < 1.0D) {
                    renderBlocks.renderMinZ = renderBlocks.renderMaxZ;
                    renderBlocks.renderMaxZ += offset;
                } else {
                    renderBlocks.renderMaxZ = offset;
                    renderBlocks.renderMinZ = 0.0D;
                    z += 1;
                }
                break;
            case WEST:
                if (renderBlocks.renderMinX > 0.0D) {
                    renderBlocks.renderMaxX = renderBlocks.renderMinX;
                    renderBlocks.renderMinX -= offset;
                } else {
                    renderBlocks.renderMaxX = 1.0D;
                    renderBlocks.renderMinX = renderBlocks.renderMaxX - offset;
                    x -= 1;
                }
                break;
            case EAST:
                if (renderBlocks.renderMaxX < 1.0D) {
                    renderBlocks.renderMinX = renderBlocks.renderMaxX;
                    renderBlocks.renderMaxX += offset;
                } else {
                    renderBlocks.renderMaxX = offset;
                    renderBlocks.renderMinX = 0.0D;
                    x += 1;
                }
                break;
        }

        return new int[] { x, y, z };
    }

    /**
     * Renders side covers.
     */
    protected void renderSideBlocks(int x, int y, int z) {
        renderBlocks.renderAllFaces = true;

        srcBlock.setBlockBoundsBasedOnState(renderBlocks.blockAccess, x, y, z);

        for (int side = 0; side < 6; ++side) {
            if (TE.hasAttribute(TE.ATTR_COVER[side])) {
                coverRendering = side;
                int[] renderOffset = getSideCoverRenderBounds(x, y, z, side);
                renderBlock(getCoverForRendering(), renderOffset[0], renderOffset[1], renderOffset[2]);
                renderBlocks.setRenderBoundsFromBlock(srcBlock);
            }
        }

        renderBlocks.renderAllFaces = false;
        coverRendering = 6;
    }

    /**
     * Override to provide custom icons.
     */
    protected IIcon getUniqueIcon(ItemStack itemStack, int side, IIcon icon) {
        return icon;
    }

    /**
     * Returns icon for face.
     */
    protected IIcon getIcon(ItemStack itemStack, int side) {
        BlockProperties.prepareItemStackForRendering(itemStack);
        IIcon icon = renderBlocks.getIconSafe(
                getUniqueIcon(
                        itemStack,
                        side,
                        BlockProperties.toBlock(itemStack).getIcon(side, itemStack.getItemDamage())));

        if (hasIconOverride[side]) {
            icon = renderBlocks.getIconSafe(iconOverride[side]);
        }

        return icon;
    }

    /**
     * Renders multiple textures to side.
     */
    protected void renderMultiTexturedSide(ItemStack itemStack, int x, int y, int z, int side) {
        Block block = BlockProperties.toBlock(itemStack);
        boolean renderCover = (block instanceof BlockCoverable ? renderPass == 0 : block.canRenderInPass(renderPass));
        boolean hasDesign = TE.hasChiselDesign(coverRendering);
        boolean hasOverlay = TE.hasAttribute(TE.ATTR_OVERLAY[coverRendering]);
        double overlayOffset = 0.0D;

        if (hasOverlay) {
            if (hasDesign) {
                overlayOffset = RenderHelper.OFFSET_MAX;
            } else if (renderPass == PASS_OPAQUE && block.getRenderBlockPass() == PASS_ALPHA
                    && !(block instanceof BlockCoverable)) {
                        overlayOffset = RenderHelper.OFFSET_MIN;
                    }
        }

        /* Render side */

        if (renderCover) {
            int tempRotation = getTextureRotation(side);
            if (BlockProperties.blockRotates(itemStack)) {
                setTextureRotationForDirectionalBlock(side);
            }
            setColorAndRender(itemStack, x, y, z, side, getIcon(itemStack, side));
            setTextureRotation(side, tempRotation);
        }

        /* Render BlockGrass side overlay here, if needed. */

        if (renderPass == PASS_OPAQUE && block.equals(Blocks.grass) && side > 0 && !isPositiveFace(side)) {
            if (Minecraft.isFancyGraphicsEnabled()) {
                setColorAndRender(new ItemStack(Blocks.grass), x, y, z, side, BlockGrass.getIconSideOverlay());
            } else {
                setColorAndRender(new ItemStack(Blocks.dirt), x, y, z, side, IconRegistry.icon_overlay_fast_grass_side);
            }
        }

        boolean temp_dye_state = suppressDyeColor;
        suppressDyeColor = true;

        if (hasDesign && !suppressChiselDesign && renderPass == PASS_ALPHA) {
            renderHelper.setOffset(RenderHelper.OFFSET_MIN);
            renderChiselDesign(x, y, z, side);
            renderHelper.clearOffset();
        }

        if (hasOverlay && !suppressOverlay && renderPass == PASS_OPAQUE) {
            renderHelper.setOffset(overlayOffset);
            renderOverlay(x, y, z, side);
            renderHelper.clearOffset();
        }

        suppressDyeColor = temp_dye_state;
    }

    /**
     * Sets side icon, draws any attributes needed, and hands to appropriate render method. This will bypass typical
     * draw behavior if breaking animation is being drawn.
     */
    protected void delegateSideRender(ItemStack itemStack, int x, int y, int z, int side) {
        /*
         * A texture override in the context of this mod indicates the breaking animation is being drawn. If this is the
         * case, draw side without decorations. Can also check icon name for beginsWith("destroy_stage_").
         */
        if (renderBlocks.hasOverrideBlockTexture()) {
            setColorAndRender(itemStack, x, y, z, side, renderBlocks.overrideBlockTexture);
        } else {
            renderMultiTexturedSide(itemStack, x, y, z, side);
        }
    }

    /**
     * Renders overlay on side.
     */
    protected void renderOverlay(int x, int y, int z, int side) {
        side = isPositiveFace(side) ? 1 : side;
        Overlay overlay = OverlayHandler.getOverlayType(TE.getAttribute(TE.ATTR_OVERLAY[coverRendering]));

        IIcon icon = OverlayHandler.getOverlayIcon(overlay, side);

        if (icon != null) {
            setColorAndRender(overlay.getItemStack(), x, y, z, side, icon);
        }
    }

    /**
     * Renders chisel design on side.
     */
    protected void renderChiselDesign(int x, int y, int z, int side) {
        String design = TE.getChiselDesign(coverRendering);
        IIcon icon = renderBlocks
                .getIconSafe(IconRegistry.icon_design_chisel.get(DesignHandler.listChisel.indexOf(design)));
        setColorAndRender(new ItemStack(Blocks.glass), x, y, z, side, icon);
    }

    /**
     * Sets color, lightness, and brightness in {@link LightingHelper} and renders side.
     * <p>
     * Also calls {@link VertexHelper#postRender} and thus cannot be overridden.
     *
     * @param itemStack the cover ItemStack
     * @param block     the block inside the ItemStack
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param z         the z coordinate
     * @param side      the side currently being worked on
     * @param icon      the icon for the side
     * @return nothing
     */
    public final void setColorAndRender(ItemStack itemStack, int x, int y, int z, int side, IIcon icon) {
        int color = getBlockColor(BlockProperties.toBlock(itemStack), itemStack.getItemDamage(), x, y, z, side, icon);

        if (!suppressDyeColor && (TE.hasAttribute(TE.ATTR_DYE[coverRendering]) || hasDyeOverride)) {
            color = hasDyeOverride ? dyeOverride : DyeHandler.getColor(TE.getAttribute(TE.ATTR_DYE[coverRendering]));
        }

        lightingHelper.setupColor(x, y, z, side, color, icon);
        render(x, y, z, side, icon);
        renderHelper.postRender();
    }

    /**
     * Returns a integer with hex for 0xrrggbb for block. Color is most commonly different for {@link Blocks#grass}
     * <p>
     * If using our custom render helpers, be sure to use {@link #applyAnaglyph(float[])}.
     *
     * @param itemStack the cover {@link ItemStack}
     * @param block     the {@link Block} inside the {@link ItemStack}
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param z         the z coordinate
     * @return a integer with hex for 0xrrggbb
     */
    public int getBlockColor(Block block, int metadata, int x, int y, int z, int side, IIcon icon) {
        if (block.hasTileEntity(metadata)) {
            block = Blocks.dirt;
        }

        TE.setMetadata(metadata);
        int color = OptifineHandler.enableOptifineIntegration
                ? OptifineHandler.getColorMultiplier(block, renderBlocks.blockAccess, x, y, z)
                : block.colorMultiplier(renderBlocks.blockAccess, x, y, z);
        TE.restoreMetadata();

        if (block.equals(Blocks.grass) && !isPositiveFace(side) && !icon.equals(BlockGrass.getIconSideOverlay())) {
            color = 16777215;
        }

        return color;
    }

    /**
     * Returns whether side is considered a top face.
     *
     * @param TE    the {@link TEBase}
     * @param block the {@link Block}
     * @param side  the side
     * @param icon  the {@link IIcon}
     * @return true if positive y face
     */
    protected boolean isPositiveFace(int side) {
        return side == 1;
    }

    /**
     * Renders a side.
     *
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @param z    the z coordinate
     * @param side the side currently being worked on
     * @param icon the icon for the side
     * @return nothing
     */
    protected void render(int x, int y, int z, int side, IIcon icon) {
        switch (side) {
            case DOWN:
                renderHelper.renderFaceYNeg(renderBlocks, x, y, z, icon);
                break;
            case UP:
                renderHelper.renderFaceYPos(renderBlocks, x, y, z, icon);
                break;
            case NORTH:
                renderHelper.renderFaceZNeg(renderBlocks, x, y, z, icon);
                break;
            case SOUTH:
                renderHelper.renderFaceZPos(renderBlocks, x, y, z, icon);
                break;
            case WEST:
                renderHelper.renderFaceXNeg(renderBlocks, x, y, z, icon);
                break;
            case EAST:
                renderHelper.renderFaceXPos(renderBlocks, x, y, z, icon);
                break;
        }
    }

    /**
     * Renders single-sided face using current render bounds.
     * <p>
     * This is needed for panes and screens, and will render if current render pass and
     * {@link FeatureRegistry#enableAlphaPanes} are compatible.
     */
    protected void renderPane(IIcon icon, int x, int y, int z, ForgeDirection facing, boolean enableAO,
            boolean flipped) {
        if (renderPass == PASS_OPAQUE ? !FeatureRegistry.enableAlphaPanes : FeatureRegistry.enableAlphaPanes) {
            doLightAndRenderSide(icon, x, y, z, facing, enableAO);
            if (flipped) {
                doLightAndRenderSide(icon, x, y, z, facing.getOpposite(), enableAO);
            }
        }
    }

    /**
     * Lights and renders a side.
     * <p>
     * Used primarily for rendering panes to help cut down on code.
     *
     * @param icon
     * @param x
     * @param y
     * @param z
     * @param facing
     */
    private void doLightAndRenderSide(IIcon icon, int x, int y, int z, ForgeDirection facing, boolean enableAO) {
        ItemStack itemStack = new ItemStack(Blocks.glass);
        int blockColor = getBlockColor(Blocks.glass, 0, x, y, z, facing.ordinal(), null);

        boolean hasAO = renderBlocks.enableAO;
        renderBlocks.enableAO = enableAO;

        switch (facing) {
            case DOWN:
                lightingHelper.setupLightingYNeg(itemStack, x, y, z);
                lightingHelper.setupColor(x, y, z, facing.ordinal(), blockColor, null);
                renderHelper.renderFaceYNeg(renderBlocks, x, y, z, icon);
                break;
            case UP:
                lightingHelper.setupLightingYPos(itemStack, x, y, z);
                lightingHelper.setupColor(x, y, z, facing.ordinal(), blockColor, null);
                renderHelper.renderFaceYPos(renderBlocks, x, y, z, icon);
                break;
            case NORTH:
                lightingHelper.setupLightingZNeg(itemStack, x, y, z);
                lightingHelper.setupColor(x, y, z, facing.ordinal(), blockColor, null);
                renderHelper.renderFaceZNeg(renderBlocks, x, y, z, icon);
                break;
            case SOUTH:
                lightingHelper.setupLightingZPos(itemStack, x, y, z);
                lightingHelper.setupColor(x, y, z, facing.ordinal(), blockColor, null);
                renderHelper.renderFaceZPos(renderBlocks, x, y, z, icon);
                break;
            case WEST:
                lightingHelper.setupLightingXNeg(itemStack, x, y, z);
                lightingHelper.setupColor(x, y, z, facing.ordinal(), blockColor, null);
                renderHelper.renderFaceXNeg(renderBlocks, x, y, z, icon);
                break;
            case EAST:
                lightingHelper.setupLightingXPos(itemStack, x, y, z);
                lightingHelper.setupColor(x, y, z, facing.ordinal(), blockColor, null);
                renderHelper.renderFaceXPos(renderBlocks, x, y, z, icon);
                break;
            default: {}
        }

        renderBlocks.enableAO = hasAO;
    }

    /**
     * Blocks override this in order to render everything they need.
     */
    protected void renderCarpentersBlock(int x, int y, int z) {
        renderBlock(getCoverForRendering(), x, y, z);
    }

    /**
     * Sets renderBlocks enableAO state to true depending on rendering environment and block requirements.
     */
    public boolean getEnableAO(ItemStack itemStack) {
        Block block = BlockProperties.toBlock(itemStack);
        return Minecraft.isAmbientOcclusionEnabled() && !disableAO && block.getLightValue() == 0;
    }

    /**
     * Renders block. Coordinates may change since side covers render here.
     */
    protected void renderBlock(ItemStack itemStack, int x, int y, int z) {
        if (BlockProperties.toBlock(itemStack) == null) {
            return;
        }

        renderBlocks.enableAO = getEnableAO(itemStack);

        if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(renderBlocks.blockAccess, x, y - 1, z, DOWN)
                || renderBlocks.renderMinY > 0.0D) {
            lightingHelper.setupLightingYNeg(itemStack, x, y, z);
            delegateSideRender(itemStack, x, y, z, DOWN);
        }

        if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(renderBlocks.blockAccess, x, y + 1, z, UP)
                || renderBlocks.renderMaxY < 1.0D) {
            lightingHelper.setupLightingYPos(itemStack, x, y, z);
            delegateSideRender(itemStack, x, y, z, UP);
        }

        if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z - 1, NORTH)
                || renderBlocks.renderMinZ > 0.0D) {
            lightingHelper.setupLightingZNeg(itemStack, x, y, z);
            delegateSideRender(itemStack, x, y, z, NORTH);
        }

        if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z + 1, SOUTH)
                || renderBlocks.renderMaxZ < 1.0D) {
            lightingHelper.setupLightingZPos(itemStack, x, y, z);
            delegateSideRender(itemStack, x, y, z, SOUTH);
        }

        if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(renderBlocks.blockAccess, x - 1, y, z, WEST)
                || renderBlocks.renderMinX > 0.0D) {
            lightingHelper.setupLightingXNeg(itemStack, x, y, z);
            delegateSideRender(itemStack, x, y, z, WEST);
        }

        if (renderBlocks.renderAllFaces || srcBlock.shouldSideBeRendered(renderBlocks.blockAccess, x + 1, y, z, EAST)
                || renderBlocks.renderMaxX < 1.0D) {
            lightingHelper.setupLightingXPos(itemStack, x, y, z);
            delegateSideRender(itemStack, x, y, z, EAST);
        }

        renderBlocks.enableAO = false;
    }
}
