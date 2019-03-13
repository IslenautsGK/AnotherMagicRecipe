package com.anotherera.magicrecipe.common.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackUtil {

	public static void saveItemStack(ItemStack stack, DataOutputStream dos) throws IOException {
		dos.writeUTF(Item.itemRegistry.getNameForObject(stack.getItem()));
		dos.writeInt(stack.stackSize);
		dos.writeInt(stack.getItemDamage());
		dos.writeBoolean(stack.hasTagCompound());
		if (stack.hasTagCompound()) {
			CompressedStreamTools.write(stack.stackTagCompound, dos);
		}
	}

	public static ItemStack loadItemStack(DataInputStream dis) throws IOException {
		Item item = (Item) Item.itemRegistry.getObject(dis.readUTF());
		int count = dis.readInt();
		int damage = dis.readInt();
		ItemStack stack = new ItemStack(item, count, damage);
		if (dis.readBoolean()) {
			NBTTagCompound nbt = CompressedStreamTools.read(dis);
			stack.setTagCompound(nbt);
		}
		return stack;
	}

}
