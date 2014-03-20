package mods.immibis.core.net;


import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import mods.immibis.core.ImmibisCore;

public class PacketButtonPress extends AbstractContainerSyncPacket {
	
	public int buttonID;
	
	public PacketButtonPress() {
	}
	
	public PacketButtonPress(int button) {
		buttonID = button;
	}
	
	@Override
	public void read(ByteBuf in) throws IOException {
		buttonID = in.readInt();
	}

	@Override
	public void write(ByteBuf out) throws IOException {
		out.writeInt(buttonID);
	}
}
