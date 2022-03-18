package com.cleanroommc.orangecore.api;

import com.cleanroommc.orangecore.client.ClientEvents;
import com.cleanroommc.orangecore.CommonEventHandler;
import com.cleanroommc.orangecore.OrangeCoreConfig;
import com.cleanroommc.orangecore.OrangeCoreUtility;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Capability for any food item
 * Allows foods to have nutrients, and also to decay / rot
 */
public interface IFood extends INBTSerializable<NBTTagCompound>
{
    /**
     * The timestamp that this food was created
     * Used to calculate expiration date
     * Rotten food uses {@code Long.MIN_VALUE} as the creation date
     *
     * @return the calendar time of creation
     */
    long getCreationDate();

    /**
     * Sets the creation date. DO NOT USE TO PRESERVE FOOD! Use {@link FoodTrait} instead
     *
     * @param creationDate A calendar time
     */
    void setCreationDate(long creationDate);

    /**
     * Get the date at which this food item will rot
     *
     * @return a calendar time
     */
    long getRottenDate();

    /**
     * @return true if the food is rotten / decayed.
     */
    default boolean isRotten()
    {
        return getRottenDate() < OrangeCoreUtility.getPlayerTickTime();
    }

    /**
     * Get a visible measure of all immutable data associated with food
     * - Nutrition information
     * - Hunger / Saturation
     * - Water (Thirst)
     *
     * @see FoodData
     */
    @Nonnull
    FoodData getData();

    /**
     * Gets the current decay date modifier, including traits
     * Note: there's a difference between the DECAY modifier, and the DECAY DATE modifier, in that they are reciprocals of eachother
     *
     * @return a value between 0 and infinity (0 = instant decay, infinity = never decay)
     */
    float getDecayDateModifier();

    /**
     * Called from {@link CommonEventHandler}
     * If the item is a food capability item, and it was created before the post init, we assume that it is a technical stack, and will not appear in the world without a copy. As such, we set it to non-decaying.
     * This is NOT SERIALIZED on the capability - as a result it will not persist across {@link ItemStack#copy()},
     */
    void setNonDecaying();

    /**
     * Gets the current list of traits on this food
     * Can also be used to add traits to the food
     *
     * @return the traits of the food
     */
    @Nonnull
    List<FoodTrait> getTraits();

    /**
     * Tooltip added to the food item
     * Called from {@link ClientEvents}
     *
     * @param stack the stack in question
     * @param text  the tooltip
     */
    @SideOnly(Side.CLIENT)
    default void addTooltipInfo(@Nonnull ItemStack stack, @Nonnull List<String> text, @Nullable EntityPlayer player)
    {
        // Expiration dates
        if (isRotten())
        {
            text.add(TextFormatting.RED + I18n.format("tfc.tooltip.food_rotten"));
        }
        if (OrangeCoreConfig.debug)
        {
            text.add("Created at " + getCreationDate());
        }

        // Nutrition / Hunger / Saturation / Water Values
        // Hide this based on the shift key (because it's a lot of into)
        if (GuiScreen.isShiftKeyDown())
        {
            text.add(TextFormatting.DARK_GREEN + I18n.format("tfc.tooltip.nutrition"));

            float water = getData().getWater();
            if (water > 0)
            {
                text.add(TextFormatting.GRAY + I18n.format("tfc.tooltip.nutrition_water", String.format("%d", (int) water)));
            }
/* TODO
            for (Nutrient nutrient : Nutrient.values())
            {
                float value = getData().getNutrients()[nutrient.ordinal()];
                if (value > 0)
                {
                    text.add(nutrient.getColor() + I18n.format("tfc.tooltip.nutrition_nutrient", I18n.format(nutrient.name()), String.format("%.1f", value)));
                }
            }
*/
        }
        else
        {
            text.add(TextFormatting.ITALIC + I18n.format("tfc.tooltip.hold_shift_for_nutrition_info"));
        }

        // Add info for each trait
        for (FoodTrait trait : getTraits())
        {
            trait.addTraitInfo(stack, text);
        }
    }
}