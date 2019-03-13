package com.anotherera.magicrecipe.common.command;

import java.util.Arrays;
import java.util.List;

import com.anotherera.magicrecipe.common.recipehandler.RecipeHandlerLoader;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandSaveRecipe extends CommandBase {

	@Override
	public String getCommandName() {
		return "saverecipe";
	}

	@Override
	public List getCommandAliases() {
		String[] names = { "sr", "saver" };
		return Arrays.asList(names);
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "";
	}

	@Override
	public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
		RecipeHandlerLoader.saveAll();
	}

}
