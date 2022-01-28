package com.cleanroom.orangecore.api_impl;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import com.cleanroom.orangecore.api.OrangeCoreAPI;
import com.cleanroom.orangecore.api.IOrangeCoreRegistry;

import java.util.HashMap;
import java.util.Map;

public enum OrangeCoreRegistryImpl implements IOrangeCoreRegistry
{
	INSTANCE;

	private Map<Block, Item> edibleBlockToItem = new HashMap<Block, Item>();
	private Map<Item, Block> edibleItemToBlock = new HashMap<Item, Block>();

	private OrangeCoreRegistryImpl()
	{
		OrangeCoreAPI.registry = this;
	}

	@GameRegistry.ObjectHolder("minecraft:cake")
	public static final Block CAKE_BLOCK = null;

	@GameRegistry.ObjectHolder("minecraft:cake")
	public static final Item CAKE_ITEM = null;

	public void init()
	{
		registerEdibleBlock(CAKE_BLOCK, CAKE_ITEM);
	}

	@Override
	public void registerEdibleBlock(Block block, Item item)
	{
		edibleBlockToItem.put(block, item);
		edibleItemToBlock.put(item, block);
	}

	@Override
	public Item getItemFromEdibleBlock(Block block)
	{
		Item item = edibleBlockToItem.get(block);
		if (item == null) item = Item.getItemFromBlock(block);
		return item;
	}

	@Override
	public Block getEdibleBlockFromItem(Item item)
	{
		Block block = edibleItemToBlock.get(item);
		if (block == null) block = Block.getBlockFromItem(item);
		return block != Blocks.AIR ? block : null;
	}
}
