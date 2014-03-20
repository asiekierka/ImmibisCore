package mods.immibis.core.impl.net;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import mods.immibis.core.api.net.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class NetChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket> {
    public NetChannelHandler() {
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, IPacket packet, ByteBuf data) throws Exception {
        packet.write(data);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, IPacket packet) {
    	try {
	        packet.read(data);
	        switch (FMLCommonHandler.instance().getEffectiveSide()) {
		        case CLIENT:
		            packet.onReceived(Minecraft.getMinecraft().thePlayer);
		            break;
		        case SERVER:
		            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
		            packet.onReceived(((NetHandlerPlayServer) netHandler).playerEntity);
		            break;
		    }
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
    }
}