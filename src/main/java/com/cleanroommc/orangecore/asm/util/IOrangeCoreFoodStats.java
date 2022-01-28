package com.cleanroommc.orangecore.asm.util;

import net.minecraft.entity.player.EntityPlayer;

public interface IOrangeCoreFoodStats
{
	int getFoodTimer();
	void setFoodTimer(int value);
	int getStarveTimer();
	void setStarveTimer(int value);
	EntityPlayer getPlayer();
	void setPlayer(EntityPlayer player);
	void setPrevFoodLevel(int value);
	float getExhaustion();
	void setExhaustion(float value);
	void setSaturation(float value);
}