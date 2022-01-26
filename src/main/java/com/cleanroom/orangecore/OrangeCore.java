package com.cleanroom.orangecore;

import com.cleanroom.orangecore.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = OrangeCore.MOD_ID, name = OrangeCore.MOD_NAME)
public class OrangeCore {
    public static final String MOD_ID = "orangecore";
    public static final String MOD_NAME = "OrangeCore";

    @Mod.Instance
    private static OrangeCore INSTANCE = null;

    @SidedProxy(modId = MOD_ID, clientSide = "io.github.cleanroommc.orangecore.proxy.ClientProxy", serverSide = "io.github.cleanroommc.orangecore.proxy.CommonProxy")
    public static CommonProxy proxy;

    public final Logger log = LogManager.getLogger(MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log.info("OrangeCore preinit...");
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        OrangeCoreUtility.init(event.getServer());
    }

    public static Logger getLog() {
        return INSTANCE.log;
    }
}
