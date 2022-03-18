package com.cleanroommc.orangecore.asm;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.cleanroommc.orangecore.api.OrangeCoreAPI;
import com.cleanroommc.orangecore.api.food.FoodEvent;
import com.cleanroommc.orangecore.api.food.FoodValues;
import com.cleanroommc.orangecore.api.food.ItemFoodProxy;
import com.cleanroommc.orangecore.api.hunger.ExhaustionEvent;
import com.cleanroommc.orangecore.api.hunger.HealthRegenEvent;
import com.cleanroommc.orangecore.api.hunger.HungerRegenEvent;
import com.cleanroommc.orangecore.api.hunger.StarvationEvent;
import com.cleanroommc.orangecore.asm.util.IOrangeCoreFoodStats;

import javax.annotation.Nonnull;

public class Hooks
{
	private static void verifyFoodStats(FoodStats foodStats, EntityPlayer player)
	{
		if (!(foodStats instanceof IOrangeCoreFoodStats))
		{
			String playerName = player != null ? player.getName() : "<unknown>";
			String className = foodStats.getClass().getName();
			throw new RuntimeException("FoodStats does not implement IAppleCoreFoodStats on player '"+playerName+"' (class = "+className+"). This likely means that AppleCore has failed catastrophically in some way.");
		}
		if (((IOrangeCoreFoodStats)foodStats).getPlayer() == null)
		{
			// attempt to salvage the situation by setting the field here
			if (player != null)
			{
				((IOrangeCoreFoodStats)foodStats).setPlayer(player);
				return;
			}
			String playerName = "<unknown>";
			String className = foodStats.getClass().getName();
			throw new RuntimeException("FoodStats has a null player field (this field is added by AppleCore at runtime) on player '"+playerName+"' (class = "+className+"). This likely means that some mod has overloaded FoodStats, which is incompatible with AppleCore.");
		}
	}

	public static boolean onOrangeCoreFoodStatsUpdate(FoodStats foodStats, EntityPlayer player)
	{
		verifyFoodStats(foodStats, player);

		IOrangeCoreFoodStats orangeCoreFoodStats = (IOrangeCoreFoodStats) foodStats;

		orangeCoreFoodStats.setPrevFoodLevel(foodStats.getFoodLevel());

		Result allowExhaustionResult = Hooks.fireAllowExhaustionEvent(player);
		float maxExhaustion = Hooks.fireExhaustionTickEvent(player, orangeCoreFoodStats.getExhaustion());
		if (allowExhaustionResult == Result.ALLOW || (allowExhaustionResult == Result.DEFAULT && orangeCoreFoodStats.getExhaustion() >= maxExhaustion))
		{
			ExhaustionEvent.Exhausted exhaustedEvent = Hooks.fireExhaustionMaxEvent(player, maxExhaustion, orangeCoreFoodStats.getExhaustion());

			orangeCoreFoodStats.setExhaustion(orangeCoreFoodStats.getExhaustion() + exhaustedEvent.deltaExhaustion);
			if (!exhaustedEvent.isCanceled())
			{
				orangeCoreFoodStats.setSaturation(Math.max(foodStats.getSaturationLevel() + exhaustedEvent.deltaSaturation, 0.0F));
				foodStats.setFoodLevel(Math.max(foodStats.getFoodLevel() + exhaustedEvent.deltaHunger, 0));
			}
		}

		boolean hasNaturalRegen = player.world.getGameRules().getBoolean("naturalRegeneration");

		Result allowSaturatedRegenResult = Hooks.fireAllowSaturatedRegenEvent(player);
		boolean shouldDoSaturatedRegen = allowSaturatedRegenResult == Result.ALLOW || (allowSaturatedRegenResult == Result.DEFAULT && hasNaturalRegen && foodStats.getSaturationLevel() > 0.0F && player.shouldHeal() && foodStats.getFoodLevel() >= 20);
		Result allowRegenResult = shouldDoSaturatedRegen ? Result.DENY : Hooks.fireAllowRegenEvent(player);
		boolean shouldDoRegen = allowRegenResult == Result.ALLOW || (allowRegenResult == Result.DEFAULT && hasNaturalRegen && foodStats.getFoodLevel() >= 18 && player.shouldHeal());
		if (shouldDoSaturatedRegen)
		{
			orangeCoreFoodStats.setFoodTimer(orangeCoreFoodStats.getFoodTimer() + 1);

			if (orangeCoreFoodStats.getFoodTimer() >= Hooks.fireSaturatedRegenTickEvent(player))
			{
				HealthRegenEvent.SaturatedRegen saturatedRegenEvent = Hooks.fireSaturatedRegenEvent(player);
				if (!saturatedRegenEvent.isCanceled())
				{
					player.heal(saturatedRegenEvent.deltaHealth);
					foodStats.addExhaustion(saturatedRegenEvent.deltaExhaustion);
				}
				orangeCoreFoodStats.setFoodTimer(0);
			}
		}
		else if (shouldDoRegen)
		{
			orangeCoreFoodStats.setFoodTimer(orangeCoreFoodStats.getFoodTimer() + 1);

			if (orangeCoreFoodStats.getFoodTimer() >= Hooks.fireRegenTickEvent(player))
			{
				HealthRegenEvent.Regen regenEvent = Hooks.fireRegenEvent(player);
				if (!regenEvent.isCanceled())
				{
					player.heal(regenEvent.deltaHealth);
					foodStats.addExhaustion(regenEvent.deltaExhaustion);
				}
				orangeCoreFoodStats.setFoodTimer(0);
			}
		}
		else
		{
			orangeCoreFoodStats.setFoodTimer(0);
		}

		Result allowStarvationResult = Hooks.fireAllowStarvation(player);
		if (allowStarvationResult == Result.ALLOW || (allowStarvationResult == Result.DEFAULT && foodStats.getFoodLevel() <= 0))
		{
			orangeCoreFoodStats.setStarveTimer(orangeCoreFoodStats.getStarveTimer() + 1);

			if (orangeCoreFoodStats.getStarveTimer() >= Hooks.fireStarvationTickEvent(player))
			{
				StarvationEvent.Starve starveEvent = Hooks.fireStarveEvent(player);
				if (!starveEvent.isCanceled())
				{
					player.attackEntityFrom(DamageSource.STARVE, starveEvent.starveDamage);
				}
				orangeCoreFoodStats.setStarveTimer(0);
			}
		}
		else
		{
			orangeCoreFoodStats.setStarveTimer(0);
		}

		return true;
	}

