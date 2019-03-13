package com.anotherera.magicrecipe.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.anotherera.magicrecipe.client.gui.GuiCrafting;
import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.inventory.ContainerWorkbench;
import com.anotherera.magicrecipe.common.inventory.InventoryCrafting;
import com.anotherera.magicrecipe.common.network.packet.MinecraftRecipeChangePacket;
import com.anotherera.magicrecipe.common.util.ItemStackUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class MinecraftRecipeChangeHandler extends ARecipeHandler<MinecraftRecipeChangePacket, IMessage> {

	public static List<Object[]> recipes = new ArrayList<>();
	private static List raw = new ArrayList();

	@Override
	public void init() {
		raw.clear();
		raw.addAll(CraftingManager.getInstance().getRecipeList());
	}

	@Override
	public void reset() {
		CraftingManager.getInstance().getRecipeList().clear();
		CraftingManager.getInstance().getRecipeList().addAll(raw);
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
	protected IMessage messageHandler(MinecraftRecipeChangePacket message, MessageContext ctx) {
		Container container = ctx.getServerHandler().playerEntity.openContainer;
		if (container instanceof ContainerWorkbench) {
			ContainerWorkbench wb = (ContainerWorkbench) container;
			if (message.isAdd) {
				ItemStack[] inputs = new ItemStack[9];
				for (int i = 0; i < 9; i++) {
					inputs[i] = wb.craftMatrix.getStackInSlot(i);
				}
				addRecipe(message.isShaped, ctx.getServerHandler().playerEntity.worldObj,
						wb.craftResult.getStackInSlot(0), inputs);
			} else {
				ItemStack[] inputs = new ItemStack[9];
				for (int i = 0; i < 9; i++) {
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
			for (int i = 0; i < 9; i++) {
				if (inputs[i] != null) {
					inputs[i] = inputs[i].copy();
					inputs[i].stackSize = 1;
					haveItem = true;
				}
			}
			if (haveItem) {
				IRecipe exRecipe;
				if (isShaped) {
					int up = 3, down = -1, left = 3, right = -1;
					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							if (inputs[i * 3 + j] != null) {
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
							in[i * w + j] = inputs[(i + up) * 3 + (j + left)];
						}
					}
					exRecipe = new ShapedRecipes(w, h, in, output);
				} else {
					ArrayList<ItemStack> in = new ArrayList<>();
					for (ItemStack itemStack : inputs) {
						if (itemStack != null) {
							in.add(itemStack);
						}
					}
					exRecipe = new ShapelessRecipes(output, in);
				}
				boolean isRaw = false;
				if (!isRaw) {
					Object[] data = new Object[2 + inputs.length];
					data[0] = Boolean.valueOf(isShaped);
					data[1] = output;
					for (int i = 0; i < inputs.length; i++) {
						data[i + 2] = inputs[i];
					}
					recipes.add(data);
				}
				CraftingManager.getInstance().getRecipeList().add(exRecipe);
			}
		}
	}

	private void delRecipe(World world, ItemStack[] inputs) {
		boolean haveItem = false;
		for (int i = 0; i < 9; i++) {
			if (inputs[i] != null) {
				inputs[i] = inputs[i].copy();
				inputs[i].stackSize = 1;
				haveItem = true;
			}
		}
		if (haveItem) {
			InventoryCrafting inv = new InventoryCrafting(3, 3);
			inv.stackList = inputs;
			Predicate<IRecipe> filter = recipe -> recipe.matches(inv, world);
			if (CraftingManager.getInstance().getRecipeList().removeIf(filter)) {
				recipes.add(inputs);
			}
		}
	}

	@Override
	public String getBlockClassName() {
		return "net.minecraft.block.BlockWorkbench";
	}

	@Override
	public Object getGuiElement(EntityPlayer player, World world, int x, int y, int z) {
		return new GuiCrafting(player.inventory, world, x, y, z);
	}

	@Override
	public Object getContainerElement(EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerWorkbench(player.inventory, world, x, y, z);
	}

}
