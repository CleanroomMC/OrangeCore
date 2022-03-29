package com.cleanroommc.orangecore;

import com.cleanroommc.orangecore.api.capability.INutritionStats;
import com.cleanroommc.orangecore.api.capability.NutritionCapability;
import com.cleanroommc.orangecore.api.capability.NutritionStatsHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = OrangeCoreMod.MOD_ID)
public class CommonEventHandler {
/*    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        Item item = stack.getItem();
        if (!stack.isEmpty()) {
            ICapabilityProvider foodHandler = CapabilityFood.getCustomFood(stack);
            if (foodHandler != null || stack.getItem() instanceof ItemFood) {
                if (stack.getItem() instanceof IItemFoodOC) {
                    foodHandler = ((IItemFoodOC) stack.getItem()).getCustomFoodHandler();
                }
                if (foodHandler == null) {
                    foodHandler = new FoodHandler(stack.getTagCompound(), new FoodData());
                }
                event.addCapability(CapabilityFood.KEY, foodHandler);
            }
        }
    }*/

    @SubscribeEvent
    public static void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getObject();
            if (!player.hasCapability(NutritionCapability.CAPABILITY, null))
            {
                event.addCapability(NutritionCapability.KEY, new NutritionStatsHandler(player));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {


        if (event.player.hasCapability(NutritionCapability.CAPABILITY, null)) {
            INutritionStats stats = event.player.getCapability(NutritionCapability.CAPABILITY, null);
            stats.updateNutrients(event.player);
        }
    }
}
