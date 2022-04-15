package com.cleanroommc.orangecore.network;

import com.cleanroommc.orangecore.api.capability.INutritionStats;
import com.cleanroommc.orangecore.api.capability.NutritionCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNutritionStatsUpdate implements IMessage {
    private NBTTagCompound nutrients;

    @SuppressWarnings("unused")
    @Deprecated
    public MessageNutritionStatsUpdate() {
    }

    public MessageNutritionStatsUpdate(Entity player) {
        this.nutrients = player.getCapability(NutritionCapability.CAPABILITY, null).serializeNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nutrients = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nutrients);
    }

    public static final class Handler implements IMessageHandler<MessageNutritionStatsUpdate, IMessage> {
        @Override
        public IMessage onMessage(MessageNutritionStatsUpdate message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = NetworkHelper.getSidedPlayer(ctx);
                if (player != null) {
                    INutritionStats nutritionHandler = player.getCapability(NutritionCapability.CAPABILITY, null);
                    if (nutritionHandler != null) {
                        nutritionHandler.deserializeNBT(message.nutrients);
                    }
                }
            });
            return null;
        }
    }

}
