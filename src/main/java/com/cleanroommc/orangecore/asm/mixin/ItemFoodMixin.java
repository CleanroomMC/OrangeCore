package com.cleanroommc.orangecore.asm.mixin;

import com.cleanroommc.orangecore.api.IItemFoodOC;
import com.cleanroommc.orangecore.api.Nutrient;
import com.cleanroommc.orangecore.api.NutrientData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemFood.class)
public class ItemFoodMixin extends Item implements IItemFoodOC {
    private NutrientData generalNutrientData;
    private Map<Integer, NutrientData> metaDataSpecificNutrientData = new HashMap<Integer, NutrientData>();


    /**
     *
     * @param nutrient
     * @param value
     */
    public void setDefaultNutrient(Nutrient nutrient, float value) {
        if (generalNutrientData == null)
            generalNutrientData = new NutrientData();
        generalNutrientData.addNutrient(nutrient, value);
    }

    public void setDefaultNutrient(Nutrient nutrient, float value, int metadata) {
        NutrientData specifiedData = metaDataSpecificNutrientData.getOrDefault(metadata, new NutrientData());
        specifiedData.addNutrient(nutrient, value);
    }

    @Override
    public NutrientData getNutrientData(ItemStack stack) {
        return generalNutrientData != null ? generalNutrientData : metaDataSpecificNutrientData.get(stack.getItemDamage());
    }
}
