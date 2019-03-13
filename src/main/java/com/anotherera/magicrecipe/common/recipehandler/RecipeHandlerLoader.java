package com.anotherera.magicrecipe.common.recipehandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.network.AvaritiaRecipeChangeHandler;
import com.anotherera.magicrecipe.common.network.MinecraftRecipeChangeHandler;
import com.anotherera.magicrecipe.common.network.NetworkRegister;
import com.anotherera.magicrecipe.common.network.RecipeChangeHandler;
import com.anotherera.magicrecipe.common.network.ThaumCraftArcaneRecipeChangeHandler;
import com.anotherera.magicrecipe.common.network.packet.AvaritiaRecipeChangePacket;
import com.anotherera.magicrecipe.common.network.packet.MinecraftRecipeChangePacket;
import com.anotherera.magicrecipe.common.network.packet.RecipeChangePacket;
import com.anotherera.magicrecipe.common.network.packet.ThaumCraftArcaneRecipeChangePacket;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class RecipeHandlerLoader {

	private static List<ARecipeHandler> rhs = new ArrayList<>();

	public static void preInit() {
		NetworkRegister.regist(new RecipeChangeHandler(), RecipeChangePacket.class, Side.CLIENT);
		register("MinecraftRecipe", new MinecraftRecipeChangeHandler(), MinecraftRecipeChangePacket.class);
		if (Loader.isModLoaded("Thaumcraft")) {
			register("ThaumCraftArcaneRecipe", new ThaumCraftArcaneRecipeChangeHandler(),
					ThaumCraftArcaneRecipeChangePacket.class);
		}
		if (Loader.isModLoaded("Avaritia")) {
			register("AvaritiaRecipe", new AvaritiaRecipeChangeHandler(), AvaritiaRecipeChangePacket.class);
		}
	}

	public static void loadAll() {
		File dir = MinecraftServer.getServer() == null ? new File(Minecraft.getMinecraft().mcDataDir, "AnotherData")
				: MinecraftServer.getServer().getFile("AnotherData");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		for (ARecipeHandler iRecipeHandler : rhs) {
			File dataFile = new File(dir, RecipeHandlerRegister.getDataFileName(iRecipeHandler));
			if (dataFile.exists()) {
				try (DataInputStream dis = new DataInputStream(new FileInputStream(dataFile))) {
					iRecipeHandler.load(dis);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void saveAll() {
		File dir = MinecraftServer.getServer() == null ? new File(Minecraft.getMinecraft().mcDataDir, "AnotherData")
				: MinecraftServer.getServer().getFile("AnotherData");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		for (ARecipeHandler iRecipeHandler : rhs) {
			try (DataOutputStream dos = new DataOutputStream(
					new FileOutputStream(new File(dir, RecipeHandlerRegister.getDataFileName(iRecipeHandler))))) {
				iRecipeHandler.save(dos);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void register(String name,
			ARecipeHandler<? super REQ, ? extends REPLY> rh, Class<REQ> message) {
		RecipeHandlerRegister.register(name, rh, message);
		rhs.add(rh);
	}

}
