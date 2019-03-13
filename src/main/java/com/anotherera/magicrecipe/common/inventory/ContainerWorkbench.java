package com.anotherera.magicrecipe.common.inventory;

import com.anotherera.magicrecipe.common.inventory.slot.SlotCrafting;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class ContainerWorkbench extends net.minecraft.inventory.ContainerWorkbench {

	public ContainerWorkbench(InventoryPlayer inv, World world, int x, int y, int z) {
		super(inv, world, x, y, z);
		this.inventorySlots.set(0, new SlotCrafting(inv.player, this.craftMatrix, this.craftResult, 0, 124, 35));
	}

}
