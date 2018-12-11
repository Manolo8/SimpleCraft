package com.github.manolo8.simplecraft.tools.item;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import net.minecraft.server.v1_13_R2.Item;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;

import java.io.DataOutput;
import java.io.IOException;

public class ItemStackData {

    public org.bukkit.inventory.ItemStack fromBytes(byte[] bytes) {
        return CraftItemStack.asBukkitCopy(fromBytes0(bytes));
    }

    public byte[] toBytes(org.bukkit.inventory.ItemStack item) {
        return toBytes0(CraftItemStack.asNMSCopy(item));
    }

    private ItemStack fromBytes0(byte[] bytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);

        ItemStack item;


        short short0 = buffer.readShort();
        if (short0 < 0) {
            item = ItemStack.a;
        } else {
            byte b0 = buffer.readByte();
            ItemStack itemstack = new ItemStack(Item.getById(short0), b0);
            itemstack.setTag(readNbt(buffer));
            if (itemstack.getTag() != null) {
                CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
            }

            item = itemstack;
        }

        buffer.release();

        return item;
    }

    private NBTTagCompound readNbt(ByteBuf buffer) {
        int i = buffer.readerIndex();
        byte b0 = buffer.readByte();
        if (b0 == 0) {
            return null;
        } else {
            buffer.readerIndex(i);

            try {
                return NBTCompressedStreamTools.a(new ByteBufInputStream(buffer), new NBTReadLimiter(2097152L));
            } catch (IOException var4) {
                throw new EncoderException(var4);
            }
        }
    }

    private byte[] toBytes0(ItemStack itemstack) {
        ByteBuf buffer = Unpooled.buffer();

        if (!itemstack.isEmpty() && itemstack.getItem() != null) {
            Item item = itemstack.getItem();
            buffer.writeShort(Item.getId(item));
            buffer.writeByte(itemstack.getCount());
            NBTTagCompound nbttagcompound = null;
            if (item.usesDurability() || item.n()) {
                itemstack = itemstack.cloneItemStack();
                CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
                nbttagcompound = itemstack.getTag();
            }

            this.writeNbt(buffer, nbttagcompound);
        } else {
            buffer.writeShort(-1);
        }

        byte[] bytes = new byte[buffer.writerIndex()];

        System.arraycopy(buffer.array(), 0, bytes, 0, buffer.writerIndex());
        buffer.release();

        return bytes;
    }

    private void writeNbt(ByteBuf buffer, NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            buffer.writeByte(0);
        } else {
            try {
                NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) new ByteBufOutputStream(buffer));
            } catch (Exception var3) {
                throw new EncoderException(var3);
            }
        }
    }

}
