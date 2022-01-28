package com.cleanroom.orangecore.api.hunger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import com.cleanroom.orangecore.api.OrangeCoreAPI;

/**
 * Base class for all ExhaustionEvent events.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class ExhaustionEvent extends Event
{
	public final EntityPlayer player;

	public ExhaustionEvent(EntityPlayer player)
	{
		this.player = player;
	}

	/**
	 * Fired each FoodStats update to determine whether or not exhaustion is allowed for the {@link #player}.
	 * 
	 * This event is fired in {@link FoodStats#onUpdate}.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event uses the {@link Result}. {@link HasResult}<br>
	 * {@link Result#DEFAULT} will use the vanilla conditionals.<br>
	 * {@link Result#ALLOW} will allow exhaustion without condition.<br>
	 * {@link Result#DENY} will deny exhaustion without condition.<br>
	 */
	@HasResult
	public static class AllowExhaustion extends ExhaustionEvent
	{
		public AllowExhaustion(EntityPlayer player)
		{
			super(player);
		}
	}

	/**
	 * Fired each time exhaustion is added to a {@link #player} to allow control over
	 * its value.
	 *
	 * Note: This is a catch-all event for *any* time exhaustion is modified. For more fine-grained
	 * control, see {@link ExhaustingAction}
	 *
	 * This event is fired in {@link FoodStats#addExhaustion}.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class ExhaustionAddition extends ExhaustionEvent
	{
		public float deltaExhaustion;

		public ExhaustionAddition(EntityPlayer player, float deltaExhaustion)
		{
			super(player);
			this.deltaExhaustion = deltaExhaustion;
		}
	}

	/**
	 * See {@link ExhaustingAction}
	 */
	public enum ExhaustingActions
	{
		HARVEST_BLOCK,
		NORMAL_JUMP,
		SPRINTING_JUMP,
		ATTACK_ENTITY,
		DAMAGE_TAKEN,
		HUNGER_POTION,
		MOVEMENT_DIVE,
		MOVEMENT_SWIM,
		MOVEMENT_SPRINT,
		MOVEMENT_CROUCH,
		MOVEMENT_WALK
	}

	/**
	 * Fired each time a {@link #player} does something that changes exhaustion in vanilla Minecraft
	 * (i.e. jumping, sprinting, etc; see {@link ExhaustingActions} for the full list of possible sources)
	 *
	 * This event is fired whenever {@link EntityPlayer#addExhaustion} is called from within Minecraft code.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class ExhaustingAction extends ExhaustionEvent
	{
		public final ExhaustingActions source;
		public float deltaExhaustion;

		public ExhaustingAction(EntityPlayer player, ExhaustingActions source, float deltaExhaustion)
		{
			super(player);
			this.source = source;
			this.deltaExhaustion = deltaExhaustion;
		}
	}

	/**
	 * Fired every time max exhaustion level is retrieved to allow control over its value.
	 * 
	 * This event is fired in {@link FoodStats#onUpdate} and in {@link OrangeCoreAPI}.<br>
	 * <br>
	 * {@link #maxExhaustionLevel} contains the exhaustion level that will trigger a hunger/saturation decrement.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class GetMaxExhaustion extends ExhaustionEvent
	{
		public float maxExhaustionLevel = 4f;

		public GetMaxExhaustion(EntityPlayer player)
		{
			super(player);
		}
	}

	/**
	 * Fired once exhaustionLevel exceeds maxExhaustionLevel (see {@link GetMaxExhaustion}),
	 * in order to control how exhaustion affects hunger/saturation.
	 * 
	 * This event is fired in {@link FoodStats#onUpdate}.<br>
	 * <br>
	 * {@link #currentExhaustionLevel} contains the exhaustion level of the {@link #player}.<br>
	 * {@link #deltaExhaustion} contains the delta to be applied to exhaustion level (default: {@link GetMaxExhaustion#maxExhaustionLevel}).<br>
	 * {@link #deltaHunger} contains the delta to be applied to hunger.<br>
	 * {@link #deltaSaturation} contains the delta to be applied to saturation.<br>
	 * <br>
	 * Note: {@link #deltaHunger} and {@link #deltaSaturation} will vary depending on their vanilla conditionals.
	 * For example, deltaHunger will be 0 when this event is fired in Peaceful difficulty.<br> 
	 * <br>
	 * This event is {@link Cancelable}.<br>
	 * If this event is canceled, it will skip applying the delta values to hunger and saturation.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	@Cancelable
	public static class Exhausted extends ExhaustionEvent
	{
		public final float currentExhaustionLevel;
		public float deltaExhaustion;
		public int deltaHunger = -1;
		public float deltaSaturation = -1f;

		public Exhausted(EntityPlayer player, float exhaustionToRemove, float currentExhaustionLevel)
		{
			super(player);
			this.deltaExhaustion = -exhaustionToRemove;
			this.currentExhaustionLevel = currentExhaustionLevel;

			boolean shouldDecreaseSaturationLevel = player.getFoodStats().getSaturationLevel() > 0f;

			if (!shouldDecreaseSaturationLevel)
				deltaSaturation = 0f;

			EnumDifficulty difficulty = player.world.getDifficulty();
			boolean shouldDecreaseFoodLevel = !shouldDecreaseSaturationLevel && difficulty != EnumDifficulty.PEACEFUL;

			if (!shouldDecreaseFoodLevel)
				deltaHunger = 0;
		}
	}

	/**
	 * Fired every time the exhaustion level is capped to allow control over the cap.
	 *
	 * This event is fired in {@link FoodStats#addExhaustion}.<br>
	 * <br>
	 * {@link #exhaustionLevelCap} contains the exhaustion level that will be used to cap the exhaustion level.<br>
	 * <br>
	 * This event is not {@link Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 */
	public static class GetExhaustionCap extends ExhaustionEvent
	{
		public float exhaustionLevelCap = 40f;

		public GetExhaustionCap(EntityPlayer player)
		{
			super(player);
		}
	}
}