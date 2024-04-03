package com.carpentersblocks.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.carpentersblocks.block.BlockCoverable;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.flowerpot.FlowerPotHandler;
import com.carpentersblocks.util.flowerpot.FlowerPotProperties;
import com.carpentersblocks.util.handler.DesignHandler;
import com.carpentersblocks.util.registry.IconRegistry;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRHFactory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockHandlerCarpentersFlowerPot extends BlockHandlerBase {

    private static final ThreadLocal<BlockHandlerCarpentersFlowerPot> threadRenderer = ThreadLocal
            .withInitial(BlockHandlerCarpentersFlowerPot::new);

    public ThreadSafeISBRHFactory newInstance() {
        return threadRenderer.get();
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    /**
     * Override to provide custom icons.
     */
    protected IIcon getUniqueIcon(ItemStack itemStack, int side, IIcon icon) {
        Block block = BlockProperties.toBlock(itemStack);

        if (block instanceof BlockCoverable) {
            return IconRegistry.icon_uncovered_solid;
        } else if (block.equals(Blocks.glass)) {
            return IconRegistry.icon_flower_pot_glass;
        } else {
            return icon;
        }
    }

    @Override
    /**
     * Renders block
     */
    protected void renderCarpentersBlock(int x, int y, int z) {
        renderBlocks.renderAllFaces = true;

        ItemStack itemStack = getCoverForRendering();

        if (TE.hasDesign()) {
            suppressOverlay = true;
            suppressChiselDesign = true;
            suppressDyeColor = true;
        }

        renderPot(itemStack, x, y, z);

        suppressOverlay = true;
        suppressChiselDesign = true;
        suppressDyeColor = true;

        if (TE.hasAttribute(TE.ATTR_SOIL)) {
            renderSoil(TE.getAttribute(TE.ATTR_SOIL), x, y, z);
        }

        if (TE.hasAttribute(TE.ATTR_PLANT)) {
            renderPlant(TE.getAttribute(TE.ATTR_PLANT), x, y, z);
        }

        suppressOverlay = false;
        suppressChiselDesign = false;
        suppressDyeColor = false;

        renderBlocks.renderAllFaces = false;
    }

    /**
     * Renders flower pot
     */
    public boolean renderPot(ItemStack itemStack, int x, int y, int z) {
        if (TE.hasDesign()) {
            IIcon designIcon = IconRegistry.icon_design_flower_pot
                    .get(DesignHandler.listFlowerPot.indexOf(TE.getDesign()));
            setIconOverride(6, designIcon);
        }

        /* BOTTOM BOX */

        renderBlocks.setRenderBounds(0.375D, 0.0D, 0.375D, 0.625D, 0.0625D, 0.625D);
        renderBlock(itemStack, x, y, z);

        /* NORTH BOX */

        renderBlocks.setRenderBounds(0.375D, 0.0D, 0.3125D, 0.625D, 0.375D, 0.375D);
        renderBlock(itemStack, x, y, z);

        /* SOUTH BOX */

        renderBlocks.setRenderBounds(0.375D, 0.0D, 0.625D, 0.625D, 0.375D, 0.6875D);
        renderBlock(itemStack, x, y, z);

        /* WEST BOX */

        renderBlocks.setRenderBounds(0.3125D, 0.0D, 0.3125D, 0.375D, 0.375D, 0.6875D);
        renderBlock(itemStack, x, y, z);

        /* EAST BOX */

        renderBlocks.setRenderBounds(0.625D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);
        renderBlock(itemStack, x, y, z);

        clearIconOverride(6);

        return true;
    }

    /**
     * Renders soil
     */
    public boolean renderSoil(ItemStack itemStack, int x, int y, int z) {
        renderBlocks.setRenderBounds(0.375D, 0.0625D, 0.375D, 0.625D, 0.25D, 0.625D);
        renderBlock(itemStack, x, y, z);

        return true;
    }

    /**
     * Renders plant
     */
    public boolean renderPlant(ItemStack itemStack, int x, int y, int z) {
        Block block = FlowerPotProperties.toBlock(itemStack);
        if (block.getRenderBlockPass() != renderPass) {
            return false;
        }

        /* Crop plants will use fully matured metadata. */

        if (block instanceof BlockCrops) {
            itemStack.setItemDamage(7);
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.addTranslation(0.0F, 0.25F, 0.0F);

        renderHelper.setPlantColor(this, itemStack, x, y, z);

        IIcon icon = block.getIcon(2, itemStack.getItemDamage());

        switch (FlowerPotHandler.getPlantProfile(itemStack)) {
            case DOUBLEPLANT:
                renderHelper.renderBlockDoublePlant(TE, renderBlocks, itemStack, x, y, z, false);
                break;
            case THIN_DOUBLEPLANT:
                renderHelper.renderBlockDoublePlant(TE, renderBlocks, itemStack, x, y, z, true);
                break;
            case REDUCED_SCALE_YP:
                renderHelper.renderPlantCrossedSquares(renderBlocks, block, icon, x, y, z, 0.75F, false);
                break;
            case REDUCED_SCALE_YN:
                renderHelper.renderPlantCrossedSquares(renderBlocks, block, icon, x, y, z, 0.75F, true);
                break;
            case TRUE_SCALE:
                renderHelper.renderPlantCrossedSquares(renderBlocks, block, icon, x, y, z, 1.0F, false);
                break;
            case THIN_YP:
                renderHelper.renderPlantThinCrossedSquares(renderBlocks, block, icon, x, y, z, false);
                break;
            case THIN_YN:
                renderHelper.renderPlantThinCrossedSquares(renderBlocks, block, icon, x, y, z, true);
                break;
            case CACTUS:
                renderHelper.drawPlantCactus(lightingHelper, renderBlocks, itemStack, x, y, z);
                break;
            case LEAVES:
                drawStackedBlocks(itemStack, x, y, z);
                break;
        }

        tessellator.addTranslation(0.0F, -0.25F, 0.0F);

        return true;
    }

    /**
     * Draws stacked blocks for leaves or mod cacti.
     */
    private void drawStackedBlocks(ItemStack itemStack, int x, int y, int z) {
        TE.setMetadata(itemStack.getItemDamage());
        renderBlocks.setRenderBounds(0.375F, 0.0D, 0.375F, 0.625F, 0.25D, 0.625F);
        renderBlock(itemStack, x, y, z);
        renderBlocks.setRenderBounds(0.375F, 0.25D, 0.375F, 0.625F, 0.50D, 0.625F);
        renderBlock(itemStack, x, y, z);
        renderBlocks.setRenderBounds(0.375F, 0.50D, 0.375F, 0.625F, 0.75D, 0.625F);
        renderBlock(itemStack, x, y, z);
        renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        TE.restoreMetadata();
    }
}
