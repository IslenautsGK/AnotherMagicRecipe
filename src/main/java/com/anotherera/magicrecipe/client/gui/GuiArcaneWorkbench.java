package com.anotherera.magicrecipe.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.anotherera.magicrecipe.AnotherMagicRecipe;
import com.anotherera.magicrecipe.common.inventory.ContainerArcaneWorkbench;
import com.anotherera.magicrecipe.common.network.packet.ThaumCraftArcaneRecipeChangePacket;

import cpw.mods.fml.common.Optional;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.tiles.TileArcaneWorkbench;

@Optional.Interface(iface = "thaumcraft.client.gui.GuiArcaneWorkbench", modid = "Thaumcraft")
public class GuiArcaneWorkbench extends thaumcraft.client.gui.GuiArcaneWorkbench {

	private boolean isShaped;
	private List<GuiTextField> texts;
	private String selectResearch;
	private static final int[][] aspectLocs = { { 72, 21 }, { 24, 43 }, { 24, 102 }, { 72, 124 }, { 120, 102 },
			{ 120, 43 } };
	private static final Aspect[] aspects = { Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER,
			Aspect.ENTROPY };

	public GuiArcaneWorkbench(InventoryPlayer inv, TileArcaneWorkbench tile) {
		super(inv, tile);
		this.inventorySlots = new ContainerArcaneWorkbench(inv, tile);
		isShaped = true;
		texts = new ArrayList<>();
		selectResearch = "ASPECTS";
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(0, (this.width - this.xSize) / 2 + 186, (this.height - this.ySize) / 2 + 41,
				24, 20, "添加"));
		this.buttonList.add(new GuiButton(1, (this.width - this.xSize) / 2 + 186, (this.height - this.ySize) / 2 + 62,
				24, 20, "删除"));
		this.buttonList.add(new GuiButton(2, (this.width - this.xSize) / 2 + 186, (this.height - this.ySize) / 2 + 83,
				24, 20, "有序"));
		this.buttonList.add(new GuiButton(3, (this.width - this.xSize) / 2 + 150, (this.height - this.ySize) / 2 + 118,
				60, 20, "选择研究"));
		GuiTextField[] gtfa = new GuiTextField[6];
		for (int i = 0; i < 6; i++) {
			gtfa[i] = new GuiTextField(this.fontRendererObj, (this.width - this.xSize) / 2 + aspectLocs[i][0] - 20,
					(this.height - this.ySize) / 2 + aspectLocs[i][1] - 10, 40, 20);
			gtfa[i].setMaxStringLength(8);
			gtfa[i].setVisible(false);
			if (!texts.isEmpty()) {
				gtfa[i].setText(texts.get(i).getText());
			}
		}
		texts = Arrays.asList(gtfa);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		for (GuiTextField guiTextField : texts) {
			guiTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
		for (GuiTextField guiTextField : texts) {
			guiTextField.drawTextBox();
		}
	}

	@Override
	protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
		super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
		for (GuiTextField guiTextField : texts) {
			guiTextField.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
			guiTextField.setVisible(guiTextField.isFocused());
		}
	}

	@Override
	protected void keyTyped(char p_73869_1_, int p_73869_2_) {
		super.keyTyped(p_73869_1_, p_73869_2_);
		for (GuiTextField guiTextField : texts) {
			guiTextField.textboxKeyTyped(p_73869_1_, p_73869_2_);
			guiTextField.setText(guiTextField.getText().replaceAll("[^0-9]", ""));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int[] armors = new int[6];
		for (int i = 0; i < armors.length; i++) {
			String intStr = texts.get(i).getText();
			armors[i] = intStr.isEmpty() ? 0 : Integer.parseInt(texts.get(i).getText());
		}
		switch (button.id) {
		case 0:
			AnotherMagicRecipe.masterKeyNetwork.sendToServer(new ThaumCraftArcaneRecipeChangePacket(true, isShaped,
					armors[0], armors[1], armors[2], armors[3], armors[4], armors[5], selectResearch));
			break;
		case 1:
			AnotherMagicRecipe.masterKeyNetwork.sendToServer(new ThaumCraftArcaneRecipeChangePacket(false, isShaped,
					armors[0], armors[1], armors[2], armors[3], armors[4], armors[5], selectResearch));
			break;
		case 2:
			isShaped = !isShaped;
			((GuiButton) this.buttonList.get(2)).displayString = isShaped ? "有序" : "无序";
			break;
		case 3:
			this.mc.displayGuiScreen(new GuiSelectResearch(this));
			break;
		}
	}

	public void setSelectResearch(String selectResearch) {
		this.selectResearch = selectResearch;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float arg0, int arg1, int arg2) {
		UtilsFX.bindTexture("textures/gui/gui_arcaneworkbench.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		int var5 = (this.width - this.xSize) / 2;
		int var6 = (this.height - this.ySize) / 2;
		drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
		GL11.glDisable(GL11.GL_BLEND);
		for (int i = 0; i < 6; i++) {
			UtilsFX.drawTag((this.width - this.xSize) / 2 + this.aspectLocs[i][0] - 8,
					(this.height - this.ySize) / 2 + this.aspectLocs[i][1] - 8, aspects[i], 0, 0, this.zLevel, 771, 1,
					false);
		}
	}

}
