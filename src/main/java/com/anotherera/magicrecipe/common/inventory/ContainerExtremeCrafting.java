package com.anotherera.magicrecipe.common.inventory;

import com.anotherera.magicrecipe.common.inventory.slot.SlotCrafting;

import cpw.mods.fml.common.Optional;
import fox.spiteful.avaritia.tile.TileEntityDireCrafting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

@Optional.Interface(iface = "fox.spiteful.avaritia.gui.ContainerExtremeCrafting", modid = "Avaritia")
public class ContainerExtremeCrafting extends fox.spiteful.avaritia.gui.ContainerExtremeCrafting {

	public ContainerExtremeCrafting(InventoryPlayer inv, World world, int x, int y, int z,
			TileEntityDireCrafting tile) {
		super(inv, world, x, y, z, tile);
		this.inventorySlots.set(0, new SlotCrafting(inv.player, this.craftMatrix, this.craftResult, 0, 210, 80));
	}

}
