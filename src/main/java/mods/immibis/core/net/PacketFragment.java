package mods.immibis.core.net;


import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.ByteBufUtils;
import mods.immibis.core.ImmibisCore;
import mods.immibis.core.api.net.IPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;

public class PacketFragment implements IPacket {
	
	public int senderSeqID;
	public int fragmentIndex;
	public int numFragments;
	public byte[] data;
	public String channel;


	@Override
	public void read(ByteBuf in) throws IOException {
		senderSeqID = in.readShort();
		fragmentIndex = in.readShort();
		numFragments = in.readShort();
		channel = ByteBufUtils.readUTF8String(in);
		
		int len = in.readShort();
		if(len < 0 || len > 32767)
			throw new IOException("invalid data length in packet fragment");
		data = new byte[len];
		in.readBytes(data);
		//if(in.read < len)
		//	throw new IOException("truncated data in packet fragment");
	}

	@Override
	public void write(ByteBuf out) throws IOException {
		out.writeShort(senderSeqID);
		out.writeShort(fragmentIndex);
		out.writeShort(numFragments);
		ByteBufUtils.writeUTF8String(out, channel);
		out.writeShort(data.length);
		out.writeBytes(data);
	}

	@Override
	public void onReceived(EntityPlayer source) {
		FragmentSequence.add(source, this);
	}
}
