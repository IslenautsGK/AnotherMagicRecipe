package com.anotherera.magicrecipe.common.command;

import java.util.Arrays;
import java.util.List;

import com.anotherera.magicrecipe.common.api.ARecipeHandler;
import com.anotherera.magicrecipe.common.recipehandler.RecipeHandlerLoader;
import com.anotherera.magicrecipe.common.recipehandler.RecipeHandlerRegister;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;

public class CommandSaveRecipe extends CommandBase {

	@Override
	public String getCommandName() {
		return "anothermagicrecipe";
	}

	@Override
	public List getCommandAliases() {
		return Arrays.asList(new String[] { "anothermagicrecipe", "amr" });
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "commands.amr.usage";
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] parms) {
		return parms.length == 1
				? getListOfStringsMatchingLastWord(parms, "save", "load", "reset", "reload", "undo", "redo")
				: parms.length == 2 && (parms[0].equals("undo") || parms[0].equals("redo"))
						? getListOfStringsMatchingLastWord(parms, (String[]) RecipeHandlerRegister.getNames())
						: null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] parms) {
		if (parms.length == 1) {
			switch (parms[0]) {
			case "save":
				RecipeHandlerLoader.saveAll();
				sender.addChatMessage(new ChatComponentTranslation("commands.amr.save.success"));
				break;
			case "load":
				RecipeHandlerLoader.loadAll();
				RecipeHandlerLoader.sendAllChange();
				sender.addChatMessage(new ChatComponentTranslation("commands.amr.load.success"));
				break;
			case "reset":
				RecipeHandlerLoader.resetAll();
				RecipeHandlerLoader.saveAll();
				RecipeHandlerLoader.sendAllChange();
				sender.addChatMessage(new ChatComponentTranslation("commands.amr.reset.success"));
				break;
			case "reload":
				RecipeHandlerLoader.resetAll();
				RecipeHandlerLoader.loadAll();
				RecipeHandlerLoader.sendAllChange();
				sender.addChatMessage(new ChatComponentTranslation("commands.amr.reload.success"));
			default:
				throw new WrongUsageException("commands.amr.usage");
			}
		} else if (parms.length == 2) {
			ARecipeHandler rh = null;
			switch (parms[0]) {
			case "undo":
				rh = RecipeHandlerRegister.getHandler(parms[1]);
				if (rh != null) {
					if (rh.undo()) {
						RecipeHandlerLoader.saveAll();
						RecipeHandlerLoader.sendAllChange();
						sender.addChatMessage(new ChatComponentTranslation("commands.amr.rh.undo.success"));
					} else {
						throw new WrongUsageException("commands.amr.rh.undo.failed");
					}
				} else {
					throw new WrongUsageException("commands.amr.rh.notfound", parms[1]);
				}
				break;
			case "redo":
				rh = RecipeHandlerRegister.getHandler(parms[1]);
				if (rh != null) {
					if (rh.redo()) {
						RecipeHandlerLoader.saveAll();
						RecipeHandlerLoader.sendAllChange();
						sender.addChatMessage(new ChatComponentTranslation("commands.amr.rh.redo.success"));
					} else {
						throw new WrongUsageException("commands.amr.rh.redo.failed");
					}
				} else {
					throw new WrongUsageException("commands.amr.rh.notfound", parms[1]);
				}
				break;
			default:
				throw new WrongUsageException("commands.amr.usage");
			}
		} else {
			throw new WrongUsageException("commands.amr.usage");
		}
	}

}