	public static FoodValues onFoodStatsAdded(FoodStats foodStats, @Nonnull ItemFood itemFood, @Nonnull ItemStack itemStack, EntityPlayer player)
	{
		verifyFoodStats(foodStats, player);
		return OrangeCoreAPI.accessor.getFoodValuesForPlayer(itemStack, player);
	}

	public static void onPostFoodStatsAdded(FoodStats foodStats, @Nonnull ItemFood itemFood, @Nonnull ItemStack itemStack, FoodValues foodValues, int hungerAdded, float saturationAdded, EntityPlayer player)
	{
		verifyFoodStats(foodStats, player);
		MinecraftForge.EVENT_BUS.post(new FoodEvent.FoodEaten(player, itemStack, foodValues, hungerAdded, saturationAdded));
	}

	public static int getItemInUseMaxCount(EntityLivingBase entityLiving, int savedMaxDuration)
	{
		EnumAction useAction = entityLiving.getActiveItemStack().getItemUseAction();
		if (useAction == EnumAction.EAT || useAction == EnumAction.DRINK)
			return savedMaxDuration;
		else
			return entityLiving.getActiveItemStack().getMaxItemUseDuration();
	}

	public static void onBlockFoodEaten(Block block, World world, EntityPlayer player)
	{
		com.cleanroommc.orangecore.api.food.IEdible edibleBlock = (com.cleanroommc.orangecore.api.food.IEdible)block;
		ItemStack itemStack = new ItemStack(OrangeCoreAPI.registry.getItemFromEdibleBlock(block));
		new ItemFoodProxy(edibleBlock).onEaten(itemStack, player);
	}

