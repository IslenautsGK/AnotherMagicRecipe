package com.anotherera.magicrecipe.common.item;

import java.util.List;

import com.anotherera.magicrecipe.client.creativetab.CreativeTabsLoader;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictItem extends Item {

	public OreDictItem() {
		this.setUnlocalizedName("ore_dict_item");
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabsLoader.anotherCreativeTab);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List subItem) {
		for (String oreName : OreDictionary.getOreNames()) {
			ItemStack stack = new ItemStack(item);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("oreName", oreName);
			stack.setTagCompound(nbt);
			subItem.add(stack);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return super.getItemStackDisplayName(stack);
		}
		NBTTagCompound nbt = stack.getTagCompound();
		if (!nbt.hasKey("oreName")) {
			return super.getItemStackDisplayName(stack);
		}
		return super.getItemStackDisplayName(stack) + ":" + nbt.getString("oreName");
	}

}
