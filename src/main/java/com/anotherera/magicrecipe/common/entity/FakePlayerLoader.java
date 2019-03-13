package com.anotherera.magicrecipe.common.entity;

import java.lang.ref.WeakReference;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class FakePlayerLoader {

	private static GameProfile gameProfile;
	private static WeakReference<EntityPlayerMP> fakePlayer;

	public static void init() {
		gameProfile = new GameProfile(UUID.fromString("66b45460-21e0-4b38-947b-105bb72f0091"), "[AnotherMagicRecipe]");
		fakePlayer = new WeakReference<EntityPlayerMP>(null);
	}

	public static WeakReference<EntityPlayerMP> getFakePlayer(WorldServer server) {
		if (fakePlayer.get() == null) {
			fakePlayer = new WeakReference<EntityPlayerMP>(FakePlayerFactory.get(server, gameProfile));
		} else {
			fakePlayer.get().worldObj = server;
		}
		return fakePlayer;
	}

}
