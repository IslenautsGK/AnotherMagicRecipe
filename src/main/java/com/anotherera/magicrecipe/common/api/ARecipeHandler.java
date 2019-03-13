package com.anotherera.magicrecipe.common.api;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.anotherera.magicrecipe.AnotherMagicRecipe;
import com.anotherera.magicrecipe.common.network.packet.RecipeChangePacket;
import com.anotherera.magicrecipe.common.recipehandler.RecipeHandlerRegister;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public abstract class ARecipeHandler<REQ extends IMessage, REPLY extends IMessage>
		implements IRecipeHandler<REQ, REPLY> {

	@Override
	public REPLY onMessage(REQ message, MessageContext ctx) {
		REPLY packet = messageHandler(message, ctx);
		File dir = MinecraftServer.getServer() == null ? new File(Minecraft.getMinecraft().mcDataDir, "AnotherData")
				: MinecraftServer.getServer().getFile("AnotherData");
		try (DataOutputStream dos = new DataOutputStream(
				new FileOutputStream(new File(dir, RecipeHandlerRegister.getDataFileName(this))))) {
			this.save(dos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (DataInputStream dis = new DataInputStream(
				new FileInputStream(new File(dir, RecipeHandlerRegister.getDataFileName(this))))) {
			this.sendChange(dis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packet;
	}

	protected abstract REPLY messageHandler(REQ message, MessageContext ctx);

	@Override
	public void reload(DataInputStream dis) throws IOException {
		this.reset();
		this.load(dis);
	}

	@Override
	public void sendChange(DataInputStream dis) throws IOException {
		byte[] data = new byte[8192];
		int size = 0;
		byte[] buf = new byte[1024];
		int c;
		while ((c = dis.read(buf)) != -1) {
			if (size + c >= data.length) {
				data = Arrays.copyOf(data, data.length * 2);
			}
			for (int i = size; i < size + c; i++) {
				data[i] = buf[i - size];
			}
			size += c;
		}
		AnotherMagicRecipe.masterKeyNetwork.sendToAll(new RecipeChangePacket(Arrays.copyOf(data, size), this));
	}

	@Override
	public void receiveChange(byte[] data) throws IOException {
		reload(new DataInputStream(new ByteArrayInputStream(data)));
	}

}
