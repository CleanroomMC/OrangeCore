package io.github.cleanroommc.orangecore;

import net.minecraft.server.MinecraftServer;

public class OrangeCoreUtility {
    private static MinecraftServer SERVER;

    public static void init(MinecraftServer serverIn) {
        SERVER = serverIn;
    }

    public static int getPlayerTickTime() {
        return SERVER.getTickCounter();
    }
}
