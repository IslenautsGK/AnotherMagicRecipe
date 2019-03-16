package com.anotherera.magicrecipe.common;

import com.anotherera.magicrecipe.AnotherMagicRecipe;
import com.anotherera.magicrecipe.client.creativetab.CreativeTabsLoader;
import com.anotherera.magicrecipe.common.block.BlockLoader;
import com.anotherera.magicrecipe.common.command.CommandSaveRecipe;
import com.anotherera.magicrecipe.common.entity.FakePlayerLoader;
import com.anotherera.magicrecipe.common.event.CommonEventLoader;
import com.anotherera.magicrecipe.common.inventory.GuiElementLoader;
import com.anotherera.magicrecipe.common.item.ItemLoader;
import com.anotherera.magicrecipe.common.recipe.CraftingLoader;
import com.anotherera.magicrecipe.common.recipehandler.RecipeHandlerLoader;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		AnotherMagicRecipe.masterKeyNetwork = NetworkRegistry.INSTANCE.newSimpleChannel(AnotherMagicRecipe.MODID);
		CreativeTabsLoader.init();
		ItemLoader.init();
		BlockLoader.init();
	}

	public void init(FMLInitializationEvent event) {
		CraftingLoader.init();
		CommonEventLoader.init();
		GuiElementLoader.init();
		FakePlayerLoader.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		RecipeHandlerLoader.postInit();
	}

	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSaveRecipe());
	}

	public void serverStarted(FMLServerStartedEvent event) {
		RecipeHandlerLoader.loadAll();
	}

	public void serverStopped(FMLServerStoppedEvent event) {
		RecipeHandlerLoader.saveAll();
	}

}
