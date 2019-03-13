package com.anotherera.magicrecipe.common.recipehandler;

import java.util.HashMap;
import java.util.Map;

import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.event.MasterKeyEvent;
import com.anotherera.magicrecipe.common.inventory.GuiElementLoader;
import com.anotherera.magicrecipe.common.network.NetworkRegister;
import com.anotherera.magicrecipe.common.network.RecipeChangeHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;

public class RecipeHandlerRegister {

	private static Map<String, ARecipeHandler> nameToObj = new HashMap<>();
	private static Map<ARecipeHandler, String> objToName = new HashMap<>();

	public static <REQ extends IMessage, REPLY extends IMessage> void register(String name,
			ARecipeHandler<? super REQ, ? extends REPLY> rh, Class<REQ> message) {
		rh.init();
		nameToObj.put(name, rh);
		objToName.put(rh, name);
		NetworkRegister.regist(rh, message, Side.SERVER);
		RecipeChangeHandler.regist(rh);
		GuiElementLoader.regist(rh);
		MasterKeyEvent.regist(rh);
	}

	public static ARecipeHandler getHandler(String name) {
		return nameToObj.get(name);
	}

	public static String getName(ARecipeHandler rh) {
		return objToName.get(rh);
	}

	public static String getDataFileName(ARecipeHandler rh) {
		return objToName.get(rh) + ".ae";
	}

}
