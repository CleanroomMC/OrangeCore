package com.cleanroommc.orangecore.commands;

import com.cleanroommc.orangecore.api.Nutrient;
import com.cleanroommc.orangecore.api.OrangeCoreAPI;
import com.cleanroommc.orangecore.api.capability.NutritionCapability;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;

public class CommandNutrition extends CommandBase {
    @Override
    public String getName() {
        return "nutrition";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "orangecore.commands.nutrition.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender commandSender, @Nonnull String[] args) throws CommandException
    {
        if (args.length > 1)
        {
            EntityPlayerMP playerToActOn = args.length > 2 ? getPlayer(server, commandSender, args[0]) : getCommandSenderAsPlayer(commandSender);
            Nutrient nutrient = Nutrient.NUTRIENTS.get(args.length == 2 ? args[0] : args[1]);
            float nutrientValue = (float) parseDouble(args.length == 2 ? args[1] : args[2], -1, 1);
            if (playerToActOn.hasCapability(NutritionCapability.CAPABILITY, null))
            {
                playerToActOn.getCapability(NutritionCapability.CAPABILITY, null).setNutrientValue(nutrient, nutrientValue);
                notifyCommandListener(commandSender, this, 1, "orangecore.commands.nutrition.set.nutrient.to", playerToActOn.getDisplayName(), nutrient.toString(), nutrientValue);
            }
            else
            {
                throw new WrongUsageException("orangecore.commands.nutrition.usage.nocapability");
            }
        }
        else
        {
            throw new WrongUsageException(getUsage(commandSender));
        }
    }
}
