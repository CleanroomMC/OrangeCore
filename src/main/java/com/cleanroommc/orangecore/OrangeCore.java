package com.cleanroommc.orangecore;

import com.cleanroommc.orangecore.api_impl.OrangeCoreAccessorMutatorImpl;
import com.cleanroommc.orangecore.api_impl.OrangeCoreRegistryImpl;
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
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.SortingIndex(1100)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class OrangeCore implements IFMLLoadingPlugin {
/*    public OrangeCore() {
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.orangecore_itemfood_injection.json");
    }*/

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
