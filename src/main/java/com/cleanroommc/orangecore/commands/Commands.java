package com.cleanroommc.orangecore.commands;

import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;

public class Commands
{
	public static void init(MinecraftServer server)
	{
		CommandHandler commandHandler = (CommandHandler) server.getCommandManager();

		commandHandler.registerCommand(new CommandGetNutrients());
		commandHandler.registerCommand(new CommandHunger());
		commandHandler.registerCommand(new CommandNutrition());
	}
}