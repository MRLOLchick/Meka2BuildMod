package com.MrLOLchick.mekanism2bc;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = MekanismToBC.MODID,
        name = MekanismToBC.NAME,
        version = MekanismToBC.VERSION,
        dependencies = "required-after:forge@[14.23.5.2847,);" +
                "required-after:mekanism;" +
                "required-after:buildcraftcore;" +
                "after:hbm")
public class MekanismToBC {

    public static final String MODID = "mekanismtobc";
    public static final String NAME = "Mekanism to BuildCraft Converter & NTM Extended";
    public static final String VERSION = "1.0";
    public static Logger logger;

    public static Block converterBlock;
    public static ItemBlock converterItemBlock;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        // Создаём блок и предмет
        converterBlock = new BlockConverter();
        converterItemBlock = new ItemBlockConverter(converterBlock);

        // Регистрируем TileEntity
        GameRegistry.registerTileEntity(TileEntityConverter.class, MODID + ":converter");

        logger.info("Mekanism to BC Converter mod loaded!");
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(converterBlock);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(converterItemBlock);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                converterItemBlock, 0,
                new ModelResourceLocation(converterBlock.getRegistryName(), "inventory")
        );
    }
}