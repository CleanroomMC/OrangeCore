package io.github.cleanroommc.orangecore;

import io.github.cleanroommc.orangecore.api.CapabilityFood;
import io.github.cleanroommc.orangecore.api.FoodData;
import io.github.cleanroommc.orangecore.api.FoodHandler;
import io.github.cleanroommc.orangecore.api.IItemFoodOC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonEventHandler {
    @SubscribeEvent
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
    }
}
