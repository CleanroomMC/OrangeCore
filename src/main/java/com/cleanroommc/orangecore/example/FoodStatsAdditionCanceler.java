package com.cleanroommc.orangecore.example;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.cleanroommc.orangecore.api.OrangeCoreAPI;
import com.cleanroommc.orangecore.api.food.FoodEvent;

public class FoodStatsAdditionCanceler
{
	@SubscribeEvent
	public void onFoodStatsAddition(FoodEvent.FoodStatsAddition event)
	{
		if (event.player.getFoodStats().getFoodLevel() > OrangeCoreAPI.accessor.getMaxHunger(event.player) / 2)
			event.setCanceled(true);
	}
}
