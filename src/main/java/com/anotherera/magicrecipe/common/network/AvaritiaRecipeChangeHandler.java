package com.anotherera.magicrecipe.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.anotherera.magicrecipe.client.gui.GuiExtremeCrafting;
import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.inventory.ContainerExtremeCrafting;
import com.anotherera.magicrecipe.common.inventory.InventoryCrafting;
import com.anotherera.magicrecipe.common.item.OreDictItem;
import com.anotherera.magicrecipe.common.network.packet.AvaritiaRecipeChangePacket;
import com.anotherera.magicrecipe.common.util.ItemStackUtil;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import fox.spiteful.avaritia.crafting.ExtremeCraftingManager;
import fox.spiteful.avaritia.tile.TileEntityDireCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

@Optional.Interface(iface = "fox.spiteful.avaritia.gui.ContainerExtremeCrafting", modid = "Avaritia")
public class AvaritiaRecipeChangeHandler extends ARecipeHandler<AvaritiaRecipeChangePacket, IMessage> {

	public static List<Object[]> recipes = new ArrayList<>();
	private static List raw = new ArrayList();
	private static List<Object[]> historicalRecord = new ArrayList<>();
	private int historicalRecordIndex = 0;
	private static final String craftingTag = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()-+<>?;:'";

	@Override
	public void init() {
		raw.clear();
		raw.addAll(ExtremeCraftingManager.getInstance().getRecipeList());
	}

	@Override
	public void reset() {
		recipes.clear();
		historicalRecord.clear();
		historicalRecordIndex = 0;
		ExtremeCraftingManager.getInstance().getRecipeList().clear();
		ExtremeCraftingManager.getInstance().getRecipeList().addAll(raw);
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
					addRecipe(isShaped, MinecraftServer.getServer().getEntityWorld(), output, inputs);
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
				delRecipe(inputs);
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
				IRecipe ir;
				if (isShaped) {
					int up = 9, down = -1, left = 9, right = -1, count = 0;
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 9; j++) {
							if (inputs[i * 9 + j] != null) {
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
							strtags[i] += craftingTag.charAt(i * w + j);
						}
					}
					ItemStack[] in = new ItemStack[w * h];
					for (int i = 0; i < h; i++) {
						for (int j = 0; j < w; j++) {
							in[i * w + j] = inputs[(i + up) * 9 + (j + left)];
						}
					}
					Object[] objs = new Object[strtags.length + count * 2];
					for (int i = 0; i < strtags.length; i++) {
						objs[i] = strtags[i];
					}
					for (int i = 0, j = 0; i < in.length; i++) {
						if (in[i] != null) {
							boolean isOreDict = false;
							objs[strtags.length + j * 2] = (char) (craftingTag.charAt(i));
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
					ir = ExtremeCraftingManager.getInstance().addExtremeShapedOreRecipe(output, objs);
					/*
					 * ItemStack[] in = new ItemStack[w * h]; for (int i = 0; i < h; i++) { for (int
					 * j = 0; j < w; j++) { in[i * w + j] = inputs[(i + up) * 9 + (j + left)]; } }
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
					ir = ExtremeCraftingManager.getInstance().addShapelessOreRecipe(output, in.toArray());
				}
				Object[] data = new Object[2 + inputs.length];
				data[0] = Boolean.valueOf(isShaped);
				data[1] = output;
				for (int i = 0; i < inputs.length; i++) {
					data[i + 2] = inputs[i];
				}
				recipes.add(data);
				historicalRecord.add(historicalRecordIndex++, new Object[] { true, ir });
				if (historicalRecordIndex < historicalRecord.size()) {
					for (int i = historicalRecord.size() - 1; i >= historicalRecordIndex; i--) {
						historicalRecord.remove(i);
					}
				}
			}
		}
	}

	private void delRecipe(ItemStack[] inputs) {
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
			boolean removed = false;
			Iterator it = ExtremeCraftingManager.getInstance().getRecipeList().iterator();
			while (it.hasNext()) {
				IRecipe ir = (IRecipe) it.next();
				if (ir.matches(inv, null)) {
					it.remove();
					removed = true;
					historicalRecord.add(historicalRecordIndex++, new Object[] { false, ir });
					if (historicalRecordIndex < historicalRecord.size()) {
						for (int i = historicalRecord.size() - 1; i >= historicalRecordIndex; i--) {
							historicalRecord.remove(i);
						}
					}
				}
			}
			if (removed) {
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

	@Override
	public boolean undo() {
		if (historicalRecordIndex > 0) {
			Object[] obj = historicalRecord.get(--historicalRecordIndex);
			if ((Boolean) obj[0]) {
				ExtremeCraftingManager.getInstance().getRecipeList().removeIf(o -> o == obj[1]);
			} else {
				ExtremeCraftingManager.getInstance().getRecipeList().add(obj[1]);
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
				ExtremeCraftingManager.getInstance().getRecipeList().add(obj[1]);
			} else {
				ExtremeCraftingManager.getInstance().getRecipeList().removeIf(o -> o == obj[1]);
			}
			return true;
		}
		return false;
	}

}
