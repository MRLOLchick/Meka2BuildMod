package com.MrLOLchick.mekanism2bc;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockConverter extends Block implements ITileEntityProvider {

    public BlockConverter() {
        super(Material.IRON);
        setRegistryName(MekanismToBC.MODID, "converter");
        setTranslationKey(MekanismToBC.MODID + ".converter");
        setHardness(3.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityConverter();
    }
}