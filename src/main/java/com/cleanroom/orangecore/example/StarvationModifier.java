package com.cleanroom.orangecore.example;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.cleanroom.orangecore.api.hunger.StarvationEvent;

public class StarvationModifier
{
	@SubscribeEvent
	public void allowStarvation(StarvationEvent.AllowStarvation event)
	{
		event.setResult(Event.Result.ALLOW);
	}

	@SubscribeEvent
	public void onStarveTick(StarvationEvent.GetStarveTickPeriod event)
	{
		event.starveTickPeriod = 60;
	}

	@SubscribeEvent(priority= EventPriority.LOWEST)
	public void onStarve(StarvationEvent.Starve event)
	{
		event.starveDamage = 1;
	}
}