package com.cleanroommc.orangecore.api.food;

import net.minecraft.item.ItemFood;

/**
 * An interface for edible blocks (e.g. cakes).<br>
 * <br>
 * Note: AppleCore will implement this on BlockCake at runtime.
 */
public interface IEdibleBlock extends IEdible
{
	/**
	 * The IEdibleBlock equivalent of {@link ItemFood#setAlwaysEdible}.
	 * Should set whether or not the food can be eaten when at max hunger.
	 */
	public void setEdibleAtMaxHunger(boolean value);
}
