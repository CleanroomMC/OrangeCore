package io.github.cleanroommc.orangecore;

import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public class OrangeCoreUtility {
    private static MinecraftServer SERVER;

    public static void init(MinecraftServer serverIn) {
        SERVER = serverIn;
    }

    public static boolean doesStackMatchOre(@Nonnull ItemStack stack, String name) {
        if (!OreDictionary.doesOreNameExist(name)) {
            OrangeCore.getLog().warn(String.format("doesStackMatchOre called with non-existing name. stack: {} name: {}", stack.toString(), name));
            return false;
        }
        if (stack.isEmpty()) return false;
        int needle = OreDictionary.getOreID(name);
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (id == needle) return true;
        }
        return false;
    }

    public static int getPlayerTickTime() {
        return SERVER.getTickCounter();
    }
}
