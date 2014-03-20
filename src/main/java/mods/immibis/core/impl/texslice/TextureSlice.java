package mods.immibis.core.impl.texslice;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

public class TextureSlice extends TextureAtlasSprite {

	// name format is filepath!subID
	
	public TextureSlice(String name) {
		super(name);
		if(!name.contains("!"))
			throw new AssertionError("Name must contain ! (name was "+name+")");
	}
	
	public static class TextureStitchListener {
		static TextureStitchListener instance = new TextureStitchListener();
		private TextureStitchListener() {}
		
		@SubscribeEvent
		public void onTextureStitch(TextureStitchEvent.Post evt) {
			sheetCache.clear();
		}
	}
	static {
		MinecraftForge.EVENT_BUS.register(TextureStitchListener.instance);
		Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer.registerMetadataSectionType(new SpriteMetadataSectionSerializer(), SpriteMetadataSection.class);
	}
	
	// sheet path -> cache for that sheet
	private static HashMap<String, SheetCache> sheetCache = new HashMap<String, SheetCache>();
	
	private static class SheetCache {
		SheetCache(IResourceManager manager, ResourceLocation location) throws IOException {
			
			@SuppressWarnings("unchecked")
			List<IResource> resources = manager.getAllResources(location);
			
			for(IResource res : resources) {
				BufferedImage image = ImageIO.read(res.getInputStream());
				
				SpriteMetadataSection md = (SpriteMetadataSection)res.getMetadata(SpriteMetadataSection.SECTION_NAME);
				
				if(md == null)
					throw new FileNotFoundException("No "+SpriteMetadataSection.SECTION_NAME+" section in "+location);
				
				int sheetWidth = image.getWidth() / md.spriteSize;
				int sheetHeight = image.getHeight() / md.spriteSize;
				
				for(Map.Entry<String, SpritePosition> e : md.spritePos.entrySet()) {
					int x = e.getValue().getX(sheetWidth, sheetHeight) * md.spriteSize;
					int y = e.getValue().getY(sheetWidth, sheetHeight) * md.spriteSize;
					
					int[] rgb = new int[md.spriteSize * md.spriteSize];
					image.getRGB(x, y, md.spriteSize, md.spriteSize, rgb, 0, md.spriteSize);
					
					sprites.put(e.getKey(), new Sprite(md.spriteSize, rgb));
				}
			}
			
		}
		
		private class Sprite {
			final int size;
			final int[] data;
			Sprite(int size, int[] data) {
				this.size = size;
				this.data = data;
			}
		}
		
		Map<String, Sprite> sprites = new HashMap<String, Sprite>();
		
		public boolean hasSprite(String subName) {
			return sprites.containsKey(subName);
		}
		
		public int[] getSpriteRGB(String subName) {
			return sprites.get(subName).data;
		}
		
		public int getSpriteWidth(String subName) {
			return sprites.get(subName).size;
		}
		public int getSpriteHeight(String subName) {
			return sprites.get(subName).size;
		}
	}
	
	@Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
    {
        return true;
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean load(IResourceManager manager, ResourceLocation location) {
		String domain = location.getResourceDomain();
		String path = location.getResourcePath();
		
		String ext = path.substring(path.lastIndexOf('.') + 1);
		String basePath = "textures/blocks/" + path.substring(0, path.lastIndexOf('!'));
		
		String subName = path.substring(path.lastIndexOf('!') + 1);
		
		SheetCache sc;
		
		try {
			synchronized(sheetCache) {
				sc = sheetCache.get(domain+":"+basePath);
				if(sc == null)
					sheetCache.put(domain+":"+basePath, sc = new SheetCache(manager, new ResourceLocation(domain, basePath + ".png")));
			}
			
			if(!sc.hasSprite(subName))
				throw new FileNotFoundException("resource "+location.toString()+" or sprite "+subName+" in "+domain+":"+basePath+".png");
			
			this.height = sc.getSpriteHeight(subName);
			this.width = sc.getSpriteWidth(subName);
			int[][] data = new int[5][];
			data[0] = sc.getSpriteRGB(subName);
			this.framesTextureData.add(data);
			
			return false;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
