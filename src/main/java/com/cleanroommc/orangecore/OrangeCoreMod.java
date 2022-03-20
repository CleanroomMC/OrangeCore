package com.cleanroommc.orangecore;

import com.cleanroommc.orangecore.api_impl.OrangeCoreAccessorMutatorImpl;
import com.cleanroommc.orangecore.api_impl.OrangeCoreRegistryImpl;
import com.cleanroommc.orangecore.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = OrangeCoreMod.MOD_ID, name = OrangeCoreMod.MOD_NAME)
public class OrangeCoreMod {
    public static final String MOD_ID = "orangecore";
    public static final String MOD_NAME = "OrangeCore";


    @Mod.Instance
    private static OrangeCoreMod INSTANCE = null;

    @SidedProxy(modId = MOD_ID, clientSide = "com.cleanroommc.orangecore.proxy.ClientProxy", serverSide = "com.cleanroommc.orangecore.proxy.CommonProxy")
    public static CommonProxy proxy;

    public final Logger log = LogManager.getLogger(MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log.info("OrangeCore preinit...");
        OrangeCoreAccessorMutatorImpl.values(); // Initialize the "simpletons" inside
        OrangeCoreRegistryImpl.values();

    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        OrangeCoreUtility.init(event.getServer());
    }

    public static Logger getLog() {
        return INSTANCE.log;
    }

}
