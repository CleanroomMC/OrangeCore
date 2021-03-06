package com.cleanroommc.orangecore.api_impl;

import com.cleanroommc.orangecore.api.*;
import com.cleanroommc.orangecore.api.capability.NutritionCapability;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import com.cleanroommc.orangecore.api.food.FoodEvent;
import com.cleanroommc.orangecore.api.food.FoodValues;
import com.cleanroommc.orangecore.api.food.IEdible;
import com.cleanroommc.orangecore.api.hunger.ExhaustionEvent;
import com.cleanroommc.orangecore.api.hunger.HealthRegenEvent;
import com.cleanroommc.orangecore.api.hunger.HungerEvent;
import com.cleanroommc.orangecore.api.hunger.StarvationEvent;
import com.cleanroommc.orangecore.asm.util.IOrangeCoreFoodStats;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public enum OrangeCoreAccessorMutatorImpl implements IOrangeCoreAccessor, IOrangeCoreMutator
{
	INSTANCE;

	OrangeCoreAccessorMutatorImpl()
	{
		OrangeCoreAPI.accessor = this;
		OrangeCoreAPI.mutator = this;
	}

	/*
	 * IAppleCoreAccessor implementation
	 */
	@Override
	public boolean isFood(@Nonnull ItemStack food)
	{
		return isEdible(food) && getUnmodifiedFoodValues(food) != null;
	}

	private boolean isEdible(@Nonnull ItemStack food)
	{
		if (food == ItemStack.EMPTY)
			return false;

		EnumAction useAction = food.getItem().getItemUseAction(food);
		if (useAction == EnumAction.EAT || useAction == EnumAction.DRINK)
			return true;

		// assume Block-based foods are edible
		return OrangeCoreAPI.registry.getEdibleBlockFromItem(food.getItem()) != null;
	}

	@Override
	public boolean canPlayerEatFood(@Nonnull ItemStack food, @Nonnull EntityPlayer player)
	{
		return player.getFoodStats().getFoodLevel() < getMaxHunger(player) || isAlwaysEdible(food);
	}

	protected static final Field itemFoodAlwaysEdible = ReflectionHelper.findField(ItemFood.class, "alwaysEdible", "field_77852_bZ", "e");

	private boolean isAlwaysEdible(@Nonnull ItemStack food)
	{
		if (food == ItemStack.EMPTY || !(food.getItem() instanceof ItemFood))
			return false;

		try
		{
			return itemFoodAlwaysEdible.getBoolean(food.getItem());
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public FoodValues getUnmodifiedFoodValues(@Nonnull ItemStack food)
	{
		if (food != ItemStack.EMPTY)
		{
			if (food.getItem() instanceof IEdible)
				return ((IEdible) food.getItem()).getFoodValues(food);
			else if (food.getItem() instanceof ItemFood)
				return getItemFoodValues((ItemFood) food.getItem(), food);

			Block block = OrangeCoreAPI.registry.getEdibleBlockFromItem(food.getItem());
			if (block != null && block instanceof IEdible)
				return ((IEdible) block).getFoodValues(food);
		}
		return null;
	}

	private FoodValues getItemFoodValues(@Nonnull ItemFood itemFood, @Nonnull ItemStack itemStack)
	{
		return new FoodValues(itemFood.getHealAmount(itemStack), itemFood.getSaturationModifier(itemStack), ((IItemFoodOC)itemFood).getNutrientData(itemStack));
	}

	@Override
	public FoodValues getFoodValues(@Nonnull ItemStack food)
	{
		FoodValues foodValues = getUnmodifiedFoodValues(food);
		if (foodValues != null)
		{
			FoodEvent.GetFoodValues event = new FoodEvent.GetFoodValues(food, foodValues);
			MinecraftForge.EVENT_BUS.post(event);
			return event.foodValues;
		}
		return null;
	}

	@Override
	public FoodValues getFoodValuesForPlayer(@Nonnull ItemStack food, EntityPlayer player)
	{
		FoodValues foodValues = getFoodValues(food);
		if (foodValues != null)
		{
			FoodEvent.GetPlayerFoodValues event = new FoodEvent.GetPlayerFoodValues(player, food, foodValues);
			MinecraftForge.EVENT_BUS.post(event);
			return event.foodValues;
		}
		return null;
	}

	@Override
	public float getExhaustion(EntityPlayer player)
	{
		try
		{
			return getOrangeCoreFoodStats(player).getExhaustion();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			return 0f;
		}
	}

	@Override
	public float getMaxExhaustion(EntityPlayer player)
	{
		ExhaustionEvent.GetMaxExhaustion event = new ExhaustionEvent.GetMaxExhaustion(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.maxExhaustionLevel;
	}

	@Override
	public int getHealthRegenTickPeriod(EntityPlayer player)
	{
		HealthRegenEvent.GetRegenTickPeriod event = new HealthRegenEvent.GetRegenTickPeriod(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.regenTickPeriod;
	}

	@Override
	public int getStarveDamageTickPeriod(EntityPlayer player)
	{
		StarvationEvent.GetStarveTickPeriod event = new StarvationEvent.GetStarveTickPeriod(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.starveTickPeriod;
	}

	@Override
	public int getSaturatedHealthRegenTickPeriod(EntityPlayer player)
	{
		HealthRegenEvent.GetSaturatedRegenTickPeriod event = new HealthRegenEvent.GetSaturatedRegenTickPeriod(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.regenTickPeriod;
	}

	@Override
	public int getMaxHunger(EntityPlayer player)
	{
		HungerEvent.GetMaxHunger event = new HungerEvent.GetMaxHunger(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.maxHunger;
	}

	/*
	 * IAppleCoreMutator implementation
	 */
	@Override
	public void setExhaustion(EntityPlayer player, float exhaustion)
	{
		try
		{
			getOrangeCoreFoodStats(player).setExhaustion(exhaustion);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setHunger(EntityPlayer player, int hunger)
	{
		player.getFoodStats().setFoodLevel(hunger);
	}

	@Override
	public void setNutrient(EntityPlayer player, Nutrient nutrient, int value) {
		if(player.hasCapability(NutritionCapability.CAPABILITY, null)) {
			player.getCapability(NutritionCapability.CAPABILITY, null).setNutrientValue(nutrient, value);
		}
	}

	@Override
	public void setSaturation(EntityPlayer player, float saturation)
	{
		try
		{
			getOrangeCoreFoodStats(player).setSaturation(saturation);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setHealthRegenTickCounter(EntityPlayer player, int tickCounter)
	{
		try
		{
			getOrangeCoreFoodStats(player).setFoodTimer(tickCounter);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setStarveDamageTickCounter(EntityPlayer player, int tickCounter)
	{
		try
		{
			getOrangeCoreFoodStats(player).setStarveTimer(tickCounter);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public IOrangeCoreFoodStats getOrangeCoreFoodStats(EntityPlayer player) throws ClassCastException
	{
		return (IOrangeCoreFoodStats) player.getFoodStats();
	}
}