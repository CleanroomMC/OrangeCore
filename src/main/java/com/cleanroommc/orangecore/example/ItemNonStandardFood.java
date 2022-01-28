package com.cleanroommc.orangecore.example;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import com.cleanroommc.orangecore.api.food.FoodValues;
import com.cleanroommc.orangecore.api.food.IEdible;
import com.cleanroommc.orangecore.api.food.ItemFoodProxy;

import javax.annotation.Nonnull;

@Optional.Interface(iface = "com.cleanroom.orangecore.api.food.IEdible", modid = "applecore")
public class ItemNonStandardFood extends Item implements IEdible
{
	public ItemNonStandardFood()
	{
		this.setCreativeTab(CreativeTabs.FOOD);
	}

	@Optional.Method(modid = "applecore")
	@Override
	public FoodValues getFoodValues(@Nonnull ItemStack itemStack)
	{
		return new FoodValues(1, 1f);
	}

	// This needs to be abstracted into an Optional method,
	// otherwise the ItemFoodProxy reference will cause problems
	@Optional.Method(modid = "applecore")
	public void onEatenCompatibility(@Nonnull ItemStack itemStack, EntityPlayer player)
	{
		// one possible compatible method
		player.getFoodStats().addStats(new ItemFoodProxy(this), itemStack);

		// another possible compatible method:
		// new ItemFoodProxy(this).onEaten(itemStack, player);
	}

	@Override
	@Nonnull
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entityLiving) {
		stack.shrink(1);

		if (entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entityLiving;

			if (Loader.isModLoaded("applecore"))
			{
				onEatenCompatibility(stack, player);
			}
			else
			{
				// this method is not compatible with AppleCore
				player.getFoodStats().addStats(1, 1F);
			}

			player.addStat(StatList.getObjectUseStats(this));
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}

		return stack;
	}

	@Override
	@Nonnull
	public EnumAction getItemUseAction(@Nonnull ItemStack itemStack)
	{
		return EnumAction.EAT;
	}

	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack itemStack)
	{
		return 32;
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
        ItemStack stack = player.getHeldItem(hand);
		if (player.canEat(true))
		{
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}
}