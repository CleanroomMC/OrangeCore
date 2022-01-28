package com.cleanroommc.orangecore.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDifficultySync implements IMessage, IMessageHandler<MessageDifficultySync, IMessage>
{
	EnumDifficulty difficulty;

	public MessageDifficultySync()
	{
	}

	public MessageDifficultySync(EnumDifficulty difficulty)
	{
		this.difficulty = difficulty;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(difficulty.getId());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		difficulty = EnumDifficulty.byId(buf.readByte());
	}

	@Override
	public IMessage onMessage(final MessageDifficultySync message, final MessageContext ctx)
	{
		// defer to the next game loop; we can't guarantee that Minecraft.thePlayer is initialized yet
		Minecraft.getMinecraft().addScheduledTask(() -> NetworkHelper.getSidedPlayer(ctx).world.getWorldInfo().setDifficulty(message.difficulty));
		return null;
	}
}