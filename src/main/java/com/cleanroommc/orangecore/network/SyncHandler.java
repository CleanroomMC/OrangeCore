package com.cleanroommc.orangecore.network;

import com.cleanroommc.orangecore.OrangeCoreMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class SyncHandler
{
	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(OrangeCoreMod.MOD_ID);

	public static void init()
	{
		CHANNEL.registerMessage(MessageDifficultySync.class, MessageDifficultySync.class, 0, Side.CLIENT);
		CHANNEL.registerMessage(new MessageNutritionStatsUpdate.Handler(), MessageNutritionStatsUpdate.class, 1, Side.CLIENT);



		MinecraftForge.EVENT_BUS.register(new SyncHandler());
	}

	/*
	 * Sync difficulty (vanilla MC does not sync it on servers)
	 */
	private EnumDifficulty lastDifficultySetting = null;

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP))
			return;

		CHANNEL.sendTo(new MessageDifficultySync(event.player.world.getDifficulty()), (EntityPlayerMP) event.player);
		CHANNEL.sendTo(new MessageNutritionStatsUpdate(event.player), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
			return;

		if (event.world instanceof WorldServer)
		{
			if (this.lastDifficultySetting != event.world.getDifficulty())
			{
				CHANNEL.sendToAll(new MessageDifficultySync(event.world.getDifficulty()));
				this.lastDifficultySetting = event.world.getDifficulty();
			}
		}
	}
}