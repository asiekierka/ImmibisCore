package mods.immibis.core.api.porting;

import java.util.EnumSet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public abstract class PortableBaseMod {
	private class TickEventServerHandler {
		@SubscribeEvent
		public void tickEventServer(TickEvent.ServerTickEvent event) {
			if(event.phase != Phase.START) return;
			onTickInGame();
		}
	}
	
	private class TickEventClientHandler {
		@SubscribeEvent
		public void tickEventClient(TickEvent.ClientTickEvent event) {
			if(event.phase != Phase.START) return;
			onTickInGame();
		}
	}
	
    public boolean onTickInGame() {return false;}

    public void enableClockTicks(final boolean server) {
    	FMLCommonHandler.instance().bus().register(server
    			? new TickEventServerHandler()
    			: new TickEventClientHandler());
    }
}
