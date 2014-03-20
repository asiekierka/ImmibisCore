package mods.immibis.core.porting;

import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.porting.SidedProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public abstract class CommonProxy142 extends SidedProxy {
	@Override
	public boolean isWorldCurrent(World w) {
		return DimensionManager.getWorld(w.provider.dimensionId) == w;
	}

	public void handlePacket(ChannelHandlerContext ctx, IPacket packet) {
		INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
		packet.onReceived(((NetHandlerPlayServer) netHandler).playerEntity);
	}
}
