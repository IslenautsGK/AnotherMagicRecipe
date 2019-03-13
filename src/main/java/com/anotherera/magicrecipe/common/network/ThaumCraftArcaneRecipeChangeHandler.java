package com.anotherera.magicrecipe.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.anotherera.magicrecipe.client.gui.GuiArcaneWorkbench;
import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.entity.FakePlayerLoader;
import com.anotherera.magicrecipe.common.inventory.ContainerArcaneWorkbench;
import com.anotherera.magicrecipe.common.network.packet.ThaumCraftArcaneRecipeChangePacket;
import com.anotherera.magicrecipe.common.util.ItemStackUtil;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.tiles.TileArcaneWorkbench;

@Optional.Interface(iface = "thaumcraft.common.container.ContainerArcaneWorkbench", modid = "Thaumcraft")
public class ThaumCraftArcaneRecipeChangeHandler extends ARecipeHandler<ThaumCraftArcaneRecipeChangePacket, IMessage> {

	public static List<Object[]> recipes = new ArrayList<>();
	private static List raw = new ArrayList();

	private static IInternalMethodHandler imh = new InternalMethodHandler() {

		@Override
		public boolean isResearchComplete(String username, String researchkey) {
			return true;
		}

	};

	@Override
	public void init() {
		raw.clear();
		raw.addAll(ThaumcraftApi.getCraftingRecipes());
	}

	@Override
	public void reset() {
		ThaumcraftApi.getCraftingRecipes().clear();
		ThaumcraftApi.getCraftingRecipes().addAll(raw);
	}

	@Override
	public void save(DataOutputStream dos) throws IOException {
		for (Object[] objs : recipes) {
			if (objs[0] instanceof ItemStack) {
				dos.writeBoolean(false);
				String tag = "";
				for (Object itemStack : objs) {
					tag = itemStack == null ? tag + "0" : tag + "1";
				}
				dos.writeUTF(tag);
				for (Object itemStack : objs) {
					if (itemStack != null) {
						ItemStackUtil.saveItemStack((ItemStack) itemStack, dos);
					}
				}
			} else {
				dos.writeBoolean(true);
				dos.writeBoolean((Boolean) objs[0]);
				ItemStackUtil.saveItemStack((ItemStack) objs[1], dos);
				for (int i = 0; i < 6; i++) {
					dos.writeInt((Integer) objs[2 + i]);
				}
				dos.writeUTF((String) objs[8]);
				String tag = "";
				for (int i = 9; i < objs.length; i++) {
					tag = objs[i] == null ? tag + "0" : tag + "1";
				}
				dos.writeUTF(tag);
				for (int i = 9; i < objs.length; i++) {
					if (objs[i] != null) {
						ItemStackUtil.saveItemStack((ItemStack) objs[i], dos);
					}
				}
			}
		}
	}

