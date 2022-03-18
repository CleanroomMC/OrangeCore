package com.cleanroommc.orangecore.asm;

import com.cleanroommc.airlock.api.asm.ASMHelper;

public class ASMConstants
{
	public static final String HOOKS = "com.cleanroommc.orangecore.asm.Hooks";
	public static final String HOOKS_INTERNAL_CLASS = ASMHelper.toInternalClassName(HOOKS);
	public static final String FOOD_VALUES = "com.cleanroommc.orangecore.api.food.FoodValues";
	public static final String IORANGECOREFOODSTATS = "com.cleanroommc.orangecore.asm.util.IOrangeCoreFoodStats";
	public static final String IEDIBLEBLOCK = "com.cleanroommc.orangecore.api.food.IEdibleBlock";
	public static final String IEDIBLE = "com.cleanroommc.orangecore.api.food.IEdible";

	public static final class ExhaustionEvent
	{
		public static final String EXHAUSTED = "com.cleanroommc.orangecore.api.hunger.ExhaustionEvent$Exhausted";
		public static final String EXHAUSTING_ACTIONS = "com.cleanroommc.orangecore.api.hunger.ExhaustionEvent$ExhaustingActions";
	}
	public static final class HealthRegenEvent
	{
		public static final String REGEN = "com.cleanroommc.orangecore.api.hunger.HealthRegenEvent$Regen";
		public static final String PEACEFUL_REGEN = "com.cleanroommc.orangecore.api.hunger.HealthRegenEvent$PeacefulRegen";
	}
	public static final class HungerRegenEvent
	{
		public static final String PEACEFUL_REGEN = "com.cleanroommc.orangecore.api.hunger.HungerRegenEvent$PeacefulRegen";
	}
	public static final class StarvationEvent
	{
		public static final String STARVE = "com.cleanroommc.orangecore.api.hunger.StarvationEvent$Starve";
	}

	//Java
	public static final String RANDOM = "java.util.Random";

	//Minecraft
	public static final String BLOCK = "net.minecraft.block.Block";
	public static final String BLOCK_CONTAINER = "net.minecraft.block.BlockContainer";
	public static final String BLOCK_ICE = "net.minecraft.block.BlockIce";
	public static final String BLOCK_POS = "net.minecraft.util.math.BlockPos";
	public static final String DAMAGE_SOURCE = "net.minecraft.util.DamageSource";
	public static final String ENTITY = "net.minecraft.entity.Entity";
	public static final String ENTITY_LIVING = "net.minecraft.entity.EntityLivingBase";
	public static final String FOOD_STATS = "net.minecraft.util.FoodStats";
	public static final String HAND = "net.minecraft.util.EnumHand";
	public static final String HAND_SIDE = "net.minecraft.util.EnumHandSide";
	public static final String IBLOCKSTATE = "net.minecraft.block.state.IBlockState";
	public static final String IGROWABLE = "net.minecraft.block.IGrowable";
	public static final String ITEM_FOOD = "net.minecraft.item.ItemFood";
	public static final String ITEM_RENDERER = "net.minecraft.client.renderer.ItemRenderer";
	public static final String MINECRAFT = "net.minecraft.client.Minecraft";
	public static final String PLAYER = "net.minecraft.entity.player.EntityPlayer";
	public static final String PLAYER_SP = "net.minecraft.client.entity.EntityPlayerSP";
	public static final String POTION = "net.minecraft.potion.Potion";
	public static final String ITEM_STACK = "net.minecraft.item.ItemStack";
	public static final String TILE_ENTITY = "net.minecraft.tileentity.TileEntity";
	public static final String STAT_BASE = "net.minecraft.stats.StatBase";
	public static final String STAT_LIST = "net.minecraft.stats.StatList";
	public static final String WORLD = "net.minecraft.world.World";

	//FML & Forge
	public static final String GUI_INGAME_FORGE = "net.minecraftforge.client.GuiIngameForge";

	//Blocks
	public static final String CAKE = "net.minecraft.block.BlockCake";
}