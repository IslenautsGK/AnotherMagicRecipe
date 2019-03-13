package com.anotherera.magicrecipe.common.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.network.packet.RecipeChangePacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class RecipeChangeHandler implements IMessageHandler<RecipeChangePacket, IMessage> {

	private static Map<String, ARecipeHandler> map = new HashMap<>();

	public static void regist(ARecipeHandler arh) {
		map.put(arh.getClass().getName(), arh);
	}

	@Override
	public IMessage onMessage(RecipeChangePacket message, MessageContext ctx) {
		ARecipeHandler arh = map.get(message.arh);
		if (arh != null) {
			try {
				arh.receiveChange(message.data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
