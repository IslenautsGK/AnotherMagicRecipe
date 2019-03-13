package com.anotherera.magicrecipe.common.network.packet;

import com.anotherera.magicrecipe.common.api.ARecipeHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class RecipeChangePacket implements IMessage {

	public byte[] data;
	public String arh;

	public RecipeChangePacket() {
	}

	public RecipeChangePacket(byte[] data, ARecipeHandler arh) {
		this.data = data;
		this.arh = arh.getClass().getName();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		arh = ByteBufUtils.readUTF8String(buf);
		data = new byte[buf.readInt()];
		buf.readBytes(data);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, arh);
		buf.writeInt(data.length);
		buf.writeBytes(data);
	}

}
