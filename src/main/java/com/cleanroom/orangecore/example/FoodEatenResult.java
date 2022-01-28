package com.cleanroom.orangecore.example;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.cleanroom.orangecore.api.food.FoodEvent;

public class FoodEatenResult
{
	@SubscribeEvent
	public void onFoodEaten(FoodEvent.FoodEaten event)
	{
		AppleCoreExample.LOG.info(event.player.getDisplayNameString() + " ate " + event.food.toString());

		if (event.hungerAdded >= 1)
			event.player.heal(1);
	}
}