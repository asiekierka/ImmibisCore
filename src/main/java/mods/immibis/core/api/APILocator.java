package mods.immibis.core.api;

import mods.immibis.core.api.crossmod.ICrossModBC;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import mods.immibis.core.api.multipart.IMultipartSystem;
import mods.immibis.core.api.net.INetworkingManager;

/**
 * Contains methods to get instances of various API interfaces.
 * 
 * Mods using any APIs should specify "after:ImmibisCore" in their dependency list, so that
 * if the mod includes an outdated API then the newer one will take precedence.
 */
public final class APILocator {
	
	private static class Ref<T> {public T v; public boolean initialized;}
	
	@SuppressWarnings("unchecked")
	private static <T> T getField(String name, Ref<T> ref) {
		if(ref.initialized)
			return ref.v;
		try {
			ref.v = (T)Class.forName("mods.immibis.core.ImmibisCore").getField(name).get(null);
		} catch(ClassNotFoundException e) {
			ref.v = null;
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {
			throw (AssertionError)new AssertionError("Should not happen").initCause(e);
		}
		ref.initialized = true;
		return ref.v;
	}

	private static Ref<ICrossModIC2> r_ic2 = new Ref<ICrossModIC2>();
	private static Ref<ICrossModBC> r_bc = new Ref<ICrossModBC>();
	private static Ref<IMultipartSystem> r_multipart_system = new Ref<IMultipartSystem>();
	private static Ref<INetworkingManager> r_networking_manager = new Ref<INetworkingManager>();
	
	public static INetworkingManager getNetManager() {
		return getField("networkingManager", r_networking_manager);
	}
	
	/**
	 * Returns the cross-mod interface for IC2.
	 */
	public static ICrossModIC2 getCrossModIC2() {
		return getField("crossModIC2", r_ic2);
	}

	/**
	 * Returns the cross-mod interface for Buildcraft.
	 */
	public static ICrossModBC getCrossModBC() {
		return getField("crossModBC", r_bc);
	}

	/**
	 * Returns the (undocumented) multipart system interface.
	 */
	public static IMultipartSystem getMultipartSystem() {
		return getField("multipartSystem", r_multipart_system);
	}
}
