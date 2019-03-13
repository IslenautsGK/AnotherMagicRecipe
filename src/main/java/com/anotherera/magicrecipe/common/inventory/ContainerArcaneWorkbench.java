package com.anotherera.magicrecipe.common.inventory;

import com.anotherera.magicrecipe.common.inventory.slot.SlotCraftingArcaneWorkbench;

import cpw.mods.fml.common.Optional;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.common.tiles.TileArcaneWorkbench;

@Optional.Interface(iface = "thaumcraft.common.container.ContainerArcaneWorkbench", modid = "Thaumcraft")
public class ContainerArcaneWorkbench extends thaumcraft.common.container.ContainerArcaneWorkbench {

	public TileArcaneWorkbench tile;
	public InventoryPlayer ip;

	public ContainerArcaneWorkbench(InventoryPlayer inv, TileArcaneWorkbench tile) {
		super(inv, tile);
		this.inventorySlots.set(0, new SlotCraftingArcaneWorkbench(inv.player, tile, tile, 9, 160, 64));
		this.tile = tile;
		this.ip = inv;
	}

}
