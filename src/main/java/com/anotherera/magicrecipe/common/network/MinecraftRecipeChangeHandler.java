package com.anotherera.magicrecipe.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.anotherera.magicrecipe.client.gui.GuiCrafting;
import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.inventory.ContainerWorkbench;
import com.anotherera.magicrecipe.common.inventory.InventoryCrafting;
import com.anotherera.magicrecipe.common.item.OreDictItem;
import com.anotherera.magicrecipe.common.network.packet.MinecraftRecipeChangePacket;
import com.anotherera.magicrecipe.common.util.ItemStackUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class MinecraftRecipeChangeHandler extends ARecipeHandler<MinecraftRecipeChangePacket, IMessage> {

	public static List<Object[]> recipes = new ArrayList<>();
	private static List raw = new ArrayList();
	private static List<Object[]> historicalRecord = new ArrayList<>();
	private int historicalRecordIndex = 0;

	@Override
	public void init() {
		raw.clear();
		raw.addAll(CraftingManager.getInstance().getRecipeList());
	}

	@Override
	public void reset() {
		recipes.clear();
		historicalRecord.clear();
		historicalRecordIndex = 0;
		CraftingManager.getInstance().getRecipeList().clear();
		CraftingManager.getInstance().getRecipeList().addAll(raw);
	}

	@Override
	public void save(DataOutputStream dos) throws IOException {
		for (int h = 0; h < historicalRecordIndex; h++) {
			// for (Object[] objs : recipes) {
			Object[] objs = recipes.get(h);
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
					addRecipe(isShaped, output, inputs);
				} else {
					String tag = dis.readUTF();
					ItemStack[] inputs = new ItemStack[tag.length()];
					for (int i = 0; i < tag.length(); i++) {
						if (tag.charAt(i) == '1') {
							inputs[i] = ItemStackUtil.loadItemStack(dis);
						}
					}
					delRecipe(inputs);
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
				addRecipe(message.isShaped, wb.craftResult.getStackInSlot(0), inputs);
			} else {
				ItemStack[] inputs = new ItemStack[9];
				for (int i = 0; i < 9; i++) {
					inputs[i] = wb.craftMatrix.getStackInSlot(i);
				}
				delRecipe(inputs);
			}
			wb.onCraftMatrixChanged(null);
		}
		return null;
	}

	private void addRecipe(boolean isShaped, ItemStack output, ItemStack[] inputs) {
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
				IRecipe ir;
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
							boolean isOreDict = false;
							objs[strtags.length + j * 2] = (char) ('0' + i);
							if (in[i].getItem() instanceof OreDictItem) {
								if (in[i].hasTagCompound()) {
									NBTTagCompound nbt = in[i].getTagCompound();
									if (nbt.hasKey("oreName")) {
										objs[strtags.length + j * 2 + 1] = nbt.getString("oreName");
										isOreDict = true;
									}
								}
							}
							if (!isOreDict) {
								objs[strtags.length + j * 2 + 1] = in[i];
							}
							j++;
						}
					}
					ir = new ShapedOreRecipe(output, objs);
					CraftingManager.getInstance().getRecipeList().add(ir);
					/*
					 * ItemStack[] in = new ItemStack[w * h]; for (int i = 0; i < h; i++) { for (int
					 * j = 0; j < w; j++) { in[i * w + j] = inputs[(i + up) * 3 + (j + left)]; } }
					 * exRecipe = new ShapedRecipes(w, h, in, output);
					 */
				} else {
					ArrayList<Object> in = new ArrayList<>();
					for (ItemStack itemStack : inputs) {
						if (itemStack != null) {
							boolean isOreDict = false;
							if (itemStack.getItem() instanceof OreDictItem) {
								if (itemStack.hasTagCompound()) {
									NBTTagCompound nbt = itemStack.getTagCompound();
									if (nbt.hasKey("oreName")) {
										in.add(nbt.getString("oreName"));
										isOreDict = true;
									}
								}
							}
							if (!isOreDict) {
								in.add(itemStack);
							}
						}
					}
					ir = new ShapelessOreRecipe(output, in.toArray());
					CraftingManager.getInstance().getRecipeList().add(ir);
				}
				Object[] data = new Object[2 + inputs.length];
				data[0] = Boolean.valueOf(isShaped);
				data[1] = output;
				for (int i = 0; i < inputs.length; i++) {
					data[i + 2] = inputs[i];
				}
				recipes.add(historicalRecordIndex, data);
				historicalRecord.add(historicalRecordIndex, new Object[] { true, ir });
				historicalRecordIndex++;
				if (historicalRecordIndex < historicalRecord.size()) {
					for (int i = historicalRecord.size() - 1; i >= historicalRecordIndex; i--) {
						recipes.remove(i);
						historicalRecord.remove(i);
					}
				}
			}
		}
	}

	private void delRecipe(ItemStack[] inputs) {
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
			Iterator it = CraftingManager.getInstance().getRecipeList().iterator();
			while (it.hasNext()) {
				IRecipe ir = (IRecipe) it.next();
				if (ir.matches(inv, null)) {
					it.remove();
					recipes.add(historicalRecordIndex, inputs);
					historicalRecord.add(historicalRecordIndex, new Object[] { false, ir });
					historicalRecordIndex++;
					if (historicalRecordIndex < historicalRecord.size()) {
						for (int i = historicalRecord.size() - 1; i >= historicalRecordIndex; i--) {
							recipes.remove(i);
							historicalRecord.remove(i);
						}
					}
				}
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

	@Override
	public boolean undo() {
		if (historicalRecordIndex > 0) {
			Object[] obj = historicalRecord.get(--historicalRecordIndex);
			if ((Boolean) obj[0]) {
				CraftingManager.getInstance().getRecipeList().removeIf(o -> o == obj[1]);
			} else {
				CraftingManager.getInstance().getRecipeList().add(obj[1]);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean redo() {
		if (historicalRecordIndex < historicalRecord.size()) {
			Object[] obj = historicalRecord.get(historicalRecordIndex++);
			if ((Boolean) obj[0]) {
				CraftingManager.getInstance().getRecipeList().add(obj[1]);
			} else {
				CraftingManager.getInstance().getRecipeList().removeIf(o -> o == obj[1]);
			}
			return true;
		}
		return false;
	}

}
