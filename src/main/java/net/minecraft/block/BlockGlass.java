package net.minecraft.block;

import java.util.Random;

import cn.Power.util.Helper;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumWorldBlockLayer;

public class BlockGlass extends BlockBreakable {
	public BlockGlass(Material materialIn, boolean ignoreSimilarity) {
		super(materialIn, ignoreSimilarity);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random) {
		return 0;
	}

	public EnumWorldBlockLayer getBlockLayer() {
		return Helper.xray ? super.getBlockLayer() : EnumWorldBlockLayer.CUTOUT_MIPPED;
	}

	public boolean isFullCube() {
		return false;
	}

	protected boolean canSilkHarvest() {
		return true;
	}
}
