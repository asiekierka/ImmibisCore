package mods.immibis.core;

import java.util.logging.Logger;

import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.crossmod.ICrossModBC;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import mods.immibis.core.api.traits.IInventoryTrait;
import mods.immibis.core.api.traits.ITrait;
import mods.immibis.core.commands.TPSCommand;
import mods.immibis.core.impl.*;
import mods.immibis.core.impl.crossmod.*;
import mods.immibis.core.impl.net.NetworkingManager;
import mods.immibis.core.multipart.MultipartSystem;
import mods.immibis.core.multipart.PacketMultipartDigFinish;
import mods.immibis.core.multipart.PacketMultipartDigStart;
import mods.immibis.core.net.FragmentSequence;
import mods.immibis.core.net.PacketButtonPress;
import mods.immibis.core.net.PacketFragment;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class ImmibisCore {
	
	public static final String VERSION = "58.0.1";
	public static final String MODID = "ImmibisCore";
	public static final String NAME = "Immibis Core";

	// 0 unused
	// 1 unused
	public static final int PACKET_TYPE_C2S_MULTIPART_DIG_START = 2;
	// 3 unused
	// 4 unused
	public static final int PACKET_TYPE_FRAGMENT = 5;
	public static final int PACKET_TYPE_C2S_BUTTON_PRESS = 6;
	public static final int PACKET_TYPE_C2S_MULTIPART_DIG_FINISH = 7;
	


	public static final String CHANNEL = "ImmibisCore";

	public static NetworkingManager networkingManager;
	public static ICrossModIC2 crossModIC2;
	public static ICrossModBC crossModBC;
	public static MultipartSystem multipartSystem = new MultipartSystem();
	
	public static String tpsCommandName;
	
	public static FMLRelaunchLog LOGGER;
	static {
		/*LOGGER = Logger.getLogger(MODID);*/
	}
	
	public static java.util.Timer TIMER = new java.util.Timer("Immibis Core background task", true);

	public void preInit(FMLPreInitializationEvent evt) {
		
		{
			String preferredEnergySystem = Config.getString("preferredEnergySystem",
				Loader.isModLoaded("IC2") ? "ic2" :
				Loader.isModLoaded("BuildCraft|Energy") ? "minecraftJoules" :
				Loader.isModLoaded("ThermalExpansion") ? "redstoneFlux" :
				"infinite",
				Configuration.CATEGORY_GENERAL,
				"Which power system should be used. Possible values are: ic2 (IndustrialCraft 2's energy network), minecraftJoules (Buildcraft's power system, also used by other mods), redstoneFlux (Thermal Expansion's power system, also used by other mods), infinite (power is free)");
			System.out.println("[Immibis Core] Preferred energy system set to: " + preferredEnergySystem);
			if(preferredEnergySystem.equalsIgnoreCase("ic2"))
				ITrait.knownInterfaces.put(IEnergyConsumerTrait.class, EnergyConsumerTraitImpl_IC2.class);
			else if(preferredEnergySystem.equalsIgnoreCase("minecraftJoules"))
				ITrait.knownInterfaces.put(IEnergyConsumerTrait.class, EnergyConsumerTraitImpl_BC.class);
			else if(preferredEnergySystem.equalsIgnoreCase("redstoneFlux"))
				ITrait.knownInterfaces.put(IEnergyConsumerTrait.class, EnergyConsumerTraitImpl_RF.class);
			else if(preferredEnergySystem.equalsIgnoreCase("infinite"))
				ITrait.knownInterfaces.put(IEnergyConsumerTrait.class, EnergyConsumerTraitImpl_Infinite.class);
			else
				throw new RuntimeException("Invalid preferred energy system selected: "+preferredEnergySystem+". Options are: ic2, minecraftJoules, redstoneFlux, infinite. Not case sensitive.");
		}
		
		//APILocator.getNetManager().listen(this);
		networkingManager = new NetworkingManager();
		networkingManager.registerPacket(PacketButtonPress.class);
		networkingManager.registerPacket(PacketFragment.class);
		networkingManager.registerPacket(PacketMultipartDigStart.class);
		networkingManager.registerPacket(PacketMultipartDigFinish.class);
		
		FragmentSequence.init();
		MainThreadTaskQueue.init();
		
		Config.getString("core.mictransformer.ignoredClasses", "", Configuration.CATEGORY_GENERAL, "advanced setting: comma-separated list of classes to ignore when generating dynamic inheritance chains");
		
		tpsCommandName = Config.getString("core.command.tps.name", "tps", Configuration.CATEGORY_GENERAL, "name of TPS command, without the slash. leave blank to disable.");
		
		if(Loader.isModLoaded("IC2") && !Config.getBoolean("core.ignoreIC2", false))
			crossModIC2 = new CrossModIC2_Impl();
		else
			crossModIC2 = new CrossModIC2_Default();
		
		if(Loader.isModLoaded("BuildCraft|Core") && !Config.getBoolean("core.ignoreBuildcraftCore", false))
			if(Loader.isModLoaded("BuildCraft|Transport") && !Config.getBoolean("core.ignoreBuildcraftTransport", false))
				crossModBC = new CrossModBC_Impl();
			else
				crossModBC = new CrossModBC_Impl_NoTransport();
		else
			crossModBC = new CrossModBC_Default();
	}

	public void init(FMLInitializationEvent evt) {
		
		multipartSystem.init();
	}

	public void postInit(FMLPostInitializationEvent evt) {
		
	}
	
	public void serverStarting(FMLServerStartingEvent evt) {
		if(!tpsCommandName.equals(""))
			evt.registerServerCommand(new TPSCommand(tpsCommandName));
	}

	public static ImmibisCore instance;

	public ImmibisCore() {
		instance = this;
	}



	public static boolean areItemsEqual(ItemStack a, ItemStack b) {
		if(a == null && b == null)
			return true;
		if(a == null || b == null)
			return false;
		if(!a.getItem().equals(b.getItem()))
			return false;
		if(a.getHasSubtypes() && a.getItemDamage() != b.getItemDamage())
			return false;
		if(a.stackTagCompound == null && b.stackTagCompound == null)
			return true;
		if(a.stackTagCompound == null || b.stackTagCompound == null)
			return false;
		return a.stackTagCompound.equals(b.stackTagCompound);
	}

	static {
		((LaunchClassLoader)ImmibisCore.class.getClassLoader()).registerTransformer(MultiInterfaceClassTransformer.class.getName());
		((LaunchClassLoader)ImmibisCore.class.getClassLoader()).registerTransformer(TraitTransformer.class.getName());
		
		ITrait.knownInterfaces.put(IInventoryTrait.class, InventoryTraitImpl.class);
	}

	/*private static ILogAgent getClientLogAgent() {
		return Minecraft.getMinecraft().getLogAgent();
	}
	public static ILogAgent getLogAgent() {
		MinecraftServer sv = MinecraftServer.getServer();
		if(sv == null)
			return getClientLogAgent();
		return sv.getLogAgent();
	}*/
}
