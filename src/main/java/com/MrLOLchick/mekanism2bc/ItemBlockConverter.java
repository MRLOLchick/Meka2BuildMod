package com.MrLOLchick.mekanism2bc;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockConverter extends ItemBlock {

    public ItemBlockConverter(Block block) {
        super(block);
        setRegistryName(block.getRegistryName());
    }
}