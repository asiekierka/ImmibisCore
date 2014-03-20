package mods.immibis.core.api.porting;

import io.netty.channel.ChannelHandlerContext;

import java.io.File;

import mods.immibis.core.api.net.IPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class SidedProxy {
	public static SidedProxy instance;
	
	public abstract File getMinecraftDir();
	public abstract double getPlayerReach(EntityPlayer ply);
	public abstract EntityPlayer getThePlayer();
	public abstract int getUniqueBlockModelID(String renderClass, boolean b);
	public abstract boolean isOp(String player);
	public abstract void registerTileEntity(Class<? extends TileEntity> clazz, String id, String rclass);
	public abstract boolean isWorldCurrent(World w);
	public abstract void registerItemRenderer(Item item, String renderClassName);
	public abstract void registerEntityRenderer(Class<? extends Entity> entClass, String renderClassName);
	
	public abstract boolean isDedicatedServer();
	
	public abstract Object createSidedObject(String clientClass, String serverClass);
	public abstract void handlePacket(ChannelHandlerContext ctx, IPacket packet);
}
