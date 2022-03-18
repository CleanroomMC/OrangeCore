package com.cleanroommc.orangecore.api;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Consumer;

public class Nutrient
{
    private final TextFormatting color;
    private final String name;
    private final ResourceLocation texture;
    private Consumer<Integer> updater;

    Nutrient(ResourceLocation name, ResourceLocation texture, TextFormatting color, Consumer<Integer> updater)
    {
        this.name = name.toString();
        this.color = color;
        this.texture = texture;
        this.updater = updater;
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
}