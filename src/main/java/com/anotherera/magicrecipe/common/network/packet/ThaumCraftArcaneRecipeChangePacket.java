package com.anotherera.magicrecipe.common.network.packet;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

@Optional.Interface(iface = "thaumcraft.common.container.ContainerArcaneWorkbench", modid = "Thaumcraft")
public class ThaumCraftArcaneRecipeChangePacket implements IMessage {

	public boolean isAdd;
	public boolean isShaped;
	public int 风;
	public int 火;
	public int 水;
	public int 地;
	public int 秩序;
	public int 混沌;
	public String research;

	public ThaumCraftArcaneRecipeChangePacket() {
	}

	public ThaumCraftArcaneRecipeChangePacket(boolean isAdd, boolean isShaped, int 风, int 火, int 水, int 地, int 秩序,
			int 混沌, String research) {
		super();
		this.isAdd = isAdd;
		this.isShaped = isShaped;
		this.风 = 风;
		this.火 = 火;
		this.水 = 水;
		this.地 = 地;
		this.秩序 = 秩序;
		this.混沌 = 混沌;
		this.research = research;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		isAdd = buf.readBoolean();
		isShaped = buf.readBoolean();
		风 = buf.readInt();
		火 = buf.readInt();
		水 = buf.readInt();
		地 = buf.readInt();
		秩序 = buf.readInt();
		混沌 = buf.readInt();
		research = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isAdd);
		buf.writeBoolean(isShaped);
		buf.writeInt(风);
		buf.writeInt(火);
		buf.writeInt(水);
		buf.writeInt(地);
		buf.writeInt(秩序);
		buf.writeInt(混沌);
		ByteBufUtils.writeUTF8String(buf, research);
	}

}
