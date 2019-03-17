package com.anotherera.magicrecipe.client.gui;

import com.anotherera.magicrecipe.AnotherMagicRecipe;
import com.anotherera.magicrecipe.common.inventory.ContainerWorkbench;
import com.anotherera.magicrecipe.common.network.packet.MinecraftRecipeChangePacket;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiCrafting extends net.minecraft.client.gui.inventory.GuiCrafting {

	private boolean isShaped;

	public GuiCrafting(InventoryPlayer inv, World world, int x, int y, int z) {
		super(inv, world, x, y, z);
		this.inventorySlots = new ContainerWorkbench(inv, world, x, y, z);
		isShaped = true;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(0, (this.width - this.xSize) / 2 + 147, (this.height - this.ySize) / 2 + 44,
				24, 20, I18n.format("gui.amr.add")));
		this.buttonList.add(new GuiButton(1, (this.width - this.xSize) / 2 + 89, (this.height - this.ySize) / 2 + 33,
				24, 20, I18n.format("gui.amr.del")));
		this.buttonList.add(new GuiButton(2, (this.width - this.xSize) / 2 + 147, (this.height - this.ySize) / 2 + 22,
				24, 20, I18n.format("gui.amr.shaped")));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			AnotherMagicRecipe.masterKeyNetwork.sendToServer(new MinecraftRecipeChangePacket(true, isShaped));
			break;
		case 1:
			AnotherMagicRecipe.masterKeyNetwork.sendToServer(new MinecraftRecipeChangePacket(false, isShaped));
			break;
		case 2:
			isShaped = !isShaped;
			((GuiButton) this.buttonList.get(2)).displayString = isShaped ? I18n.format("gui.amr.shaped")
					: I18n.format("gui.amr.shapless");
			break;
		}
	}

}
