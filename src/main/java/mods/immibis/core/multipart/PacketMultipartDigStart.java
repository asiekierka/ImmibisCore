package mods.immibis.core.multipart;


import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.immibis.core.ImmibisCore;
import mods.immibis.core.api.multipart.PartCoordinates;
import mods.immibis.core.api.multipart.util.BlockMultipartBase;
import mods.immibis.core.api.net.IPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;

public class PacketMultipartDigStart implements IPacket {
	
	public int x, y, z, part;
	public boolean isCSPart;

	public PacketMultipartDigStart(PartCoordinates coord) {
		this.x = coord.x;
		this.y = coord.y;
		this.z = coord.z;
		this.part = coord.part;
		this.isCSPart = coord.isCoverSystemPart;
	}
	
	public PacketMultipartDigStart() {
		
	}
	
	@Override
	public void read(ByteBuf in) throws IOException {
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		part = in.readInt();
		isCSPart = in.readBoolean();
	}

	@Override
	public void write(ByteBuf out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
		out.writeInt(part);
		out.writeBoolean(isCSPart);
	}

	@Override
	public void onReceived(EntityPlayer source) {
		if(source != null) {
			//System.out.println("received PMDS: "+source.username+" "+x+" "+y+" "+z+" "+part+" "+isCSPart);
			ImmibisCore.multipartSystem.setBreakingPart(source, new PartCoordinates(x, y, z, part, isCSPart));
		}
	}
}
