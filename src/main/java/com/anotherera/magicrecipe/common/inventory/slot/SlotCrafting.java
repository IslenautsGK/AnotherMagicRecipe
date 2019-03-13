package com.anotherera.magicrecipe.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotCrafting extends net.minecraft.inventory.SlotCrafting {

	public SlotCrafting(EntityPlayer p_i1823_1_, IInventory p_i1823_2_, IInventory p_i1823_3_, int p_i1823_4_,
			int p_i1823_5_, int p_i1823_6_) {
		super(p_i1823_1_, p_i1823_2_, p_i1823_3_, p_i1823_4_, p_i1823_5_, p_i1823_6_);
	}

	@Override
	public boolean isItemValid(ItemStack p_75214_1_) {
		return true;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_) {
	}

}
