package mods.immibis.core.impl.net;

import io.netty.channel.ChannelFutureListener;

import java.util.ArrayList;
import java.util.EnumMap;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import mods.immibis.core.api.net.INetworkingManager;
import mods.immibis.core.api.net.IPacket;

public class NetworkingManager implements INetworkingManager {
	private EnumMap<Side, FMLEmbeddedChannel> channels;
	private ArrayList<Class<? extends IPacket>> packets = new ArrayList<Class<? extends IPacket>>();
    private NetChannelHandler codec;
    
	public NetworkingManager() {
		codec = new NetChannelHandler();
		this.channels = NetworkRegistry.INSTANCE.newChannel("immibis", codec);
	}
	
	public void registerPacket(Class<? extends IPacket> packet) {
		packets.add(packet);
		codec.addDiscriminator(packets.indexOf(packet), packet);
	}
	
	@Override
	public void sendToServer(IPacket packet) {
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeOutbound(packet);
	}

	@Override
	public void sendToClient(IPacket packet, EntityPlayer target) {
		if(target != null) {
			channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
			channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(target);
			channels.get(Side.SERVER).writeOutbound(packet);
		} else {
			channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
			channels.get(Side.SERVER).writeOutbound(packet);
		}
	}

	@Override
	public Packet wrap(IPacket packet) {
		return channels.get(Side.SERVER).generatePacketFrom(packet);
	}

	@Override
	public void sendToClientDimension(IPacket packet, int dimension) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        channels.get(Side.SERVER).writeOutbound(packet);
	}
}
