package com.cleanroommc.orangecore.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Nutrient
{
    private final TextFormatting color;
    private final String name;
    private final ResourceLocation texture;
    private final BiFunction<Float, EntityPlayer, Float> updater;

    public static final Map<String, Nutrient> NUTRIENTS = new HashMap<>();
    private boolean enabled = true;

    /**
     * Creates a new nutrient that can be applied to a player.
     * @param name The name of the nutrient (done as a ResourceLocation to prevent overlap)
     * @param texture The texture of said nutrient when displayed
     * @param color Color for displaying on food items
     * @param updater Takes a current quantity of a nutrient and the player with that quantity, which may be used in any way desired, as long as it outputs the next value of said nutrient.
     */
    public Nutrient(ResourceLocation name, ResourceLocation texture, TextFormatting color, BiFunction<Float, EntityPlayer, Float> updater)
    {
        this.name = name.toString();
        this.color = color;
        this.texture = texture;
        this.updater = updater;
        NUTRIENTS.put(this.name, this);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Nonnull
    public TextFormatting getColor()
    {
        return color;
    }

    public float update(float value, EntityPlayer player) {
        return this.updater.apply(value, player);
    }

    public void enable(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     *
     * @param decreasePerTick
     * @param effect
     * @return A function which applies the specified effect to the player when the player has less than 20 of said nutrient.
     */
    public static BiFunction<Float, EntityPlayer, Float> basicNutrientFormulaWithSideEffect(float decreasePerTick, Potion effect) {
        PotionEffect modifiedEffect = new PotionEffect(effect, 1);
        modifiedEffect.setCurativeItems(Collections.EMPTY_LIST);
        return (value, player) -> {
            if (value < -20)
                player.addPotionEffect(modifiedEffect);
            return value - decreasePerTick;
        };
    }
}