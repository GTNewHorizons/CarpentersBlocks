package com.carpentersblocks.network;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.carpentersblocks.block.BlockCoverable;
import com.carpentersblocks.util.EntityLivingUtil;
import com.carpentersblocks.util.ModLogger;
import com.carpentersblocks.util.handler.EventHandler;

import cpw.mods.fml.common.eventhandler.Event;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

public class PacketActivateBlock extends TilePacket {

    private int side;

    public PacketActivateBlock() {}

    public PacketActivateBlock(int x, int y, int z, int side) {
        super(x, y, z);
        this.side = side;
    }

    @Override
    public void processData(EntityPlayer entityPlayer, ByteBufInputStream bbis) throws IOException {
        super.processData(entityPlayer, bbis);

        PlayerInteractEvent pie = new PlayerInteractEvent(
                entityPlayer,
                PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,
                x,
                y,
                z,
                side,
                entityPlayer.worldObj);
        EventHandler.IGNORE_INTERACT_EVENT = pie;
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(pie);
        EventHandler.IGNORE_INTERACT_EVENT = null;
        if (cancelled || (pie.useBlock == Event.Result.DENY)) {
            ModLogger.logger.warn(
                    ModLogger.securityMarker,
                    "Player {} denied block interaction at {}, {}, {}",
                    entityPlayer.getGameProfile(),
                    x,
                    y,
                    z);
            return;
        }

        ItemStack itemStack = entityPlayer.getHeldItem();
        side = bbis.readInt();

        final Block block = entityPlayer.worldObj.getBlock(x, y, z);

        if (!(block instanceof BlockCoverable)) {
            ModLogger.logger.warn(
                    ModLogger.securityMarker,
                    "Player {} tried to use PacketActivateBlock on a non-carpenters block at {}, {}, {}",
                    entityPlayer.getGameProfile(),
                    x,
                    y,
                    z);
        }

        boolean result = block.onBlockActivated(entityPlayer.worldObj, x, y, z, entityPlayer, side, 1.0F, 1.0F, 1.0F);

        if (!result) {
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                itemStack.tryPlaceItemIntoWorld(entityPlayer, entityPlayer.worldObj, x, y, z, side, 1.0F, 1.0F, 1.0F);
                EntityLivingUtil.decrementCurrentSlot(entityPlayer);
            }
        }
    }

    @Override
    public void appendData(ByteBuf buffer) throws IOException {
        super.appendData(buffer);
        buffer.writeInt(side);
    }
}
