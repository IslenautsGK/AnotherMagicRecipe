package com.anotherera.magicrecipe.common.inventory;

import java.util.ArrayList;
import java.util.List;

import com.anotherera.magicrecipe.AnotherMagicRecipe;
import com.anotherera.magicrecipe.common.api.ARecipeHandler;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiElementLoader implements IGuiHandler {

	public static final int WORK_BENCH = 0;
	public static final int ARCANE_WORKBENCH = 1;
	public static final int AVARITIA_WORKBENCH = 2;
	private static List<ARecipeHandler> elements = new ArrayList<>();

	public static void regist(ARecipeHandler rh) {
		elements.add(rh);
	}

	public static int getId(ARecipeHandler rh) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getClass().getName().equals(rh.getClass().getName())) {
				return i;
			}
		}
		return -1;
	}

	public static void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(AnotherMagicRecipe.instance, new GuiElementLoader());
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		/*
		 * switch (ID) { case WORK_BENCH: return new
		 * ContainerWorkbench(player.inventory, world, x, y, z); case ARCANE_WORKBENCH:
		 * return getThaumCraftArcaneWorkbenchContainer(player, world, x, y, z); case
		 * AVARITIA_WORKBENCH: return getArcaneWorkbenchContainer(player, world, x, y,
		 * z); default: return null; }
		 */
		return elements.get(ID) == null ? null : elements.get(ID).getContainerElement(player, world, x, y, z);
	}

	/*
	 * @Optional.Method(modid = "Thaumcraft") private ContainerArcaneWorkbench
	 * getThaumCraftArcaneWorkbenchContainer(EntityPlayer player, World world, int
	 * x, int y, int z) { return new ContainerArcaneWorkbench(player.inventory,
	 * (TileArcaneWorkbench) world.getTileEntity(x, y, z)); }
	 * 
	 * @Optional.Method(modid = "Avaritia") private ContainerExtremeCrafting
	 * getArcaneWorkbenchContainer(EntityPlayer player, World world, int x, int y,
	 * int z) { return new ContainerExtremeCrafting(player.inventory, world, x, y,
	 * z, (TileEntityDireCrafting) world.getTileEntity(x, y, z)); }
	 */

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		/*
		 * switch (ID) { case WORK_BENCH: return new GuiCrafting(player.inventory,
		 * world, x, y, z); case ARCANE_WORKBENCH: return
		 * getThaumCraftGuiArcaneWorkbench(player, world, x, y, z); case
		 * AVARITIA_WORKBENCH: return getArcaneGuiWorkbench(player, world, x, y, z);
		 * default: return null; }
		 */
		return elements.get(ID) == null ? null : elements.get(ID).getGuiElement(player, world, x, y, z);
	}

	/*
	 * @Optional.Method(modid = "Thaumcraft") private GuiArcaneWorkbench
	 * getThaumCraftGuiArcaneWorkbench(EntityPlayer player, World world, int x, int
	 * y, int z) { return new GuiArcaneWorkbench(player.inventory,
	 * (TileArcaneWorkbench) world.getTileEntity(x, y, z)); }
	 * 
	 * @Optional.Method(modid = "Avaritia") private GuiExtremeCrafting
	 * getArcaneGuiWorkbench(EntityPlayer player, World world, int x, int y, int z)
	 * { return new GuiExtremeCrafting(player.inventory, world, x, y, z,
	 * (TileEntityDireCrafting) world.getTileEntity(x, y, z)); }
	 */

}
