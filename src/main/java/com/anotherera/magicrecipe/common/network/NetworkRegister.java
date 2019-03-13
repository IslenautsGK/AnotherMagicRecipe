package com.anotherera.magicrecipe.common.network;

import com.anotherera.magicrecipe.AnotherMagicRecipe;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;

public class NetworkRegister {

	private static int disc = 0;

	public static <REQ extends IMessage, REPLY extends IMessage> void regist(
			IMessageHandler<? super REQ, ? extends REPLY> rh, Class<REQ> message, Side side) {
		AnotherMagicRecipe.masterKeyNetwork.registerMessage(rh, message, disc++, side);
	}

}
