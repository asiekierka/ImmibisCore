package mods.immibis.cobaltite;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mods.immibis.core.api.net.IPacket;

/**
 * Annotate an integral constant containing a packet ID with this to automatically create
 * packets of that type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PacketType {
	public Class<? extends IPacket> type();
	public Direction direction();
	
	public enum Direction {
		C2S, S2C, BOTH
	}
}
