package mods.immibis.core.impl.crossmod;

import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import mods.immibis.core.api.traits.IEnergyConsumerTraitUser;
import mods.immibis.core.api.traits.TraitClass;
import mods.immibis.core.api.traits.TraitMethod;

@TraitClass(interfaces={IPowerReceptor.class})
public class EnergyConsumerTraitImpl_BC implements IEnergyConsumerTrait, IPowerReceptor {
	
	private IEnergyConsumerTraitUser tile;
	
	private double UNITS_PER_MJ;
	
	private PowerHandler handler;
	
	public EnergyConsumerTraitImpl_BC(Object tile) {
		try {
			this.tile = (IEnergyConsumerTraitUser)tile;
		} catch(ClassCastException e) {
			throw new RuntimeException("Tile '"+tile+"' must implement IEnergyConsumerTraitUser.", e);
		}
		
		this.UNITS_PER_MJ = EnergyUnit.MJ.getConversionRate(this.tile.EnergyConsumer_getPreferredUnit());
		
		double buffer = this.tile.EnergyConsumer_getPreferredBufferSize() / UNITS_PER_MJ;
		if(buffer < 100) buffer = 100;
		
		handler = new PowerHandler(this, PowerHandler.Type.MACHINE);
		handler.configure(1, (float)Math.min(Math.max(20, buffer / 20), 500), (float)buffer / 2, (float)buffer);
		handler.configurePowerPerdition(1, 5); // 0.2 MJ/tick
	}
	
	@Override
	@TraitMethod
	public void doWork(PowerHandler workProvider) {
		
	}
	
	@Override
	@TraitMethod
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return handler.getPowerReceiver();
	}
	
	@Override
	@TraitMethod
	public net.minecraft.world.World getWorld() {
		return ((TileEntity)tile).getWorldObj();
	}


	@Override
	public void readFromNBT(NBTTagCompound tag) {
		handler.readFromNBT(tag, "ECTI_BC");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		handler.writeToNBT(tag, "ECTI_BC");
	}



	@Override
	public double getStoredEnergy() {
		return handler.getEnergyStored() * UNITS_PER_MJ;
	}


	@Override
	public double useEnergy(double min, double max) {
		return handler.useEnergy((float)(min / UNITS_PER_MJ), (float)(max / UNITS_PER_MJ), true) * UNITS_PER_MJ;
	}


	@Override
	public void onValidate() {
		
	}


	@Override
	public void onChunkUnload() {
		
	}


	@Override
	public void onInvalidate() {
		
	}
	
	@Override
	public void setStoredEnergy(double amt) {
		handler.setEnergy((float)(amt / UNITS_PER_MJ));
	}
}