	@Override
	public void load(DataInputStream dis) throws IOException {
		IInternalMethodHandler temp = ThaumcraftApi.internalMethods;
		ThaumcraftApi.internalMethods = imh;
		try {
			while (true) {
				if (dis.readBoolean()) {
					boolean isShaped = dis.readBoolean();
					ItemStack output = ItemStackUtil.loadItemStack(dis);
					AspectList aspects = new AspectList();
					int armor = dis.readInt();
					if (armor != 0) {
						aspects.add(Aspect.AIR, armor);
					}
					armor = dis.readInt();
					if (armor != 0) {
						aspects.add(Aspect.FIRE, armor);
					}
					armor = dis.readInt();
					if (armor != 0) {
						aspects.add(Aspect.WATER, armor);
					}
					armor = dis.readInt();
					if (armor != 0) {
						aspects.add(Aspect.EARTH, armor);
					}
					armor = dis.readInt();
					if (armor != 0) {
						aspects.add(Aspect.ORDER, armor);
					}
					armor = dis.readInt();
					if (armor != 0) {
						aspects.add(Aspect.ENTROPY, armor);
					}
					String research = dis.readUTF();
					String tag = dis.readUTF();
					ItemStack[] inputs = new ItemStack[tag.length()];
					for (int i = 0; i < tag.length(); i++) {
						if (tag.charAt(i) == '1') {
							inputs[i] = ItemStackUtil.loadItemStack(dis);
						}
					}
					addRecipe(isShaped, output, inputs, aspects, research);
				} else {
					String tag = dis.readUTF();
					ItemStack[] inputs = new ItemStack[tag.length()];
					for (int i = 0; i < tag.length(); i++) {
						if (tag.charAt(i) == '1') {
							inputs[i] = ItemStackUtil.loadItemStack(dis);
						}
					}
					TileArcaneWorkbench taw = new TileArcaneWorkbench();
					for (int i = 0; i < inputs.length; i++) {
						taw.stackList[i] = inputs[i];
					}
					if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
						delRecipe(FakePlayerLoader.getFakePlayer(MinecraftServer.getServer().worldServerForDimension(0))
								.get(), taw);
					} else {
						delRecipe(getPlayer(), taw);
					}
				}
			}
		} catch (EOFException e) {
		}
		ThaumcraftApi.internalMethods = temp;
	}

	@SideOnly(Side.CLIENT)
	private EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	protected IMessage messageHandler(ThaumCraftArcaneRecipeChangePacket message, MessageContext ctx) {
		IInternalMethodHandler temp = ThaumcraftApi.internalMethods;
		ThaumcraftApi.internalMethods = imh;
		Container container = ctx.getServerHandler().playerEntity.openContainer;
		if (container instanceof ContainerArcaneWorkbench) {
			ContainerArcaneWorkbench awb = (ContainerArcaneWorkbench) container;
			if (message.isAdd) {
				ItemStack[] inputs = new ItemStack[9];
				for (int i = 0; i < 9; i++) {
					inputs[i] = awb.tile.getStackInSlot(i);
				}
				AspectList aspects = new AspectList();
				if (message.风 != 0) {
					aspects.add(Aspect.AIR, message.风);
				}
				if (message.火 != 0) {
					aspects.add(Aspect.FIRE, message.火);
				}
				if (message.水 != 0) {
					aspects.add(Aspect.WATER, message.水);
				}
				if (message.地 != 0) {
					aspects.add(Aspect.EARTH, message.地);
				}
				if (message.秩序 != 0) {
					aspects.add(Aspect.ORDER, message.秩序);
				}
				if (message.混沌 != 0) {
					aspects.add(Aspect.ENTROPY, message.混沌);
				}
				addRecipe(message.isShaped, awb.tile.getStackInSlot(9), inputs, aspects, message.research);
			} else {
				delRecipe(ctx.getServerHandler().playerEntity, awb.tile);
			}
			awb.onCraftMatrixChanged(null);
		}
		ThaumcraftApi.internalMethods = temp;
		return null;
	}

	private void addRecipe(boolean isShaped, ItemStack output, ItemStack[] inputs, AspectList aspects,
			String research) {
		if (output != null) {
			output = output.copy();
			boolean haveItem = false;
			for (int i = 0; i < 9; i++) {
				if (inputs[i] != null) {
					inputs[i] = inputs[i].copy();
					inputs[i].stackSize = 1;
					haveItem = true;
				}
			}
			if (haveItem && aspects.size() != 0) {
				IArcaneRecipe exRecipe;
				if (isShaped) {
					int up = 3, down = -1, left = 3, right = -1, count = 0;
					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							if (inputs[i * 3 + j] != null) {
								count++;
								if (up > i) {
									up = i;
								}
								if (down < i) {
									down = i;
								}
								if (left > j) {
									left = j;
								}
								if (right < j) {
									right = j;
								}
							}
						}
					}
					int w = right - left + 1;
					int h = down - up + 1;
					String[] strtags = new String[h];
					for (int i = 0; i < h; i++) {
						strtags[i] = "";
						for (int j = 0; j < w; j++) {
							strtags[i] += i * w + j;
						}
					}
					ItemStack[] in = new ItemStack[w * h];
					for (int i = 0; i < h; i++) {
						for (int j = 0; j < w; j++) {
							in[i * w + j] = inputs[(i + up) * 3 + (j + left)];
						}
					}
					Object[] objs = new Object[strtags.length + count * 2];
					for (int i = 0; i < strtags.length; i++) {
						objs[i] = strtags[i];
					}
					for (int i = 0, j = 0; i < in.length; i++) {
						if (in[i] != null) {
							objs[strtags.length + j * 2] = (char) ('0' + i);
							objs[strtags.length + j * 2 + 1] = in[i];
							j++;
						}
					}
					exRecipe = ThaumcraftApi.addArcaneCraftingRecipe(research, output, aspects, objs);
				} else {
					ArrayList<ItemStack> in = new ArrayList<>();
					for (ItemStack itemStack : inputs) {
						if (itemStack != null) {
							in.add(itemStack);
						}
					}
					exRecipe = ThaumcraftApi.addShapelessArcaneCraftingRecipe(research, output, aspects,
							(Object[]) inputs);
				}
				boolean isRaw = false;
				if (!isRaw) {
					Object[] data = new Object[9 + inputs.length];
					data[0] = Boolean.valueOf(isShaped);
					data[1] = output;
					data[2] = aspects.getAmount(Aspect.AIR);
					data[3] = aspects.getAmount(Aspect.FIRE);
					data[4] = aspects.getAmount(Aspect.WATER);
					data[5] = aspects.getAmount(Aspect.EARTH);
					data[6] = aspects.getAmount(Aspect.ORDER);
					data[7] = aspects.getAmount(Aspect.ENTROPY);
					data[8] = research;
					for (int i = 0; i < inputs.length; i++) {
						data[i + 9] = inputs[i];
					}
					recipes.add(data);
				}
			}
		}
	}

	private void delRecipe(EntityPlayer player, TileArcaneWorkbench tile) {
		boolean haveItem = false;
		ItemStack[] inputs = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			inputs[i] = tile.getStackInSlot(i);
			if (inputs[i] != null) {
				inputs[i] = inputs[i].copy();
				inputs[i].stackSize = 1;
				haveItem = true;
			}
		}
		if (haveItem) {
			Predicate<Object> filter = recipe -> recipe instanceof IArcaneRecipe
					&& ((IArcaneRecipe) recipe).matches(tile, null, player);
			if (ThaumcraftApi.getCraftingRecipes().removeIf(filter)) {
				recipes.add(inputs);
			}
		}
	}

	@Override
	public String getBlockClassName() {
		return "thaumcraft.common.blocks.BlockTable";
	}

	@Override
	public Object getGuiElement(EntityPlayer player, World world, int x, int y, int z) {
		return new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench) world.getTileEntity(x, y, z));
	}

	@Override
	public Object getContainerElement(EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerArcaneWorkbench(player.inventory, (TileArcaneWorkbench) world.getTileEntity(x, y, z));
	}

}
