package com.anotherera.magicrecipe.common.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public class MinecraftRecipeChangePacket implements IMessage {

	public boolean isAdd;
	public boolean isShaped;
	
	public MinecraftRecipeChangePacket() {
	}

	public MinecraftRecipeChangePacket(boolean isAdd, boolean isShaped) {
		super();
		this.isAdd = isAdd;
		this.isShaped = isShaped;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		isAdd = buf.readBoolean();
		isShaped = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isAdd);
		buf.writeBoolean(isShaped);
	}

}
