package com.cleanroommc.orangecore;

import com.cleanroommc.orangecore.asm.TransformerModuleHandler;
import com.cleanroommc.orangecore.proxy.CommonProxy;
import com.cleanroommc.airlock.api.asm.ObfHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.SortingIndex(1100)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@Mod.EventBusSubscriber
@Mod(modid = OrangeCore.MOD_ID, name = OrangeCore.MOD_NAME)
public class OrangeCore implements IFMLLoadingPlugin {
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

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{TransformerModuleHandler.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        ObfHelper.setObfuscated((Boolean) data.get("runtimeDeobfuscationEnabled"));
        ObfHelper.setRunsAfterDeobfRemapper(true);
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
