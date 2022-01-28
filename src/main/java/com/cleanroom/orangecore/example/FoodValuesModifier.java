package com.cleanroom.orangecore.example;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.cleanroom.orangecore.api.food.FoodEvent;
import com.cleanroom.orangecore.api.food.FoodValues;

public class FoodValuesModifier
{
	@SubscribeEvent
	public void getFoodValues(FoodEvent.GetFoodValues event)
	{
		if (event.food.getItem() == Items.APPLE)
			event.foodValues = new FoodValues(5, 1f);
	}

	@SubscribeEvent
	public void getPlayerSpecificFoodValues(FoodEvent.GetPlayerFoodValues event)
	{
		// Player can be null when, for example, Minecraft caches tooltips at startup,
		// and a tooltip handler forwards that null player to AppleCore
		if (event.player == null)
			return;

		if (event.food.getItem() == Items.APPLE)
			event.foodValues = new FoodValues(19, 1f);
		else
			event.foodValues = new FoodValues((20 - event.player.getFoodStats().getFoodLevel()) / 8, 1);
	}
}