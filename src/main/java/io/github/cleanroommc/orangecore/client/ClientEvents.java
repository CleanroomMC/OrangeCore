package io.github.cleanroommc.orangecore.client;

import io.github.cleanroommc.orangecore.OrangeCore;
import io.github.cleanroommc.orangecore.api.CapabilityFood;
import io.github.cleanroommc.orangecore.api.IFood;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Mod.EventBusSubscriber(modid = OrangeCore.MOD_ID)
public class ClientEvents {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        List<String> tt = event.getToolTip();
        if (!stack.isEmpty()) {
            IFood nutrients = stack.getCapability(CapabilityFood.CAPABILITY, null);
            if (nutrients != null) {
                nutrients.addTooltipInfo(stack, tt, event.getEntityPlayer());
            }
        }
    }

}
