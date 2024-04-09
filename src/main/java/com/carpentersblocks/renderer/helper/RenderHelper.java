package com.carpentersblocks.renderer.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.carpentersblocks.data.FlowerPot;
import com.carpentersblocks.data.Slope;
import com.carpentersblocks.renderer.BlockHandlerBase;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.collapsible.CollapsibleUtil;
import com.carpentersblocks.util.flowerpot.FlowerPotProperties;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHelper extends VertexHelper {

    private boolean rotationOverride = false;
    protected int rotation;
    private double uMin;
    private double uMax;
    private double vMin;
    private double vMax;
    protected double xMin;
    protected double xMax;
    protected double yMin;
    protected double yMax;
    protected double zMin;
    protected double zMax;
    protected double uTL;
    protected double vTL;
    protected double uBL;
    protected double vBL;
    protected double uBR;
    protected double vBR;
    protected double uTR;
    protected double vTR;

    public static final double OFFSET_MAX = 2.0D / 1024.0D;
    public static final double OFFSET_MIN = 1.0D / 1024.0D;

    public double renderOffset;

    public void setOffset(double offset) {
        renderOffset = offset;
    }

    public void clearOffset() {
        renderOffset = 0.0D;
    }

    public void setTextureRotationOverride(int in_rotation) {
        rotationOverride = true;
        rotation = in_rotation;
    }

    public void clearTextureRotationOverride() {
        rotationOverride = false;
    }

    /**
     * Sets UV coordinates for each corner based on side rotation.
     */
    private void setCornerUV(double t_uTL, double t_vTL, double t_uBL, double t_vBL, double t_uBR, double t_vBR,
            double t_uTR, double t_vTR) {
        uTL = t_uTL;
        vTL = t_vTL;
        uBL = t_uBL;
        vBL = t_vBL;
        uBR = t_uBR;
        vBR = t_vBR;
        uTR = t_uTR;
        vTR = t_vTR;
    }

    /**
     * Will populate render bounds and icon u, v translations.
     */
    protected void prepareRender(RenderBlocks renderBlocks, ForgeDirection side, double x, double y, double z,
            IIcon icon) {
        /* Enforce default floating icons */

        if (icon == BlockGrass.getIconSideOverlay()
                || icon.getIconName().contains("overlay/overlay_") && icon.getIconName().endsWith("_side")) {
            setFloatingIcon();
        }

        /* Set render bounds with offset. */

        xMin = x + renderBlocks.renderMinX - renderOffset;
        xMax = x + renderBlocks.renderMaxX + renderOffset;
        yMin = y + renderBlocks.renderMinY - renderOffset;
        yMax = y + renderBlocks.renderMaxY + renderOffset;
        zMin = z + renderBlocks.renderMinZ - renderOffset;
        zMax = z + renderBlocks.renderMaxZ + renderOffset;

        // Sloppy way to help prevent z-fighting on sloped faces.
        // Working on a better solution...
        // if (BlockHandlerSloped.isSideSloped) {
        // switch (side) {
        // case DOWN:
        // yMin -= renderOffset;
        // yMax -= renderOffset;
        // break;
        // case UP:
        // yMin += renderOffset;
        // yMax += renderOffset;
        // break;
        // case NORTH:
        // zMin -= renderOffset;
        // zMax -= renderOffset;
        // break;
        // case SOUTH:
        // zMin += renderOffset;
        // zMax += renderOffset;
        // break;
        // case WEST:
        // xMin -= renderOffset;
        // xMax -= renderOffset;
        // break;
        // case EAST:
        // xMin += renderOffset;
        // xMax += renderOffset;
        // break;
        // default: {}
        // }
        // }

        /* Set u, v for icon with rotation. */

        if (!rotationOverride) {
            switch (side) {
                case DOWN:
                    rotation = renderBlocks.uvRotateBottom;
                    break;
                case UP:
                    rotation = renderBlocks.uvRotateTop;
                    break;
                case NORTH:
                    rotation = renderBlocks.uvRotateNorth;
                    break;
                case SOUTH:
                    rotation = renderBlocks.uvRotateSouth;
                    break;
                case WEST:
                    rotation = renderBlocks.uvRotateWest;
                    break;
                case EAST:
                    rotation = renderBlocks.uvRotateEast;
                    break;
                default: {}
            }
        }

        switch (side) {
            case DOWN:
                switch (rotation) {
                    case 0:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMinX * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMaxX * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinZ * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxZ * 16.0D);

                        setCornerUV(uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax);

                        break;

                    case 1:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxZ * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMinZ * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinX * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxX * 16.0D);

                        setCornerUV(uMin, vMax, uMax, vMax, uMax, vMin, uMin, vMin);

                        break;

                    case 2:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMinX * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxX * 16.0D);
                        vMin = icon.getInterpolatedV(16.0D - renderBlocks.renderMinZ * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - renderBlocks.renderMaxZ * 16.0D);

                        setCornerUV(uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax);

                        break;

                    case 3:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMaxZ * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMinZ * 16.0D);
                        vMin = icon.getInterpolatedV(16.0D - renderBlocks.renderMinX * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - renderBlocks.renderMaxX * 16.0D);

                        setCornerUV(uMin, vMax, uMax, vMax, uMax, vMin, uMin, vMin);

                        break;
                }

                break;

            case UP:
                switch (rotation) {
                    case 0:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMinX * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMaxX * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinZ * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxZ * 16.0D);

                        setCornerUV(uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax);

                        break;

                    case 1:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMaxZ * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMinZ * 16.0D);
                        vMin = icon.getInterpolatedV(16.0D - renderBlocks.renderMinX * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - renderBlocks.renderMaxX * 16.0D);

                        setCornerUV(uMin, vMax, uMax, vMax, uMax, vMin, uMin, vMin);

                        break;

                    case 2:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMinX * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxX * 16.0D);
                        vMin = icon.getInterpolatedV(16.0D - renderBlocks.renderMinZ * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - renderBlocks.renderMaxZ * 16.0D);

                        setCornerUV(uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax);

                        break;

                    case 3:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxZ * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMinZ * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinX * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxX * 16.0D);

                        setCornerUV(uMin, vMax, uMax, vMax, uMax, vMin, uMin, vMin);

                        break;
                }

                break;

            case NORTH:
                switch (rotation) {
                    case 0:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxX * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMinX * 16.0D);
                        vMin = icon.getInterpolatedV(
                                16.0D - (floatingIcon ? 1.0D - (renderBlocks.renderMaxY - renderBlocks.renderMinY)
                                        : renderBlocks.renderMinY) * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - (floatingIcon ? 1.0D : renderBlocks.renderMaxY) * 16.0D);

                        setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax);

                        break;

                    case 1:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxY * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMinY * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMaxX * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMinX * 16.0D);

                        setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax);

                        break;

                    case 2:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMaxX * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMinX * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinY * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxY * 16.0D);

                        setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax);

                        break;

                    case 3:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMaxY * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMinY * 16.0D);
                        vMin = icon.getInterpolatedV(16.0D - renderBlocks.renderMaxX * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - renderBlocks.renderMinX * 16.0D);

                        setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax);

                        break;
                }

                break;

            case SOUTH:
                switch (rotation) {
                    case 0:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMinX * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMaxX * 16.0D);
                        vMin = icon.getInterpolatedV(
                                16.0D - (floatingIcon ? 1.0D - (renderBlocks.renderMaxY - renderBlocks.renderMinY)
                                        : renderBlocks.renderMinY) * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - (floatingIcon ? 1.0D : renderBlocks.renderMaxY) * 16.0D);

                        setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax);

                        break;

                    case 1:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxY * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMinY * 16.0D);
                        vMin = icon.getInterpolatedV(16.0D - renderBlocks.renderMinX * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - renderBlocks.renderMaxX * 16.0D);

                        setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax);

                        break;

                    case 2:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMinX * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxX * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinY * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxY * 16.0D);

                        setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax);

                        break;

                    case 3:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMaxY * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMinY * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinX * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxX * 16.0D);

                        setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax);

                        break;
                }

                break;

            case WEST:
                switch (rotation) {
                    case 0:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMinZ * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMaxZ * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - (floatingIcon ? 1.0D : renderBlocks.renderMaxY) * 16.0D);
                        vMin = icon.getInterpolatedV(
                                16.0D - (floatingIcon ? 1.0D - (renderBlocks.renderMaxY - renderBlocks.renderMinY)
                                        : renderBlocks.renderMinY) * 16.0D);

                        setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax);

                        break;

                    case 1:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxY * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMinY * 16.0D);
                        vMin = icon.getInterpolatedV(16.0D - renderBlocks.renderMinZ * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - renderBlocks.renderMaxZ * 16.0D);

                        setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax);

                        break;

                    case 2:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMinZ * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxZ * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinY * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxY * 16.0D);

                        setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax);

                        break;

                    case 3:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMaxY * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMinY * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinZ * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxZ * 16.0D);

                        setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax);

                        break;
                }

                break;

            case EAST:
                switch (rotation) {
                    case 0:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxZ * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMinZ * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - (floatingIcon ? 1.0D : renderBlocks.renderMaxY) * 16.0D);
                        vMin = icon.getInterpolatedV(
                                16.0D - (floatingIcon ? 1.0D - (renderBlocks.renderMaxY - renderBlocks.renderMinY)
                                        : renderBlocks.renderMinY) * 16.0D);

                        setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax);

                        break;

                    case 1:
                        uMin = icon.getInterpolatedU(16.0D - renderBlocks.renderMaxY * 16.0D);
                        uMax = icon.getInterpolatedU(16.0D - renderBlocks.renderMinY * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMaxZ * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMinZ * 16.0D);

                        setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax);

                        break;

                    case 2:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMaxZ * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMinZ * 16.0D);
                        vMin = icon.getInterpolatedV(renderBlocks.renderMinY * 16.0D);
                        vMax = icon.getInterpolatedV(renderBlocks.renderMaxY * 16.0D);

                        setCornerUV(uMin, vMax, uMin, vMin, uMax, vMin, uMax, vMax);

                        break;

                    case 3:
                        uMin = icon.getInterpolatedU(renderBlocks.renderMaxY * 16.0D);
                        uMax = icon.getInterpolatedU(renderBlocks.renderMinY * 16.0D);
                        vMin = icon.getInterpolatedV(16.0D - renderBlocks.renderMaxZ * 16.0D);
                        vMax = icon.getInterpolatedV(16.0D - renderBlocks.renderMinZ * 16.0D);

                        setCornerUV(uMin, vMin, uMax, vMin, uMax, vMax, uMin, vMax);

                        break;
                }

                break;

            default: {}
        }
    }

    /**
     * Renders the given texture to the bottom face of the block. Args: slope, x, y, z, texture
     */
    public void renderFaceYNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.DOWN, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMin, zMax, uTR, vTR, SOUTHWEST);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
        setupVertex(renderBlocks, xMax, yMin, zMax, uTL, vTL, SOUTHEAST);
    }

    /**
     * Renders the given texture to the top face of the block. Args: slope, x, y, z, texture
     */
    public void renderFaceYPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.UP, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
        setupVertex(renderBlocks, xMax, yMax, zMin, uBL, vBL, NORTHEAST);
        setupVertex(renderBlocks, xMin, yMax, zMin, uBR, vBR, NORTHWEST);
        setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
    }

    /**
     * Renders the given texture to the North face of the block. Args: slope, x, y, z, texture
     */
    public void renderFaceZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, TOP_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, TOP_RIGHT);
    }

    /**
     * Renders the given texture to the South face of the block. Args: slope, x, y, z, texture
     */
    public void renderFaceZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, TOP_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, TOP_RIGHT);
    }

    /**
     * Renders the given texture to the West face of the block. Args: slope, x, y, z, texture
     */
    public void renderFaceXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, TOP_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, TOP_RIGHT);
    }

    /**
     * Renders the given texture to the East face of the block. Args: slope, x, y, z, texture
     */
    public void renderFaceXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, TOP_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, TOP_RIGHT);
    }

    /**
     * Renders a half triangle on the North left face of the block.
     */
    public void triangleRenderFaceZNegXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMin, zMin, uBL, floatingIcon ? vTL : vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, TOP_RIGHT);
    }

    /**
     * Renders a half triangle on the North right face of the block.
     */
    public void triangleRenderFaceZNegXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, TOP_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBR, floatingIcon ? vTR : vBR, BOTTOM_RIGHT);
    }

    /**
     * Renders a half triangle on the South left face of the block.
     */
    public void triangleRenderFaceZPosXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMin, zMax, uBL, floatingIcon ? vTL : vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, TOP_RIGHT);
    }

    /**
     * Renders a half triangle on the South right face of the block.
     */
    public void triangleRenderFaceZPosXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBR, floatingIcon ? vTR : vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, TOP_LEFT);
    }

    /**
     * Renders a half triangle on the West left face of the block.
     */
    public void triangleRenderFaceXNegZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMin, zMin, uBL, floatingIcon ? vTL : vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, TOP_RIGHT);
    }

    /**
     * Renders a half triangle on the West right face of the block.
     */
    public void triangleRenderFaceXNegZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, TOP_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMin, yMin, zMax, uBR, floatingIcon ? vTR : vBR, BOTTOM_RIGHT);
    }

    /**
     * Renders a half triangle on the East left face of the block.
     */
    public void triangleRenderFaceXPosZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMin, zMax, uBL, floatingIcon ? vTL : vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
        setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, TOP_RIGHT);
    }

    /**
     * Renders a half triangle on the East right face of the block.
     */
    public void triangleRenderFaceXPosZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, TOP_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBR, floatingIcon ? vTR : vBR, BOTTOM_RIGHT);
    }

    /**
     * Renders the given texture to the bottom face of the block.
     */
    public void orthoWedgeRenderFaceYNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.DOWN, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_OBL_EXT_POS_NW:
            case Slope.ID_OBL_INT_NEG_NW:
            case Slope.ID_WEDGE_NW:
                setupVertex(renderBlocks, xMin, yMin, zMax, uTR, vTR, SOUTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uTL, vTL, SOUTHEAST);
                break;
            case Slope.ID_OBL_EXT_POS_SW:
            case Slope.ID_OBL_INT_NEG_SW:
            case Slope.ID_WEDGE_SW:
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uTL, vTL, SOUTHEAST);
                break;
            case Slope.ID_OBL_EXT_POS_NE:
            case Slope.ID_OBL_INT_NEG_NE:
            case Slope.ID_WEDGE_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uTR, vTR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uTL, vTL, SOUTHEAST);
                break;
            case Slope.ID_OBL_EXT_POS_SE:
            case Slope.ID_OBL_INT_NEG_SE:
            case Slope.ID_WEDGE_SE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uTR, vTR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                break;
        }
    }

    /**
     * Renders the given texture to the top face of the block.
     */
    public void orthoWedgeRenderFaceYPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.UP, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_OBL_EXT_NEG_NW:
            case Slope.ID_OBL_INT_POS_NW:
            case Slope.ID_WEDGE_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
            case Slope.ID_OBL_EXT_NEG_SW:
            case Slope.ID_OBL_INT_POS_SW:
            case Slope.ID_WEDGE_SW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uBR, vBR, NORTHWEST);
                break;
            case Slope.ID_OBL_EXT_NEG_NE:
            case Slope.ID_OBL_INT_POS_NE:
            case Slope.ID_WEDGE_NE:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
            case Slope.ID_OBL_EXT_NEG_SE:
            case Slope.ID_OBL_INT_POS_SE:
            case Slope.ID_WEDGE_SE:
                setupVertex(renderBlocks, xMax, yMax, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
        }
    }

    /**
     * Renders the given texture to the North face of the block.
     */
    public void orthoWedgeRenderFaceZNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        Slope slope = Slope.getSlopeById(slopeID);

        if (slope.isPositive) {
            if (slope.facings.contains(ForgeDirection.WEST)) {
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, floatingIcon ? vTR : vBR, BOTTOM_RIGHT);
            } else {
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, floatingIcon ? vTL : vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, TOP_RIGHT);
            }
        } else {
            if (slope.facings.contains(ForgeDirection.WEST)) {
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, TOP_RIGHT);
            } else {
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, TOP_RIGHT);
            }
        }
    }

    /**
     * Renders the given texture to the South face of the block.
     */
    public void orthoWedgeRenderFaceZPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        Slope slope = Slope.getSlopeById(slopeID);

        if (slope.isPositive) {
            if (slope.facings.contains(ForgeDirection.WEST)) {
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, floatingIcon ? vTL : vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, TOP_RIGHT);
            } else {
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, floatingIcon ? vTR : vBR, BOTTOM_RIGHT);
            }
        } else {
            if (slope.facings.contains(ForgeDirection.WEST)) {
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, TOP_RIGHT);
            } else {
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, TOP_RIGHT);
            }
        }
    }

    /**
     * Renders the given texture to the West face of the block.
     */
    public void orthoWedgeRenderFaceXNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        Slope slope = Slope.getSlopeById(slopeID);

        if (slope.isPositive) {
            if (slope.facings.contains(ForgeDirection.NORTH)) {
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, floatingIcon ? vTL : vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, TOP_RIGHT);
            } else {
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, floatingIcon ? vTR : vBR, BOTTOM_RIGHT);
            }
        } else {
            if (slope.facings.contains(ForgeDirection.NORTH)) {
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, TOP_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
            } else {
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, TOP_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
            }
        }
    }

    /**
     * Renders the given texture to the East face of the block.
     */
    public void orthoWedgeRenderFaceXPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        Slope slope = Slope.getSlopeById(slopeID);

        if (slope.isPositive) {
            if (slope.facings.contains(ForgeDirection.NORTH)) {
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, floatingIcon ? vTR : vBR, BOTTOM_RIGHT);
            } else {
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, floatingIcon ? vTL : vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, TOP_RIGHT);
            }
        } else {
            if (slope.facings.contains(ForgeDirection.NORTH)) {
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, TOP_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, TOP_LEFT);
            } else {
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, TOP_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, TOP_LEFT);
            }
        }
    }

    /**
     * Renders the given texture to the negative North slope of the block.
     */
    public void prismRenderSlopeYNegZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        double uBM = uBR - (uBR - uBL) / 2;
        double xMid = xMax - (xMax - xMin) / 2;

        setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
        setupVertex(renderBlocks, xMid, yMin, zMax, uBM, vBR, TOP_CENTER);
        setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
    }

    /**
     * Renders the given texture to the negative South slope of the block.
     */
    public void prismRenderSlopeYNegZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        double uBM = uBR - (uBR - uBL) / 2;
        double xMid = xMax - (xMax - xMin) / 2;

        setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
        setupVertex(renderBlocks, xMid, yMin, zMin, uBM, vBR, BOTTOM_CENTER);
        setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
    }

    /**
     * Renders the given texture to the negative West slope of the block.
     */
    public void prismRenderSlopeYNegXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        double uBM = uBR - (uBR - uBL) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
        setupVertex(renderBlocks, xMax, yMin, zMid, uBM, vBR, LEFT_CENTER);
        setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
    }

    /**
     * Renders the given texture to the negative East slope of the block.
     */
    public void prismRenderSlopeYNegXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        double uBM = uBR - (uBR - uBL) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
        setupVertex(renderBlocks, xMin, yMin, zMid, uBM, vBR, RIGHT_CENTER);
        setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
    }

    /**
     * Renders the given texture to the positive North slope of the block.
     */
    public void prismRenderSlopeYPosZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        double uTM = uTR - (uTR - uTL) / 2;
        double xMid = xMax - (xMax - xMin) / 2;

        setupVertex(renderBlocks, xMid, yMax, zMax, uTM, vTL, TOP_CENTER);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
    }

    /**
     * Renders the given texture to the positive South slope of the block.
     */
    public void prismRenderSlopeYPosZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        double uTM = uTR - (uTR - uTL) / 2;
        double xMid = xMax - (xMax - xMin) / 2;

        setupVertex(renderBlocks, xMid, yMax, zMin, uTM, vTL, BOTTOM_CENTER);
        setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
    }

    /**
     * Renders the given texture to the positive West slope of the block.
     */
    public void prismRenderSlopeYPosXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        double uTM = uTR - (uTR - uTL) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        setupVertex(renderBlocks, xMax, yMax, zMid, uTM, vTL, LEFT_CENTER);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
        setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
    }

    /**
     * Renders the given texture to the positive East slope of the block.
     */
    public void prismRenderSlopeYPosXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        double uTM = uTR - (uTR - uTL) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        setupVertex(renderBlocks, xMin, yMax, zMid, uTM, vTR, RIGHT_CENTER);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
    }

    /**
     * Renders the given texture to the West prism on the North face.
     */
    public void prismRenderWestPointSlopeZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
        setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
    }

    /**
     * Renders the given texture to the West prism on the South face.
     */
    public void prismRenderWestPointSlopeZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
        setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
        setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
    }

    /**
     * Renders the given texture to the East prism on the North face.
     */
    public void prismRenderEastPointSlopeZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
        setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
    }

    /**
     * Renders the given texture to the East prism on the South face.
     */
    public void prismRenderEastPointSlopeZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
        setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
    }

    /**
     * Renders the given texture to the North prism on the West face.
     */
    public void prismRenderNorthPointSlopeXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
        setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
        setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
    }

    /**
     * Renders the given texture to the North prism on the East face.
     */
    public void prismRenderNorthPointSlopeXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
        setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
        setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
    }

    /**
     * Renders the given texture to the South prism on the West face.
     */
    public void prismRenderSouthPointSlopeXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
        setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
        setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
    }

    /**
     * Renders the given texture to the South prism on the East face.
     */
    public void prismRenderSouthPointSlopeXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
        setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
        setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
    }

    /**
     * Renders the given texture to the North sloped face of the block. Args: slope, x, y, z, texture
     */
    public void oblWedgeRenderSlopeZNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_WEDGE_POS_N:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_NEG_N:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_NW:
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, TOP_RIGHT);
                break;
            case Slope.ID_WEDGE_NE:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, TOP_RIGHT);
                break;
        }
    }

    /**
     * Renders the given texture to the South sloped face of the block. Args: slope, x, y, z, texture
     */
    public void oblWedgeRenderSlopeZPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_WEDGE_POS_S:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_NEG_S:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_SW:
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, TOP_RIGHT);
                break;
            case Slope.ID_WEDGE_SE:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, TOP_RIGHT);
                break;
        }
    }

    /**
     * Renders the given texture to the West sloped face of the block. Args: slope, x, y, z, texture
     */
    public void oblWedgeRenderSlopeXNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_WEDGE_POS_W:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_NEG_W:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                break;
        }
    }

    /**
     * Renders the given texture to the East sloped face of the block. Args: slope, x, y, z, texture
     */
    public void oblWedgeRenderSlopeXPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_WEDGE_POS_E:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_NEG_E:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                break;
        }
    }

    /**
     * Renders the given texture to the North sloped face of the block.
     */
    public void prismRenderWedgeSlopeZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        oblWedgeRenderSlopeZNeg(renderBlocks, Slope.ID_WEDGE_POS_N, x, y, z, icon);
    }

    /**
     * Renders the given texture to the South sloped face of the block.
     */
    public void prismRenderWedgeSlopeZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        oblWedgeRenderSlopeZPos(renderBlocks, Slope.ID_WEDGE_POS_S, x, y, z, icon);
    }

    /**
     * Renders the given texture to the West sloped face of the block.
     */
    public void prismRenderWedgeSlopeXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        oblWedgeRenderSlopeXNeg(renderBlocks, Slope.ID_WEDGE_POS_W, x, y, z, icon);
    }

    /**
     * Renders the given texture to the East sloped face of the block.
     */
    public void prismRenderWedgeSlopeXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        oblWedgeRenderSlopeXPos(renderBlocks, Slope.ID_WEDGE_POS_E, x, y, z, icon);
    }

    /**
     * Renders the given texture to interior oblique on the bottom sloped face.
     */
    public void obliqueRenderIntObliqueYNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        double uMI = uTR - (uTR - uTL) / 2;
        double vMI = rotation % 2 == 0 ? vTL : (vBR - (vBR - vBL) / 2);

        switch (slopeID) {
            case Slope.ID_OBL_INT_NEG_NW:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uMI, vMI, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                break;
            case Slope.ID_OBL_INT_NEG_SW:
                setupVertex(renderBlocks, xMin, yMax, zMax, uMI, vMI, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                break;
            case Slope.ID_OBL_INT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uMI, vMI, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                break;
            case Slope.ID_OBL_INT_NEG_SE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uMI, vMI, SOUTHEAST);
                break;
        }
    }

    /**
     * Renders the given texture to interior oblique on the top sloped face.
     */
    public void obliqueRenderIntObliqueYPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        boolean altRot = rotation % 2 == 0;
        double uMI = !altRot ? uBL : (uTR - (uTR - uTL) / 2);
        double vMI = altRot ? vBR : (vBR - (vBR - vBL) / 2);

        switch (slopeID) {
            case Slope.ID_OBL_INT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uMI, vMI, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
            case Slope.ID_OBL_INT_POS_SW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uMI, vMI, SOUTHWEST);
                break;
            case Slope.ID_OBL_INT_POS_NE:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uMI, vMI, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                break;
            case Slope.ID_OBL_INT_POS_SE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uMI, vMI, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                break;
        }
    }

    /**
     * Renders the given texture to exterior oblique bottom face on right.
     */
    public void obliqueRenderExtObliqueYNegLeft(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        double uTOP_MIDDLE = uTR;
        double uTOP_RIGHT_MIDDLE = uTOP_MIDDLE - (uTR - uTL) / 2;

        double xMid = xMax - (xMax - xMin) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        switch (slopeID) {
            case Slope.ID_OBL_EXT_NEG_NW:
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_MIDDLE, vTR, TOP_CENTER);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTOP_RIGHT_MIDDLE, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMax, uTOP_MIDDLE, vBL, BOTTOM_LEFT);
                break;
            case Slope.ID_OBL_EXT_NEG_SW:
                setupVertex(renderBlocks, xMin, yMax, zMin, uTOP_RIGHT_MIDDLE, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMin, uTOP_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_MIDDLE, vTR, TOP_CENTER);
                break;
            case Slope.ID_OBL_EXT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uTOP_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_MIDDLE, vTR, TOP_CENTER);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTOP_RIGHT_MIDDLE, vTL, TOP_LEFT);
                break;
            case Slope.ID_OBL_EXT_NEG_SE:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTOP_RIGHT_MIDDLE, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uTOP_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_MIDDLE, vTR, TOP_CENTER);
                break;
        }
    }

    /**
     * Renders the given texture to exterior oblique bottom face on right.
     */
    public void obliqueRenderExtObliqueYNegRight(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        double uTOP_MIDDLE = uTR - (uTR - uTL) / 2;
        double uTOP_LEFT_MIDDLE = uTOP_MIDDLE - (uTR - uTL) / 2;

        double xMid = xMax - (xMax - xMin) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        switch (slopeID) {
            case Slope.ID_OBL_EXT_NEG_NW:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTOP_MIDDLE, vTR, TOP_RIGHT);
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_LEFT_MIDDLE, vTL, TOP_CENTER);
                setupVertex(renderBlocks, xMax, yMin, zMax, uTOP_LEFT_MIDDLE, vBL, BOTTOM_LEFT);
                break;
            case Slope.ID_OBL_EXT_NEG_SW:
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_LEFT_MIDDLE, vTL, TOP_CENTER);
                setupVertex(renderBlocks, xMax, yMin, zMin, uTOP_LEFT_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTOP_MIDDLE, vTR, TOP_RIGHT);
                break;
            case Slope.ID_OBL_EXT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uTOP_LEFT_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTOP_MIDDLE, vTR, TOP_RIGHT);
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_LEFT_MIDDLE, vTL, TOP_CENTER);
                break;
            case Slope.ID_OBL_EXT_NEG_SE:
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_LEFT_MIDDLE, vTL, TOP_CENTER);
                setupVertex(renderBlocks, xMin, yMin, zMin, uTOP_LEFT_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTOP_MIDDLE, vTR, TOP_RIGHT);
                break;
        }
    }

    /**
     * Renders the given texture to exterior oblique top face on left.
     */
    public void obliqueRenderExtObliqueYPosLeft(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        double uMI1 = uBR; // u middle coordinate, left triangle
        double uMI2 = uMI1 - (uBR - uBL) / 2; // u middle coordinate, right triangle

        double xMid = xMax - (xMax - xMin) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        switch (slopeID) {
            case Slope.ID_OBL_EXT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uMI1, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMax, yMin, zMin, uMI2, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMin, zMid, uMI1, vBR, BOTTOM_CENTER);
                break;
            case Slope.ID_OBL_EXT_POS_SW:
                setupVertex(renderBlocks, xMid, yMin, zMid, uMI1, vBR, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMax, yMax, zMin, uMI1, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMin, uMI2, vBL, BOTTOM_LEFT);
                break;
            case Slope.ID_OBL_EXT_POS_NE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uMI2, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMin, zMid, uMI1, vBR, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMin, yMax, zMax, uMI1, vTL, TOP_LEFT);
                break;
            case Slope.ID_OBL_EXT_POS_SE:
                setupVertex(renderBlocks, xMin, yMax, zMin, uMI1, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMin, yMin, zMax, uMI2, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMin, zMid, uMI1, vBR, BOTTOM_CENTER);
                break;
        }
    }

    /**
     * Renders the given texture to exterior oblique top face on right.
     */
    public void obliqueRenderExtObliqueYPosRight(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        double uBOTTOM_MIDDLE = uBR - (uBR - uBL) / 2;
        double uBOTTOM_LEFT_MIDDLE = uBOTTOM_MIDDLE - (uBR - uBL) / 2;

        double xMid = xMax - (xMax - xMin) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        switch (slopeID) {
            case Slope.ID_OBL_EXT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uBOTTOM_LEFT_MIDDLE, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMid, yMin, zMid, uBOTTOM_LEFT_MIDDLE, vBL, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBOTTOM_MIDDLE, vBR, BOTTOM_RIGHT);
                break;
            case Slope.ID_OBL_EXT_POS_SW:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBOTTOM_MIDDLE, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMax, yMax, zMin, uBOTTOM_LEFT_MIDDLE, vTL, TOP_LEFT);
                setupVertex(renderBlocks, xMid, yMin, zMid, uBOTTOM_LEFT_MIDDLE, vBL, BOTTOM_CENTER);
                break;
            case Slope.ID_OBL_EXT_POS_NE:
                setupVertex(renderBlocks, xMid, yMin, zMid, uBOTTOM_LEFT_MIDDLE, vBL, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBOTTOM_MIDDLE, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMax, uBOTTOM_LEFT_MIDDLE, vTL, TOP_LEFT);
                break;
            case Slope.ID_OBL_EXT_POS_SE:
                setupVertex(renderBlocks, xMid, yMin, zMid, uBOTTOM_LEFT_MIDDLE, vBL, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBOTTOM_MIDDLE, vBR, BOTTOM_RIGHT);
                setupVertex(renderBlocks, xMin, yMax, zMin, uBOTTOM_LEFT_MIDDLE, vTL, TOP_LEFT);
                break;
        }
    }

    /**
     * Renders the given texture to the North sloped face of the block.
     */
    public void cornerRenderSlopeZNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_WEDGE_INT_NEG_NW:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_INT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_EXT_NEG_NW:
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_EXT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                break;
            case Slope.ID_WEDGE_INT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_INT_POS_NE:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_EXT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                break;
            case Slope.ID_WEDGE_EXT_POS_NE:
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
        }
    }

    /**
     * Renders the given texture to the South sloped face of the block.
     */
    public void cornerRenderSlopeZPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_WEDGE_INT_NEG_SW:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                break;
            case Slope.ID_WEDGE_INT_NEG_SE:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                break;
            case Slope.ID_WEDGE_EXT_NEG_SW:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_EXT_NEG_SE:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_INT_POS_SW:
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_INT_POS_SE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                break;
            case Slope.ID_WEDGE_EXT_POS_SW:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_EXT_POS_SE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                break;
        }
    }

    /**
     * Renders the given texture to the West sloped face of the block.
     */
    public void cornerRenderSlopeXNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_WEDGE_INT_NEG_NW:
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_INT_NEG_SW:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_EXT_NEG_NW:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_EXT_NEG_SW:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                break;
            case Slope.ID_WEDGE_INT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                break;
            case Slope.ID_WEDGE_INT_POS_SW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_EXT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_EXT_POS_SW:
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                break;
        }
    }

    /**
     * Renders the given texture to the East sloped face of the block.
     */
    public void cornerRenderSlopeXPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z,
            IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        switch (slopeID) {
            case Slope.ID_WEDGE_INT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                break;
            case Slope.ID_WEDGE_INT_NEG_SE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_EXT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_EXT_NEG_SE:
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                break;
            case Slope.ID_WEDGE_INT_POS_NE:
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_INT_POS_SE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_EXT_POS_NE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                break;
            case Slope.ID_WEDGE_EXT_POS_SE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                break;
        }
    }

    /**
     * Renders the given texture to the bottom North slope.
     */
    public void collapsibleRenderSlopeYNegZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.DOWN, x, y, z, icon);

        double uTM = uTR - (uTR - uTL) / 2;
        double xMid = xMax - (xMax - xMin) / 2;

        setupVertex(renderBlocks, xMin, y + 1.0D - CollapsibleUtil.offset_XZNN, zMin, uBR, vBR, NORTHWEST);
        setupVertex(renderBlocks, xMax, y + 1.0D - CollapsibleUtil.offset_XZPN, zMin, uBL, vBL, NORTHEAST);
        setupVertex(renderBlocks, xMid, y + 1.0D - CollapsibleUtil.CENTER_YMAX, zMax, uTM, vTR, TOP_CENTER);
    }

    /**
     * Renders the given texture to the bottom South slope.
     */
    public void collapsibleRenderSlopeYNegZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.DOWN, x, y, z, icon);

        double uBM = uBR - (uBR - uBL) / 2;
        double xMid = xMax - (xMax - xMin) / 2;

        setupVertex(renderBlocks, xMin, y + 1.0D - CollapsibleUtil.offset_XZNP, zMax, uTR, vTR, SOUTHWEST);
        setupVertex(renderBlocks, xMid, y + 1.0D - CollapsibleUtil.CENTER_YMAX, zMin, uBM, vBR, BOTTOM_CENTER);
        setupVertex(renderBlocks, xMax, y + 1.0D - CollapsibleUtil.offset_XZPP, zMax, uTL, vTL, SOUTHEAST);
    }

    /**
     * Renders the given texture to the top North slope.
     */
    public void collapsibleRenderSlopeYPosZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.UP, x, y, z, icon);

        double uTM = uTR - (uTR - uTL) / 2;
        double xMid = xMax - (xMax - xMin) / 2;

        setupVertex(renderBlocks, xMid, y + CollapsibleUtil.CENTER_YMAX, zMax, uTM, vTR, TOP_CENTER);
        setupVertex(renderBlocks, xMax, y + CollapsibleUtil.offset_XZPN, zMin, uBL, vBL, NORTHEAST);
        setupVertex(renderBlocks, xMin, y + CollapsibleUtil.offset_XZNN, zMin, uBR, vBR, NORTHWEST);
    }

    /**
     * Renders the given texture to the top South slope.
     */
    public void collapsibleRenderSlopeYPosZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.UP, x, y, z, icon);

        double uBM = uBR - (uBR - uBL) / 2;
        double xMid = xMax - (xMax - xMin) / 2;

        setupVertex(renderBlocks, xMax, y + CollapsibleUtil.offset_XZPP, zMax, uTL, vTL, SOUTHEAST);
        setupVertex(renderBlocks, xMid, y + CollapsibleUtil.CENTER_YMAX, zMin, uBM, vBR, BOTTOM_CENTER);
        setupVertex(renderBlocks, xMin, y + CollapsibleUtil.offset_XZNP, zMax, uTR, vTR, SOUTHWEST);
    }

    /**
     * Renders the given texture to the top West slope.
     */
    public void collapsibleRenderSlopeXNegYNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.DOWN, x, y, z, icon);

        double vLM = vBL - (vBL - vTL) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        setupVertex(renderBlocks, xMin, y + 1.0D - CollapsibleUtil.offset_XZNP, zMax, uTR, vTR, SOUTHWEST);
        setupVertex(renderBlocks, xMin, y + 1.0D - CollapsibleUtil.offset_XZNN, zMin, uBR, vBR, NORTHWEST);
        setupVertex(renderBlocks, xMax, y + 1.0D - CollapsibleUtil.CENTER_YMAX, zMid, uBL, vLM, LEFT_CENTER);
    }

    /**
     * Renders the given texture to the top East slope.
     */
    public void collapsibleRenderSlopeXPosYNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.DOWN, x, y, z, icon);

        double vRM = vBR - (vBR - vTR) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        setupVertex(renderBlocks, xMin, y + 1.0D - CollapsibleUtil.CENTER_YMAX, zMid, uBR, vRM, RIGHT_CENTER);
        setupVertex(renderBlocks, xMax, y + 1.0D - CollapsibleUtil.offset_XZPN, zMin, uBL, vBL, NORTHEAST);
        setupVertex(renderBlocks, xMax, y + 1.0D - CollapsibleUtil.offset_XZPP, zMax, uTL, vTL, SOUTHEAST);
    }

    /**
     * Renders the given texture to the top West slope.
     */
    public void collapsibleRenderSlopeXNegYPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.UP, x, y, z, icon);

        double vLM = vBL - (vBL - vTL) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        setupVertex(renderBlocks, xMax, y + CollapsibleUtil.CENTER_YMAX, zMid, uBL, vLM, LEFT_CENTER);
        setupVertex(renderBlocks, xMin, y + CollapsibleUtil.offset_XZNN, zMin, uBR, vBR, NORTHWEST);
        setupVertex(renderBlocks, xMin, y + CollapsibleUtil.offset_XZNP, zMax, uTR, vTR, SOUTHWEST);
    }

    /**
     * Renders the given texture to the top East slope.
     */
    public void collapsibleRenderSlopeXPosYPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon) {
        prepareRender(renderBlocks, ForgeDirection.UP, x, y, z, icon);

        double vRM = vBR - (vBR - vTR) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        setupVertex(renderBlocks, xMax, y + CollapsibleUtil.offset_XZPP, zMax, uTL, vTL, SOUTHEAST);
        setupVertex(renderBlocks, xMax, y + CollapsibleUtil.offset_XZPN, zMin, uBL, vBL, NORTHEAST);
        setupVertex(renderBlocks, xMin, y + CollapsibleUtil.CENTER_YMAX, zMid, uBR, vRM, RIGHT_CENTER);
    }

    /**
     * Renders the given texture to the North face of the block.
     */
    public void collapsibleRenderFaceZNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon,
            boolean isPositive) {
        prepareRender(renderBlocks, ForgeDirection.NORTH, x, y, z, icon);

        if (isPositive) {

            if (floatingIcon) {
                vBL = vTL - (vTL - vBL) * CollapsibleUtil.offset_XZPN;
                vBR = vTR - (vTR - vBR) * CollapsibleUtil.offset_XZNN;
            } else {
                vTL = vBL + (vTL - vBL) * CollapsibleUtil.offset_XZPN;
                vTR = vBR + (vTR - vBR) * CollapsibleUtil.offset_XZNN;
            }

            setupVertex(renderBlocks, xMax, yMin + CollapsibleUtil.offset_XZPN, zMin, uTL, vTL, TOP_LEFT);
            setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
            setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
            setupVertex(renderBlocks, xMin, yMin + CollapsibleUtil.offset_XZNN, zMin, uTR, vTR, TOP_RIGHT);

        } else {

            vBL = vTL - (vTL - vBL) * CollapsibleUtil.offset_XZPN;
            vBR = vTR - (vTR - vBR) * CollapsibleUtil.offset_XZNN;

            setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, TOP_LEFT);
            setupVertex(renderBlocks, xMax, yMax - CollapsibleUtil.offset_XZPN, zMin, uBL, vBL, BOTTOM_LEFT);
            setupVertex(renderBlocks, xMin, yMax - CollapsibleUtil.offset_XZNN, zMin, uBR, vBR, BOTTOM_RIGHT);
            setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, TOP_RIGHT);
        }
    }

    /**
     * Renders the given texture to the South face of the block.
     */
    public void collapsibleRenderFaceZPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon,
            boolean isPositive) {
        prepareRender(renderBlocks, ForgeDirection.SOUTH, x, y, z, icon);

        if (isPositive) {

            if (floatingIcon) {
                vBL = vTL - (vTL - vBL) * CollapsibleUtil.offset_XZNP;
                vBR = vTR - (vTR - vBR) * CollapsibleUtil.offset_XZPP;
            } else {
                vTL = vBL + (vTL - vBL) * CollapsibleUtil.offset_XZNP;
                vTR = vBR + (vTR - vBR) * CollapsibleUtil.offset_XZPP;
            }

            setupVertex(renderBlocks, xMin, yMin + CollapsibleUtil.offset_XZNP, zMax, uTL, vTL, TOP_LEFT);
            setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
            setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
            setupVertex(renderBlocks, xMax, yMin + CollapsibleUtil.offset_XZPP, zMax, uTR, vTR, TOP_RIGHT);

        } else {

            vBL = vTL - (vTL - vBL) * CollapsibleUtil.offset_XZNP;
            vBR = vTR - (vTR - vBR) * CollapsibleUtil.offset_XZPP;

            setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, TOP_LEFT);
            setupVertex(renderBlocks, xMin, yMax - CollapsibleUtil.offset_XZNP, zMax, uBL, vBL, BOTTOM_LEFT);
            setupVertex(renderBlocks, xMax, yMax - CollapsibleUtil.offset_XZPP, zMax, uBR, vBR, BOTTOM_RIGHT);
            setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, TOP_RIGHT);
        }
    }

    /**
     * Renders the given texture to the West face of the block.
     */
    public void collapsibleRenderFaceXNeg(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon,
            boolean isPositive) {
        prepareRender(renderBlocks, ForgeDirection.WEST, x, y, z, icon);

        if (isPositive) {

            if (floatingIcon) {
                vBL = vTL - (vTL - vBL) * CollapsibleUtil.offset_XZNN;
                vBR = vTR - (vTR - vBR) * CollapsibleUtil.offset_XZNP;
            } else {
                vTL = vBL + (vTL - vBL) * CollapsibleUtil.offset_XZNN;
                vTR = vBR + (vTR - vBR) * CollapsibleUtil.offset_XZNP;
            }

            setupVertex(renderBlocks, xMin, yMin + CollapsibleUtil.offset_XZNN, zMin, uTL, vTL, TOP_LEFT);
            setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, BOTTOM_LEFT);
            setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, BOTTOM_RIGHT);
            setupVertex(renderBlocks, xMin, yMin + CollapsibleUtil.offset_XZNP, zMax, uTR, vTR, TOP_RIGHT);

        } else {

            vBL = vTL - (vTL - vBL) * CollapsibleUtil.offset_XZNN;
            vBR = vTR - (vTR - vBR) * CollapsibleUtil.offset_XZNP;

            setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, TOP_LEFT);
            setupVertex(renderBlocks, xMin, yMax - CollapsibleUtil.offset_XZNN, zMin, uBL, vBL, BOTTOM_LEFT);
            setupVertex(renderBlocks, xMin, yMax - CollapsibleUtil.offset_XZNP, zMax, uBR, vBR, BOTTOM_RIGHT);
            setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, TOP_RIGHT);
        }
    }

    /**
     * Renders the given texture to the East face of the block.
     */
    public void collapsibleRenderFaceXPos(RenderBlocks renderBlocks, double x, double y, double z, IIcon icon,
            boolean isPositive) {
        prepareRender(renderBlocks, ForgeDirection.EAST, x, y, z, icon);

        if (isPositive) {

            if (floatingIcon) {
                vBL = vTL - (vTL - vBL) * CollapsibleUtil.offset_XZPP;
                vBR = vTR - (vTR - vBR) * CollapsibleUtil.offset_XZPN;
            } else {
                vTL = vBL + (vTL - vBL) * CollapsibleUtil.offset_XZPP;
                vTR = vBR + (vTR - vBR) * CollapsibleUtil.offset_XZPN;
            }

            setupVertex(renderBlocks, xMax, yMin + CollapsibleUtil.offset_XZPP, zMax, uTL, vTL, TOP_LEFT);
            setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, BOTTOM_LEFT);
            setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, BOTTOM_RIGHT);
            setupVertex(renderBlocks, xMax, yMin + CollapsibleUtil.offset_XZPN, zMin, uTR, vTR, TOP_RIGHT);

        } else {

            vBL = vTL - (vTL - vBL) * CollapsibleUtil.offset_XZPP;
            vBR = vTR - (vTR - vBR) * CollapsibleUtil.offset_XZPN;

            setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, TOP_LEFT);
            setupVertex(renderBlocks, xMax, yMax - CollapsibleUtil.offset_XZPP, zMax, uBL, vBL, BOTTOM_LEFT);
            setupVertex(renderBlocks, xMax, yMax - CollapsibleUtil.offset_XZPN, zMin, uBR, vBR, BOTTOM_RIGHT);
            setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, TOP_RIGHT);
        }
    }

    /**
     * Applies plant color to tessellator.
     */
    public void setPlantColor(BlockHandlerBase blockHandler, ItemStack itemStack, int x, int y, int z) {
        Block block = FlowerPotProperties.toBlock(itemStack);
        Tessellator tessellator = Tessellator.instance;

        float[] rgb = LightingHelper
                .getRGB(blockHandler.getBlockColor(block, itemStack.getItemDamage(), x, y, z, 1, null));
        blockHandler.lightingHelper.applyAnaglyph(rgb);

        tessellator.setColorOpaque_F(rgb[0], rgb[1], rgb[2]);

        if (blockHandler.TE.hasAttribute(blockHandler.TE.ATTR_FERTILIZER)) {
            if (FlowerPotProperties.getPlantColor(blockHandler.TE) != 16777215) {
                tessellator.setColorOpaque_F(0.45F, 0.80F, 0.30F);
            }
        }
    }

    /**
     * Renders a vanilla double tall plant.
     */
    public boolean renderBlockDoublePlant(TEBase TE, RenderBlocks renderBlocks, ItemStack itemStack, int x, int y,
            int z, boolean thin) {
        BlockDoublePlant block = (BlockDoublePlant) FlowerPotProperties.toBlock(itemStack);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z));

        boolean thinPlant = itemStack.getUnlocalizedName().equals("tile.doublePlant.grass");

        int metadata = itemStack.getItemDamage();

        /* Render bottom stem. */

        IIcon icon_bottom = block.func_149888_a(false, metadata);

        if (thinPlant) {
            renderPlantThinCrossedSquares(renderBlocks, block, icon_bottom, x, y, z, false);
        } else {
            renderPlantCrossedSquares(renderBlocks, block, icon_bottom, x, y, z, 0.75F, false);
        }

        tessellator.addTranslation(0.0F, 0.75F, 0.0F);

        /* Render top stem. */

        IIcon icon_top = block.func_149888_a(true, metadata);

        if (thinPlant) {
            renderPlantThinCrossedSquares(renderBlocks, block, icon_top, x, y, z, false);
        } else {
            renderPlantCrossedSquares(renderBlocks, block, icon_top, x, y, z, 0.75F, false);
        }

        /* Render sunflower top. */

        if (metadata == 0) {

            tessellator.addTranslation(0.0F, -0.15F, 0.0F);

            IIcon icon_sunflower_top_front = block.sunflowerIcons[0];
            double angle = FlowerPot.getAngle(TE) / 16.0D * 2 * Math.PI + Math.PI / 2;

            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double uMin = icon_sunflower_top_front.getMinU();
            double vMin = icon_sunflower_top_front.getMinV();
            double uMax = icon_sunflower_top_front.getMaxU();
            double vMax = icon_sunflower_top_front.getMaxV();
            double d11 = 0.5D + 0.25D * cos - 0.45D * sin;
            double d12 = 0.5D + 0.45D * cos + 0.25D * sin;
            double d13 = 0.5D + 0.25D * cos + 0.45D * sin;
            double d14 = 0.5D + -0.45D * cos + 0.25D * sin;
            double d15 = 0.5D + -0.05D * cos + 0.45D * sin;
            double d16 = 0.5D + -0.45D * cos + -0.05D * sin;
            double d17 = 0.5D + -0.05D * cos - 0.45D * sin;
            double d18 = 0.5D + 0.45D * cos + -0.05D * sin;
            tessellator.addVertexWithUV(x + d15, y + 1.0D, z + d16, uMin, vMax);
            tessellator.addVertexWithUV(x + d17, y + 1.0D, z + d18, uMax, vMax);
            tessellator.addVertexWithUV(x + d11, y + 0.0D, z + d12, uMax, vMin);
            tessellator.addVertexWithUV(x + d13, y + 0.0D, z + d14, uMin, vMin);
            IIcon icon_sunflower_top_back = block.sunflowerIcons[1];
            uMin = icon_sunflower_top_back.getMinU();
            vMin = icon_sunflower_top_back.getMinV();
            uMax = icon_sunflower_top_back.getMaxU();
            vMax = icon_sunflower_top_back.getMaxV();
            tessellator.addVertexWithUV(x + d17, y + 1.0D, z + d18, uMin, vMax);
            tessellator.addVertexWithUV(x + d15, y + 1.0D, z + d16, uMax, vMax);
            tessellator.addVertexWithUV(x + d13, y + 0.0D, z + d14, uMax, vMin);
            tessellator.addVertexWithUV(x + d11, y + 0.0D, z + d12, uMin, vMin);

            tessellator.addTranslation(0.0F, 0.15F, 0.0F);
        }

        tessellator.addTranslation(0.0F, -0.75F, 0.0F);

        return true;
    }

    /**
     * Renders plant using crossed squares.
     */
    public boolean renderPlantCrossedSquares(RenderBlocks renderBlocks, Block block, IIcon icon, int x, int y, int z,
            float scale, boolean flip_vertical) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z));

        double uMin = icon.getMinU();
        double vMin = icon.getMinV();
        double uMax = icon.getMaxU();
        double vMax = icon.getMaxV();
        double rotation = 0.45D * scale;
        double xMin = x + 0.5D - rotation;
        double xMax = x + 0.5D + rotation;
        double zMin = z + 0.5D - rotation;
        double zMax = z + 0.5D + rotation;

        if (flip_vertical) {
            double temp = vMin;
            vMin = vMax;
            vMax = temp;
        }

        tessellator.addVertexWithUV(xMin, y + (double) scale, zMin, uMin, vMin);
        tessellator.addVertexWithUV(xMin, y + 0.0D, zMin, uMin, vMax);
        tessellator.addVertexWithUV(xMax, y + 0.0D, zMax, uMax, vMax);
        tessellator.addVertexWithUV(xMax, y + (double) scale, zMax, uMax, vMin);
        tessellator.addVertexWithUV(xMax, y + (double) scale, zMax, uMin, vMin);
        tessellator.addVertexWithUV(xMax, y + 0.0D, zMax, uMin, vMax);
        tessellator.addVertexWithUV(xMin, y + 0.0D, zMin, uMax, vMax);
        tessellator.addVertexWithUV(xMin, y + (double) scale, zMin, uMax, vMin);
        tessellator.addVertexWithUV(xMin, y + (double) scale, zMax, uMin, vMin);
        tessellator.addVertexWithUV(xMin, y + 0.0D, zMax, uMin, vMax);
        tessellator.addVertexWithUV(xMax, y + 0.0D, zMin, uMax, vMax);
        tessellator.addVertexWithUV(xMax, y + (double) scale, zMin, uMax, vMin);
        tessellator.addVertexWithUV(xMax, y + (double) scale, zMin, uMin, vMin);
        tessellator.addVertexWithUV(xMax, y + 0.0D, zMin, uMin, vMax);
        tessellator.addVertexWithUV(xMin, y + 0.0D, zMax, uMax, vMax);
        tessellator.addVertexWithUV(xMin, y + (double) scale, zMax, uMax, vMin);

        return true;
    }

    /**
     * Renders thin plant using crossed squares.
     */
    public void renderPlantThinCrossedSquares(RenderBlocks renderBlocks, Block block, IIcon icon, int x, int y, int z,
            boolean flip_vertical) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z));

        double uMin = icon.getInterpolatedU(0.0D);
        double uMax = icon.getInterpolatedU(4.0D);
        double vMin = icon.getInterpolatedV(16.0D);
        double vMax = icon.getInterpolatedV(0.0D);
        double rotatedScaleFactor = 0.45D * 0.375F;
        double xMin = x + 0.5D - rotatedScaleFactor;
        double xMax = x + 0.5D + rotatedScaleFactor;
        double zMin = z + 0.5D - rotatedScaleFactor;
        double zMax = z + 0.5D + rotatedScaleFactor;

        if (flip_vertical) {
            double temp = vMin;
            vMin = vMax;
            vMax = temp;
        }

        tessellator.addVertexWithUV(xMin, y + 0.75D, zMin, uMin, vMax);
        tessellator.addVertexWithUV(xMin, y + 0.0D, zMin, uMin, vMin);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.0D, z + 0.5D, uMax, vMin);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.75D, z + 0.5D, uMax, vMax);

        tessellator.addVertexWithUV(xMax, y + 0.75D, zMin, uMin, vMax);
        tessellator.addVertexWithUV(xMax, y + 0.0D, zMin, uMin, vMin);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.0D, z + 0.5D, uMax, vMin);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.75D, z + 0.5D, uMax, vMax);

        tessellator.addVertexWithUV(xMax, y + 0.75D, zMax, uMin, vMax);
        tessellator.addVertexWithUV(xMax, y + 0.0D, zMax, uMin, vMin);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.0D, z + 0.5D, uMax, vMin);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.75D, z + 0.5D, uMax, vMax);

        tessellator.addVertexWithUV(xMin, y + 0.75D, zMax, uMin, vMax);
        tessellator.addVertexWithUV(xMin, y + 0.0D, zMax, uMin, vMin);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.0D, z + 0.5D, uMax, vMin);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.75D, z + 0.5D, uMax, vMax);

        uMin = icon.getInterpolatedU(12.0D);
        uMax = icon.getInterpolatedU(16.0D);

        tessellator.addVertexWithUV(x + 0.5D, y + 0.75D, z + 0.5D, uMin, vMax);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.0D, z + 0.5D, uMin, vMin);
        tessellator.addVertexWithUV(xMin, y + 0.0D, zMin, uMax, vMin);
        tessellator.addVertexWithUV(xMin, y + 0.75D, zMin, uMax, vMax);

        tessellator.addVertexWithUV(x + 0.5D, y + 0.75D, z + 0.5D, uMin, vMax);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.0D, z + 0.5D, uMin, vMin);
        tessellator.addVertexWithUV(xMax, y + 0.0D, zMin, uMax, vMin);
        tessellator.addVertexWithUV(xMax, y + 0.75D, zMin, uMax, vMax);

        tessellator.addVertexWithUV(x + 0.5D, y + 0.75D, z + 0.5D, uMin, vMax);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.0D, z + 0.5D, uMin, vMin);
        tessellator.addVertexWithUV(xMax, y + 0.0D, zMax, uMax, vMin);
        tessellator.addVertexWithUV(xMax, y + 0.75D, zMax, uMax, vMax);

        tessellator.addVertexWithUV(x + 0.5D, y + 0.75D, z + 0.5D, uMin, vMax);
        tessellator.addVertexWithUV(x + 0.5D, y + 0.0D, z + 0.5D, uMin, vMin);
        tessellator.addVertexWithUV(xMin, y + 0.0D, zMax, uMax, vMin);
        tessellator.addVertexWithUV(xMin, y + 0.75D, zMax, uMax, vMax);
    }

    /**
     * Renders vanilla cactus using "prickly" method.
     */
    public void drawPlantCactus(LightingHelper lightingHelper, RenderBlocks renderBlocks, ItemStack itemStack, int x,
            int y, int z) {
        Block block = BlockProperties.toBlock(itemStack);
        IIcon icon = block.getBlockTextureFromSide(2);

        double uMinL = icon.getInterpolatedU(0.0D);
        double uMaxL = icon.getInterpolatedU(3.0D);
        double uMinR = icon.getInterpolatedU(13.0D);
        double uMaxR = icon.getInterpolatedU(16.0D);
        double vMin = icon.getInterpolatedV(16.0D);
        double vMax = icon.getInterpolatedV(0.0D);

        renderBlocks.enableAO = true;
        renderBlocks.setRenderBounds(0.375D, 0.25D, 0.375D, 0.6875D, 1.0D, 0.6875D);

        /* NORTH FACE */

        lightingHelper.setupLightingZNeg(itemStack, x, y, z);
        lightingHelper.setupColor(x, y, z, 2, 16777215, icon);

        // LEFT
        setupVertex(renderBlocks, x + 0.6875F, y + 0.75F, z + 0.375F, uMinL, vMax, TOP_LEFT);
        setupVertex(renderBlocks, x + 0.6875F, y, z + 0.375F, uMinL, vMin, BOTTOM_LEFT);
        setupVertex(renderBlocks, x + 0.5F, y, z + 0.375F, uMaxL, vMin, BOTTOM_CENTER);
        setupVertex(renderBlocks, x + 0.5F, y + 0.75F, z + 0.375F, uMaxL, vMax, TOP_CENTER);

        // RIGHT
        setupVertex(renderBlocks, x + 0.5F, y + 0.75F, z + 0.375F, uMinR, vMax, TOP_CENTER);
        setupVertex(renderBlocks, x + 0.5F, y, z + 0.375F, uMinR, vMin, BOTTOM_CENTER);
        setupVertex(renderBlocks, x + 0.3125F, y, z + 0.375F, uMaxR, vMin, BOTTOM_RIGHT);
        setupVertex(renderBlocks, x + 0.3125F, y + 0.75F, z + 0.375F, uMaxR, vMax, TOP_RIGHT);

        /* SOUTH FACE */

        lightingHelper.setupLightingZPos(itemStack, x, y, z);
        lightingHelper.setupColor(x, y, z, 3, 16777215, icon);

        // LEFT
        setupVertex(renderBlocks, x + 0.3125F, y + 0.75F, z + 0.625F, uMinL, vMax, TOP_LEFT);
        setupVertex(renderBlocks, x + 0.3125F, y, z + 0.625F, uMinL, vMin, BOTTOM_LEFT);
        setupVertex(renderBlocks, x + 0.5F, y, z + 0.625F, uMaxL, vMin, BOTTOM_CENTER);
        setupVertex(renderBlocks, x + 0.5F, y + 0.75F, z + 0.625F, uMaxL, vMax, TOP_CENTER);

        // RIGHT
        setupVertex(renderBlocks, x + 0.5F, y + 0.75F, z + 0.625F, uMinR, vMax, TOP_CENTER);
        setupVertex(renderBlocks, x + 0.5F, y, z + 0.625F, uMinR, vMin, BOTTOM_CENTER);
        setupVertex(renderBlocks, x + 0.6875F, y, z + 0.625F, uMaxR, vMin, BOTTOM_RIGHT);
        setupVertex(renderBlocks, x + 0.6875F, y + 0.75F, z + 0.625F, uMaxR, vMax, TOP_RIGHT);

        /* WEST FACE */

        lightingHelper.setupLightingXNeg(itemStack, x, y, z);
        lightingHelper.setupColor(x, y, z, 4, 16777215, icon);

        // LEFT
        setupVertex(renderBlocks, x + 0.375F, y + 0.75F, z + 0.3125F, uMinL, vMax, TOP_LEFT);
        setupVertex(renderBlocks, x + 0.375F, y, z + 0.3125F, uMinL, vMin, BOTTOM_LEFT);
        setupVertex(renderBlocks, x + 0.375F, y, z + 0.5F, uMaxL, vMin, BOTTOM_CENTER);
        setupVertex(renderBlocks, x + 0.375F, y + 0.75F, z + 0.5F, uMaxL, vMax, TOP_CENTER);

        // RIGHT
        setupVertex(renderBlocks, x + 0.375F, y + 0.75F, z + 0.5F, uMinR, vMax, TOP_CENTER);
        setupVertex(renderBlocks, x + 0.375F, y, z + 0.5F, uMinR, vMin, BOTTOM_CENTER);
        setupVertex(renderBlocks, x + 0.375F, y, z + 0.6875F, uMaxR, vMin, BOTTOM_RIGHT);
        setupVertex(renderBlocks, x + 0.375F, y + 0.75F, z + 0.6875F, uMaxR, vMax, TOP_RIGHT);

        /* EAST FACE */

        lightingHelper.setupLightingXPos(itemStack, x, y, z);
        lightingHelper.setupColor(x, y, z, 5, 16777215, icon);

        // LEFT
        setupVertex(renderBlocks, x + 0.625F, y + 0.75F, z + 0.6875F, uMinL, vMax, TOP_LEFT);
        setupVertex(renderBlocks, x + 0.625F, y, z + 0.6875F, uMinL, vMin, BOTTOM_LEFT);
        setupVertex(renderBlocks, x + 0.625F, y, z + 0.5F, uMaxL, vMin, BOTTOM_CENTER);
        setupVertex(renderBlocks, x + 0.625F, y + 0.75F, z + 0.5F, uMaxL, vMax, TOP_CENTER);

        // RIGHT
        setupVertex(renderBlocks, x + 0.625F, y + 0.75F, z + 0.5F, uMinR, vMax, TOP_CENTER);
        setupVertex(renderBlocks, x + 0.625F, y, z + 0.5F, uMinR, vMin, BOTTOM_CENTER);
        setupVertex(renderBlocks, x + 0.625F, y, z + 0.3125F, uMaxR, vMin, BOTTOM_RIGHT);
        setupVertex(renderBlocks, x + 0.625F, y + 0.75F, z + 0.3125F, uMaxR, vMax, TOP_RIGHT);

        /* UP */

        lightingHelper.setupLightingYPos(itemStack, x, y, z);
        lightingHelper.setupColor(x, y, z, 1, 16777215, icon);

        icon = block.getBlockTextureFromSide(1);

        double uMin = icon.getInterpolatedU(6.0D);
        double uMax = icon.getInterpolatedU(10.0D);
        vMin = icon.getInterpolatedV(10.0D);
        vMax = icon.getInterpolatedV(6.0D);

        setupVertex(renderBlocks, x + 0.375F, y + 0.75F, z + 0.625F, uMin, vMin, TOP_LEFT);
        setupVertex(renderBlocks, x + 0.625F, y + 0.75F, z + 0.625F, uMin, vMax, BOTTOM_LEFT);
        setupVertex(renderBlocks, x + 0.625F, y + 0.75F, z + 0.375F, uMax, vMax, BOTTOM_RIGHT);
        setupVertex(renderBlocks, x + 0.375F, y + 0.75F, z + 0.375F, uMax, vMin, TOP_RIGHT);

        renderBlocks.enableAO = false;
    }
}
