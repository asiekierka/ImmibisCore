package mods.immibis.core.impl.crossmod;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import mods.immibis.core.api.crossmod.ICrossModBC;

public class CrossModBC_Impl_NoTransport implements ICrossModBC {
	@Override
	public Class<?> getWrenchInterface() {
		return IToolWrench.class;
	}
	
	@Override
	public boolean emitItem(ItemStack stack, TileEntity pipe, TileEntity from) {
		return false;
	}
	
	@Override
	public String getPipeClass(TileEntity te) {
		return null;
	}
}
