package com.anotherera.magicrecipe.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.anotherera.magicrecipe.client.gui.GuiExtremeCrafting;
import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.inventory.ContainerExtremeCrafting;
import com.anotherera.magicrecipe.common.inventory.InventoryCrafting;
import com.anotherera.magicrecipe.common.network.packet.AvaritiaRecipeChangePacket;
import com.anotherera.magicrecipe.common.util.ItemStackUtil;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import fox.spiteful.avaritia.crafting.ExtremeCraftingManager;
import fox.spiteful.avaritia.crafting.ExtremeShapedRecipe;
import fox.spiteful.avaritia.crafting.ExtremeShapelessRecipe;
import fox.spiteful.avaritia.tile.TileEntityDireCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

@Optional.Interface(iface = "fox.spiteful.avaritia.gui.ContainerExtremeCrafting", modid = "Avaritia")
public class AvaritiaRecipeChangeHandler extends ARecipeHandler<AvaritiaRecipeChangePacket, IMessage> {

	public static List<Object[]> recipes = new ArrayList<>();
	private static List raw = new ArrayList();

	@Override
	public void init() {
		raw.clear();
		raw.addAll(ExtremeCraftingManager.getInstance().getRecipeList());
	}

	@Override
	public void reset() {
		ExtremeCraftingManager.getInstance().getRecipeList().clear();
		ExtremeCraftingManager.getInstance().getRecipeList().addAll(raw);
	}

	@Override
	public void save(DataOutputStream dos) throws IOException {
		for (Object[] objs : recipes) {
			if (objs[0] instanceof Boolean) {
				dos.writeBoolean(true);
				dos.writeBoolean((Boolean) objs[0]);
				ItemStackUtil.saveItemStack((ItemStack) objs[1], dos);
				String tag = "";
				for (int i = 2; i < objs.length; i++) {
					tag = objs[i] == null ? tag + "0" : tag + "1";
				}
				dos.writeUTF(tag);
				for (int i = 2; i < objs.length; i++) {
					if (objs[i] != null) {
						ItemStackUtil.saveItemStack((ItemStack) objs[i], dos);
					}
				}
			} else {
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
			}
		}
	}

	@Override
	public void load(DataInputStream dis) throws IOException {
		try {
			while (true) {
				if (dis.readBoolean()) {
					boolean isShaped = dis.readBoolean();
					ItemStack output = ItemStackUtil.loadItemStack(dis);
					String tag = dis.readUTF();
					ItemStack[] inputs = new ItemStack[tag.length()];
					for (int i = 0; i < tag.length(); i++) {
						if (tag.charAt(i) == '1') {
							inputs[i] = ItemStackUtil.loadItemStack(dis);
						}
					}
					addRecipe(isShaped, MinecraftServer.getServer().getEntityWorld(), output, inputs);
				} else {
					String tag = dis.readUTF();
					ItemStack[] inputs = new ItemStack[tag.length()];
					for (int i = 0; i < tag.length(); i++) {
						if (tag.charAt(i) == '1') {
							inputs[i] = ItemStackUtil.loadItemStack(dis);
						}
					}
					delRecipe(MinecraftServer.getServer().getEntityWorld(), inputs);
				}
			}
		} catch (EOFException e) {
		}
	}

	@Override
	protected IMessage messageHandler(AvaritiaRecipeChangePacket message, MessageContext ctx) {
		Container container = ctx.getServerHandler().playerEntity.openContainer;
		if (container instanceof ContainerExtremeCrafting) {
			ContainerExtremeCrafting wb = (ContainerExtremeCrafting) container;
			if (message.isAdd) {
				ItemStack[] inputs = new ItemStack[81];
				for (int i = 0; i < 81; i++) {
					inputs[i] = wb.craftMatrix.getStackInSlot(i);
				}
				addRecipe(message.isShaped, ctx.getServerHandler().playerEntity.worldObj,
						wb.craftResult.getStackInSlot(0), inputs);
			} else {
				ItemStack[] inputs = new ItemStack[81];
				for (int i = 0; i < 81; i++) {
					inputs[i] = wb.craftMatrix.getStackInSlot(i);
				}
				delRecipe(ctx.getServerHandler().playerEntity.worldObj, inputs);
			}
			wb.onCraftMatrixChanged(null);
		}
		return null;
	}

	private void addRecipe(boolean isShaped, World world, ItemStack output, ItemStack[] inputs) {
		if (output != null) {
			output = output.copy();
			boolean haveItem = false;
			for (int i = 0; i < 81; i++) {
				if (inputs[i] != null) {
					inputs[i] = inputs[i].copy();
					inputs[i].stackSize = 1;
					haveItem = true;
				}
			}
			if (haveItem) {
				IRecipe exRecipe;
				if (isShaped) {
					int up = 9, down = -1, left = 9, right = -1;
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 9; j++) {
							if (inputs[i * 9 + j] != null) {
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
					ItemStack[] in = new ItemStack[w * h];
					for (int i = 0; i < h; i++) {
						for (int j = 0; j < w; j++) {
							in[i * w + j] = inputs[(i + up) * 9 + (j + left)];
						}
					}
					exRecipe = new ExtremeShapedRecipe(w, h, in, output);
				} else {
					ArrayList<ItemStack> in = new ArrayList<>();
					for (ItemStack itemStack : inputs) {
						if (itemStack != null) {
							in.add(itemStack);
						}
					}
					exRecipe = new ExtremeShapelessRecipe(output, in);
				}
				Object[] data = new Object[2 + inputs.length];
				data[0] = Boolean.valueOf(isShaped);
				data[1] = output;
				for (int i = 0; i < inputs.length; i++) {
					data[i + 2] = inputs[i];
				}
				recipes.add(data);
				ExtremeCraftingManager.getInstance().getRecipeList().add(exRecipe);
			}
		}
	}

	private void delRecipe(World world, ItemStack[] inputs) {
		boolean haveItem = false;
		for (int i = 0; i < 81; i++) {
			if (inputs[i] != null) {
				inputs[i] = inputs[i].copy();
				inputs[i].stackSize = 1;
				haveItem = true;
			}
		}
		if (haveItem) {
			InventoryCrafting inv = new InventoryCrafting(9, 9);
			inv.stackList = inputs;
			Predicate<IRecipe> filter = recipe -> recipe.matches(inv, world);
			if (ExtremeCraftingManager.getInstance().getRecipeList().removeIf(filter)) {
				recipes.add(inputs);
			}
		}
	}

	@Override
	public String getBlockClassName() {
		return "fox.spiteful.avaritia.blocks.BlockDireCrafting";
	}

	@Override
	public Object getGuiElement(EntityPlayer player, World world, int x, int y, int z) {
		return new GuiExtremeCrafting(player.inventory, world, x, y, z,
				(TileEntityDireCrafting) world.getTileEntity(x, y, z));
	}

	@Override
	public Object getContainerElement(EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerExtremeCrafting(player.inventory, world, x, y, z,
				(TileEntityDireCrafting) world.getTileEntity(x, y, z));
	}

}