	public static Result fireAllowExhaustionEvent(EntityPlayer player)
	{
		ExhaustionEvent.AllowExhaustion event = new ExhaustionEvent.AllowExhaustion(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getResult();
	}

	public static float fireExhaustionTickEvent(EntityPlayer player, float foodExhaustionLevel)
	{
		return OrangeCoreAPI.accessor.getMaxExhaustion(player);
	}

	public static ExhaustionEvent.Exhausted fireExhaustionMaxEvent(EntityPlayer player, float maxExhaustionLevel, float foodExhaustionLevel)
	{
		ExhaustionEvent.Exhausted event = new ExhaustionEvent.Exhausted(player, maxExhaustionLevel, foodExhaustionLevel);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	public static Result fireAllowRegenEvent(EntityPlayer player)
	{
		HealthRegenEvent.AllowRegen event = new HealthRegenEvent.AllowRegen(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getResult();
	}

	public static Result fireAllowSaturatedRegenEvent(EntityPlayer player)
	{
		HealthRegenEvent.AllowSaturatedRegen event = new HealthRegenEvent.AllowSaturatedRegen(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getResult();
	}

	public static int fireRegenTickEvent(EntityPlayer player)
	{
		return OrangeCoreAPI.accessor.getHealthRegenTickPeriod(player);
	}

	public static int fireSaturatedRegenTickEvent(EntityPlayer player)
	{
		return OrangeCoreAPI.accessor.getSaturatedHealthRegenTickPeriod(player);
	}

	public static HealthRegenEvent.Regen fireRegenEvent(EntityPlayer player)
	{
		HealthRegenEvent.Regen event = new HealthRegenEvent.Regen(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	public static HealthRegenEvent.SaturatedRegen fireSaturatedRegenEvent(EntityPlayer player)
	{
		HealthRegenEvent.SaturatedRegen event = new HealthRegenEvent.SaturatedRegen(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	public static HealthRegenEvent.PeacefulRegen firePeacefulRegenEvent(EntityPlayer player)
	{
		HealthRegenEvent.PeacefulRegen event = new HealthRegenEvent.PeacefulRegen(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	public static HungerRegenEvent.PeacefulRegen firePeacefulHungerRegenEvent(EntityPlayer player)
	{
		HungerRegenEvent.PeacefulRegen event = new HungerRegenEvent.PeacefulRegen(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	public static Result fireAllowStarvation(EntityPlayer player)
	{
		StarvationEvent.AllowStarvation event = new StarvationEvent.AllowStarvation(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getResult();
	}

	public static int fireStarvationTickEvent(EntityPlayer player)
	{
		return OrangeCoreAPI.accessor.getStarveDamageTickPeriod(player);
	}

	public static StarvationEvent.Starve fireStarveEvent(EntityPlayer player)
	{
		StarvationEvent.Starve event = new StarvationEvent.Starve(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

	public static boolean fireFoodStatsAdditionEvent(EntityPlayer player, FoodValues foodValuesToBeAdded)
	{
		FoodEvent.FoodStatsAddition event = new FoodEvent.FoodStatsAddition(player, foodValuesToBeAdded);
		MinecraftForge.EVENT_BUS.post(event);
		return event.isCancelable() && event.isCanceled();
	}

	public static boolean needFood(FoodStats foodStats)
	{
		verifyFoodStats(foodStats, null);
		return foodStats.getFoodLevel() < getMaxHunger(foodStats);
	}

	public static int getMaxHunger(FoodStats foodStats)
	{
		verifyFoodStats(foodStats, null);

		EntityPlayer player = ((IOrangeCoreFoodStats) foodStats).getPlayer();
		return OrangeCoreAPI.accessor.getMaxHunger(player);
	}

	@SideOnly(Side.CLIENT)
	public static int getHungerForDisplay(FoodStats foodStats)
	{
		if (!(foodStats instanceof IOrangeCoreFoodStats))
			return foodStats.getFoodLevel();

		// return a scaled value so that the HUD can still use the same logic
		// as if the max was 20
		EntityPlayer player = ((IOrangeCoreFoodStats) foodStats).getPlayer();
		float scale = 20f / OrangeCoreAPI.accessor.getMaxHunger(player);
		int realHunger = foodStats.getFoodLevel();

		// only return 0 if the real hunger value is 0
		if (realHunger == 0)
			return 0;

		// floor here so that full hunger is only drawn when its actually maxed
		int scaledHunger = MathHelper.floor(realHunger * scale);

		// hunger is always some non-zero value here, so return at least one
		// to make sure we don't draw 0 hunger when we're not actually
		// starving
		return Math.max(scaledHunger, 1);
	}

	public static float onExhaustionAdded(FoodStats foodStats, float deltaExhaustion)
	{
		verifyFoodStats(foodStats, null);

		EntityPlayer player = ((IOrangeCoreFoodStats) foodStats).getPlayer();
		ExhaustionEvent.ExhaustionAddition event = new ExhaustionEvent.ExhaustionAddition(player, deltaExhaustion);
		MinecraftForge.EVENT_BUS.post(event);
		return event.deltaExhaustion;
	}

	public static float getExhaustionCap(FoodStats foodStats)
	{
		verifyFoodStats(foodStats, null);

		EntityPlayer player = ((IOrangeCoreFoodStats) foodStats).getPlayer();
		ExhaustionEvent.GetExhaustionCap event = new ExhaustionEvent.GetExhaustionCap(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.exhaustionLevelCap;
	}

	public static float fireExhaustingActionEvent(EntityPlayer player, ExhaustionEvent.ExhaustingActions source, float deltaExhaustion)
	{
		ExhaustionEvent.ExhaustingAction event = new ExhaustionEvent.ExhaustingAction(player, source, deltaExhaustion);
		MinecraftForge.EVENT_BUS.post(event);
		return event.deltaExhaustion;
	}
}