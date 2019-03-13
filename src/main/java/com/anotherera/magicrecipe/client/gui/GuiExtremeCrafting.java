package com.anotherera.magicrecipe.client.gui;

import com.anotherera.magicrecipe.AnotherMagicRecipe;
import com.anotherera.magicrecipe.common.inventory.ContainerExtremeCrafting;
import com.anotherera.magicrecipe.common.network.packet.AvaritiaRecipeChangePacket;

import cpw.mods.fml.common.Optional;
import fox.spiteful.avaritia.gui.GUIExtremeCrafting;
import fox.spiteful.avaritia.tile.TileEntityDireCrafting;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

@Optional.Interface(iface = "fox.spiteful.avaritia.gui.ContainerExtremeCrafting", modid = "Avaritia")
public class GuiExtremeCrafting extends GUIExtremeCrafting {

	private boolean isShaped;

	public GuiExtremeCrafting(InventoryPlayer inv, World world, int x, int y, int z, TileEntityDireCrafting table) {
		super(inv, world, x, y, z, table);
		this.inventorySlots = new ContainerExtremeCrafting(inv, world, x, y, z, table);
		isShaped = true;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(0, (this.width - this.xSize) / 2 + 206, (this.height - this.ySize) / 2 + 52,
				24, 20, "添加"));
		this.buttonList.add(new GuiButton(1, (this.width - this.xSize) / 2 + 206, (this.height - this.ySize) / 2 + 8,
				24, 20, "删除"));
		this.buttonList.add(new GuiButton(2, (this.width - this.xSize) / 2 + 206, (this.height - this.ySize) / 2 + 30,
				24, 20, "有序"));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			AnotherMagicRecipe.masterKeyNetwork.sendToServer(new AvaritiaRecipeChangePacket(true, isShaped));
			break;
		case 1:
			AnotherMagicRecipe.masterKeyNetwork.sendToServer(new AvaritiaRecipeChangePacket(false, isShaped));
			break;
		case 2:
			isShaped = !isShaped;
			((GuiButton) this.buttonList.get(2)).displayString = isShaped ? "有序" : "无序";
			break;
		}
	}

}
