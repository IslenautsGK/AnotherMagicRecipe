package com.anotherera.magicrecipe.common.inventory.slot;

import cpw.mods.fml.common.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thaumcraft.common.tiles.TileArcaneWorkbench;

@Optional.Interface(iface = "thaumcraft.common.container.SlotCraftingArcaneWorkbench", modid = "Thaumcraft")
public class SlotCraftingArcaneWorkbench extends thaumcraft.common.container.SlotCraftingArcaneWorkbench {

	public SlotCraftingArcaneWorkbench(EntityPlayer par1EntityPlayer, IInventory par2iInventory,
			IInventory par3iInventory, int par4, int par5, int par6) {
		super(par1EntityPlayer, par2iInventory, par3iInventory, par4, par5, par6);
	}

	@Override
	public boolean isItemValid(ItemStack p_75214_1_) {
		return true;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_) {
	}

	@Override
	public void putStack(ItemStack p_75215_1_) {
		((TileArcaneWorkbench) this.inventory).setInventorySlotContentsSoftly(this.getSlotIndex(), p_75215_1_);
		this.onSlotChanged();
	}

}
