package com.cleanroom.orangecore.example;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.cleanroom.orangecore.api.hunger.HungerEvent;

public class MaxHungerModifier
{
	@SubscribeEvent
	public void onGetMaxHunger(HungerEvent.GetMaxHunger event)
	{
		event.maxHunger = 60;
	}
}