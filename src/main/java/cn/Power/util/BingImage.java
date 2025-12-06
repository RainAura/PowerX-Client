package cn.Power.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

public class BingImage {
	public static String text, url;

	public final static String URL = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN";

	public String getText() {
		return text;
	}

	public static ResourceLocation getTexture() {
		String url = "https://api.izhao.me/img/";
		MinecraftProfileTexture mpt = new MinecraftProfileTexture(url, new HashMap());
		final ResourceLocation rl = new ResourceLocation("bingWallPaper/" + mpt.getHash());
		IImageBuffer iib = new IImageBuffer() {
			ImageBufferDownload ibd = new ImageBufferDownload();

			public BufferedImage parseUserSkin(BufferedImage var1) {
				return var1;
			}

			public void func_152634_a() {
			}

			@Override
			public void skinAvailable() {
			}
		};
		ThreadDownloadImageData textureArt = new ThreadDownloadImageData((File) null, mpt.getUrl(),
				(ResourceLocation) null, iib);
		Minecraft.getMinecraft().getTextureManager().loadTexture(rl, textureArt);
		return rl;
	}

	public static String getJSON() {

		String jsonData = null;
		try {
			URL url = new URL(URL);
			InputStream is = url.openStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int length;
			byte buff[] = new byte[4096];
			while ((length = is.read(buff)) != -1) {
				baos.write(buff, 0, length);
			}
			jsonData = new String(baos.toByteArray());
			is.close();
			baos.close();
		} catch (Exception e) {
		}
		return jsonData;
	}

}
