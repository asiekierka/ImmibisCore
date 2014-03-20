package mods.immibis.core.impl.crossmod;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import mods.immibis.core.api.crossmod.ICrossModBC;

public class CrossModBC_Default implements ICrossModBC {
	@Override
	public boolean emitItem(ItemStack stack, TileEntity pipe, TileEntity from) {
		return false;
	}
	
	@Override
	public String getPipeClass(TileEntity te) {
		return null;
	}
	
	@Override
	public Class<?> getWrenchInterface() {
		return null;
	}
}
