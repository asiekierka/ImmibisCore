package mods.immibis.core.api.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public interface INetworkingManager {
	public void sendToServer(IPacket packet);
	public void sendToClient(IPacket packet, EntityPlayer target);
	public void sendToClientDimension(IPacket packet, int dimension);
	public Packet wrap(IPacket packet);
	public void registerPacket(Class<? extends IPacket> packet);
}
