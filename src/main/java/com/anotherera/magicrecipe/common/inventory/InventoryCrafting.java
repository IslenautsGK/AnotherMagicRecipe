package com.anotherera.magicrecipe.common.inventory;

import net.minecraft.item.ItemStack;

public class InventoryCrafting extends net.minecraft.inventory.InventoryCrafting {

	public ItemStack[] stackList;

	public InventoryCrafting(int w, int h) {
		super(null, w, h);
		stackList = new ItemStack[9];
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return p_70301_1_ >= this.getSizeInventory() ? null : this.stackList[p_70301_1_];
	}

}
