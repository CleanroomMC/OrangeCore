package com.cleanroommc.orangecore.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * This should be implemented on any addon-added food items that extend {@link net.minecraft.item.ItemFood}, instead of using {@link net.minecraft.item.Item#initCapabilities(ItemStack, NBTTagCompound)}
 *
 * WHY:
 * - OrangeCore will attach a food capability instance for EVERY {@link net.minecraft.item.ItemFood} subclass.
 */

public interface IItemFoodOC
{
    /**
     * @return A capability provider which exposes an {@link IFood} capability, e.g. {@link FoodHandler}
     */
    NutrientData getNutrientData(ItemStack stack);
}
