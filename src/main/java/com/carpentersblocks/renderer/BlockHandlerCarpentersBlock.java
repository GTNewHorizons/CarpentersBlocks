package com.carpentersblocks.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRHFactory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockHandlerCarpentersBlock extends BlockHandlerBase {

    private static final ThreadLocal<BlockHandlerCarpentersBlock> threadRenderer = ThreadLocal
            .withInitial(BlockHandlerCarpentersBlock::new);

    public ThreadSafeISBRHFactory newInstance() {
        return threadRenderer.get();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderBlocks) {
        renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        super.renderInventoryBlock(block, metadata, modelID, renderBlocks);
    }
}
