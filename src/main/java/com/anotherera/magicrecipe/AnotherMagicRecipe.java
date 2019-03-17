package com.anotherera.magicrecipe;

import com.anotherera.magicrecipe.common.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = AnotherMagicRecipe.MODID, name = AnotherMagicRecipe.NAME, version = AnotherMagicRecipe.VERSION, acceptedMinecraftVersions = "1.7.10")
public class AnotherMagicRecipe {

	public static final String MODID = "anothermagicrecipe";
	public static final String NAME = "Another Magic Recipe";
	public static final String VERSION = "0.0.3";

	@Instance(AnotherMagicRecipe.MODID)
	public static AnotherMagicRecipe instance;

	@SidedProxy(clientSide = "com.anotherera.magicrecipe.client.ClientProxy", serverSide = "com.anotherera.magicrecipe.common.CommonProxy")
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper masterKeyNetwork;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		proxy.serverStarted(event);
	}

	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		proxy.serverStopped(event);
	}

}
