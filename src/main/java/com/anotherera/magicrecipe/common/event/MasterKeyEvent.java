package com.anotherera.magicrecipe.common.event;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.anotherera.magicrecipe.AnotherMagicRecipe;
import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.inventory.GuiElementLoader;
import com.anotherera.magicrecipe.common.item.ItemLoader;
import com.anotherera.magicrecipe.common.recipehandler.RecipeHandlerRegister;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.oredict.OreDictionary;

public class MasterKeyEvent {

	private static List<ARecipeHandler> elements = new ArrayList<>();

	public static void regist(ARecipeHandler rh) {
		elements.add(rh);
	}

	@SubscribeEvent
	public void onMasterKeyRightClick(PlayerInteractEvent event) {
		if (event.world.isRemote || event.entityPlayer.getCurrentEquippedItem() == null
				|| event.entityPlayer.getCurrentEquippedItem().getItem() != ItemLoader.masterKey
				|| event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Block block = event.world.getBlock(event.x, event.y, event.z);
		int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
		for (ARecipeHandler element : elements) {
			if (block.getClass().getName().equals(element.getBlockClassName())
					&& GuiElementLoader.getId(element) != -1) {
				event.entityPlayer.openGui(AnotherMagicRecipe.instance, GuiElementLoader.getId(element), event.world,
						event.x, event.y, event.z);
				event.setCanceled(true);
				break;
			}
		}
	}

	@SubscribeEvent
	public void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
		File dir = MinecraftServer.getServer().getFile("AnotherData");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		for (ARecipeHandler element : elements) {
			File dataFile = new File(dir, RecipeHandlerRegister.getDataFileName(element));
			if (dataFile.exists()) {
				try (DataInputStream dis = new DataInputStream(new FileInputStream(dataFile))) {
					element.sendChange(dis);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemTooltipRender(ItemTooltipEvent event) {
		if (event.itemStack.getItem() == ItemLoader.oredictitem && event.itemStack.hasTagCompound()) {
			NBTTagCompound nbt = event.itemStack.getTagCompound();
			if (nbt.hasKey("oreName")) {
				List<ItemStack> items = OreDictionary.getOres(nbt.getString("oreName"), false);
				for (ItemStack item : items) {
					event.toolTip.add(1, " -" + item.getDisplayName());
				}
				event.toolTip.add(1, "example:");
			}
		}
	}

}
