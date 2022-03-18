package com.cleanroommc.orangecore.api.food;

import com.cleanroommc.orangecore.api.NutrientData;
import net.minecraft.entity.player.EntityPlayer;
import com.cleanroommc.orangecore.api.OrangeCoreAPI;
import com.cleanroommc.orangecore.api.IOrangeCoreAccessor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * FoodValues is a utility class used to retrieve and hold food values.
 *
 * To get food values for any given food, use any of the static {@link #get} methods.
 *
 * <pre>
 * {@code
 * FoodValues appleFoodValues = FoodValues.get(new ItemStack(Items.apple));
 * }
 * </pre>
 */
public class FoodValues
{
	public final int hunger;
	public final float saturationModifier;
	public NutrientData nutrientData = new NutrientData();

	public FoodValues(int hunger, float saturationModifier)
	{
		this.hunger = hunger;
		this.saturationModifier = saturationModifier;
	}

	public FoodValues(int hunger, float saturationModifier, NutrientData nutrientData)
	{
		this.hunger = hunger;
		this.saturationModifier = saturationModifier;
		this.nutrientData = nutrientData;
	}

	public FoodValues(FoodValues other)
	{
		this(other.hunger, other.saturationModifier);
	}

	/**
	 * @return The amount of saturation that the food values would provide, ignoring any limits.
	 */
	public float getUnboundedSaturationIncrement()
	{
		return hunger * saturationModifier * 2f;
	}

	/**
	 * @return The bounded amount of saturation that the food values would provide to this player,
	 * taking their max hunger level into account.
	 */
	public float getSaturationIncrement(EntityPlayer player)
	{
		return Math.min(OrangeCoreAPI.accessor.getMaxHunger(player), getUnboundedSaturationIncrement());
	}

	/**
	 * See {@link IOrangeCoreAccessor#getUnmodifiedFoodValues}
	 */
	public static FoodValues getUnmodified(ItemStack itemStack)
	{
		return OrangeCoreAPI.accessor.getUnmodifiedFoodValues(itemStack);
	}

	/**
	 * See {@link IOrangeCoreAccessor#getFoodValues}
	 */
	public static FoodValues get(@Nonnull ItemStack itemStack)
	{
		return OrangeCoreAPI.accessor.getFoodValues(itemStack);
	}

	/**
	 * See {@link IOrangeCoreAccessor#getFoodValuesForPlayer}
	 */
	public static FoodValues get(@Nonnull ItemStack itemStack, EntityPlayer player)
	{
		return OrangeCoreAPI.accessor.getFoodValuesForPlayer(itemStack, player);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + hunger;
		result = prime * result + Float.floatToIntBits(saturationModifier);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FoodValues other = (FoodValues) obj;
		if (hunger != other.hunger)
			return false;
		if (Float.floatToIntBits(saturationModifier) != Float.floatToIntBits(other.saturationModifier))
			return false;
		return true;
	}
}