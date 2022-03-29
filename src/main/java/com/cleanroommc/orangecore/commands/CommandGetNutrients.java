package com.cleanroommc.orangecore.commands;

import com.cleanroommc.orangecore.api.Nutrient;
import com.cleanroommc.orangecore.api.capability.NutritionCapability;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandGetNutrients extends CommandBase {
    @Override
    public String getName() {
        return "getnutrients";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "orangecore.commands.getnutrients.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            EntityPlayerMP player = getPlayer(server, sender, args[0]);
            if (player.hasCapability(NutritionCapability.CAPABILITY, null)) {
                for (Nutrient nutrient : Nutrient.NUTRIENTS.values()) {
                    notifyCommandListener(sender, this, nutrient.toString() + ": "
                            + player.getCapability(NutritionCapability.CAPABILITY, null)
                            .getNutrientValue(nutrient));
                }
            }
        } else {
            for (Nutrient nutrient : Nutrient.NUTRIENTS.values()) {
                notifyCommandListener(sender, this, nutrient.toString());
            }
        }
    }
}
