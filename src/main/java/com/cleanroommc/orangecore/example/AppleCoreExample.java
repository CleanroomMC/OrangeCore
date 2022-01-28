package com.cleanroommc.orangecore.example;

import com.cleanroommc.orangecore.OrangeCore;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = OrangeCore.MOD_ID + "example", dependencies = "required-after:orangecore")
public class AppleCoreExample
{
	public static final Logger LOG = LogManager.getLogger(OrangeCore.MOD_ID + "example");

	@Instance(OrangeCore.MOD_ID + "example")
	public static AppleCoreExample instance;

	public static Item testFood;
	public static Item testMetadataFood;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		testFood = new ItemNonStandardFood().setRegistryName("testNonStandardFood");
		testFood.setRegistryName(new ResourceLocation(OrangeCore.MOD_ID + "example", "testNonStandardFood"));
		event.getRegistry().register(testFood);

		testMetadataFood = new ItemMetadataFood(new int[]{1, 10}, new float[]{2f, 0.1f}).setRegistryName("testMetadataFood");
		testMetadataFood.setRegistryName(new ResourceLocation(OrangeCore.MOD_ID + "example", "testMetadataFood"));
		event.getRegistry().register(testMetadataFood);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(testFood, 0, new ModelResourceLocation("potato"));
		ModelLoader.setCustomModelResourceLocation(testMetadataFood, 0, new ModelResourceLocation("potato"));
		ModelLoader.setCustomModelResourceLocation(testMetadataFood, 1, new ModelResourceLocation("potato"));
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// only add in the test modifications if we are 'alone' in the dev environment
		boolean otherDependantModsExist = false;
		for (ModContainer mod : Loader.instance().getActiveModList())
		{
			if (mod.getMod() == this)
				continue;

			for (ArtifactVersion dependency : mod.getRequirements())
			{
				if (dependency.getLabel().equals(OrangeCore.MOD_ID))
				{
					otherDependantModsExist = true;
					break;
				}
			}
			if (otherDependantModsExist)
				break;
		}
		if (!otherDependantModsExist)
		{
			MinecraftForge.EVENT_BUS.register(new EatingSpeedModifier());
			MinecraftForge.EVENT_BUS.register(new ExhaustionModifier());
			MinecraftForge.EVENT_BUS.register(new FoodEatenResult());
			MinecraftForge.EVENT_BUS.register(new FoodStatsAdditionCanceler());
			MinecraftForge.EVENT_BUS.register(new FoodValuesModifier());
			MinecraftForge.EVENT_BUS.register(new HealthRegenModifier());
			MinecraftForge.EVENT_BUS.register(new HungerRegenModifier());
			MinecraftForge.EVENT_BUS.register(new StarvationModifier());
			MinecraftForge.EVENT_BUS.register(new MaxHungerModifier());
		}
		if (event.getSide() == Side.CLIENT)
			MinecraftForge.EVENT_BUS.register(new FoodValuesTooltipHandler());
	}
}