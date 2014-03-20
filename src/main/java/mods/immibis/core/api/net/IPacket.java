package mods.immibis.core.api.net;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;

public interface IPacket {
	/**
	 * Reads the packet's contents from the given stream.
	 * Must be able to be safely called on any thread.
	 */
	public void read(ByteBuf in) throws IOException;
	
	/**
	 * Writes the packet's contents to the given stream.
	 * Must be able to be safely called on any thread.
	 */
	public void write(ByteBuf out) throws IOException;
	
	/** Source is the player who sent the packet, always null on the client */
	public void onReceived(EntityPlayer source);
}
