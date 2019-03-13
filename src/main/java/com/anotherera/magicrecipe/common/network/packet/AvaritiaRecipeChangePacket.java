package com.anotherera.magicrecipe.common.network.packet;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

@Optional.Interface(iface = "fox.spiteful.avaritia.gui.ContainerExtremeCrafting", modid = "Avaritia")
public class AvaritiaRecipeChangePacket implements IMessage {

	public boolean isAdd;
	public boolean isShaped;

	public AvaritiaRecipeChangePacket() {
	}

	public AvaritiaRecipeChangePacket(boolean isAdd, boolean isShaped) {
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
