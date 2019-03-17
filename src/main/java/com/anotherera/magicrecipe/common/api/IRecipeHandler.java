package com.anotherera.magicrecipe.common.api;

import java.io.DataInputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IRecipeHandler<REQ extends IMessage, REPLY extends IMessage>
		extends IRecipeDataHandler, IMessageHandler<REQ, REPLY> {

	void init();

	void reset();

	void reload(DataInputStream dis) throws IOException;

	void sendChange(DataInputStream dis) throws IOException;

	@SideOnly(Side.CLIENT)
	void receiveChange(byte[] data) throws IOException;

	boolean undo();

	boolean redo();

	String getBlockClassName();

	Object getGuiElement(EntityPlayer player, World world, int x, int y, int z);

	Object getContainerElement(EntityPlayer player, World world, int x, int y, int z);

}
