package com.anotherera.magicrecipe.client.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictItemRender implements IItemRenderer {

	private final RenderItem ri = new RenderItem();
	private boolean recursive = false;
	private long preTime = 0;
	private int index = 0;

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if (!this.recursive && type == IItemRenderer.ItemRenderType.INVENTORY) {
			if (item.hasTagCompound()) {
				NBTTagCompound nbt = item.getTagCompound();
				if (nbt.hasKey("oreName")) {
					List<ItemStack> items = OreDictionary.getOres(nbt.getString("oreName"), false);
					if (!items.isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		this.recursive = true;
		long curTime = System.currentTimeMillis();
		if (curTime - preTime > 2000) {
			preTime = curTime;
			if (index == Integer.MAX_VALUE) {
				index = 0;
			}
			index++;
		}
		NBTTagCompound nbt = item.getTagCompound();
		List<ItemStack> items = OreDictionary.getOres(nbt.getString("oreName"), false);
		ItemStack ore = items.get(index % items.size());
		if (ore.getItemDamage() >= 32767 || ore.getItemDamage() < 0) {
			ore = ore.copy();
			ore.setItemDamage(0);
		}
		Minecraft mc = Minecraft.getMinecraft();
		GL11.glPushAttrib(24576);
		RenderHelper.enableGUIStandardItemLighting();
		this.ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), ore, 0, 0);
		RenderHelper.disableStandardItemLighting();
		GL11.glPopAttrib();
		this.recursive = false;
	}

}
