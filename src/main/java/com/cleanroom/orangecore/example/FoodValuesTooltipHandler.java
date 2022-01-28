package com.cleanroom.orangecore.example;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.cleanroom.orangecore.api.OrangeCoreAPI;
import com.cleanroom.orangecore.api.food.FoodValues;

import java.text.DecimalFormat;

public class FoodValuesTooltipHandler
{
	public static final DecimalFormat DF = new DecimalFormat("##.##");

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemTooltip(ItemTooltipEvent event)
	{
		if (OrangeCoreAPI.accessor.isFood(event.getItemStack()))
		{
			FoodValues unmodifiedValues = OrangeCoreAPI.accessor.getUnmodifiedFoodValues(event.getItemStack());
			FoodValues modifiedValues = OrangeCoreAPI.accessor.getFoodValues(event.getItemStack());
			FoodValues playerValues = OrangeCoreAPI.accessor.getFoodValuesForPlayer(event.getItemStack(), event.getEntityPlayer());

			event.getToolTip().add("Food Values [hunger : satMod (+sat)]");
			event.getToolTip().add("- Player-specific: " + playerValues.hunger + " : " + playerValues.saturationModifier + " (+" + DF.format(playerValues.getSaturationIncrement(event.getEntityPlayer())) + ")");
			event.getToolTip().add("- Player-agnostic: " + modifiedValues.hunger + " : " + modifiedValues.saturationModifier + " (+" + DF.format(modifiedValues.getSaturationIncrement(event.getEntityPlayer())) + ")");
			event.getToolTip().add("- Unmodified: " + unmodifiedValues.hunger + " : " + unmodifiedValues.saturationModifier + " (+" + DF.format(unmodifiedValues.getSaturationIncrement(event.getEntityPlayer())) + ")");

			if (event.getEntityPlayer() != null)
			{
				boolean isCurrentlyEdible = OrangeCoreAPI.accessor.canPlayerEatFood(event.getItemStack(), event.getEntityPlayer());
				event.getToolTip().add(isCurrentlyEdible ? "Can currently be eaten" : "Can not currently be eaten");
			}
		}
	}
}