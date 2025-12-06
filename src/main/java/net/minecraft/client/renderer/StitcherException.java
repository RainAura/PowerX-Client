package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.Stitcher;

public class StitcherException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5172525435253766586L;
	
	
	public StitcherException(Stitcher.Holder p_i2344_1_, String p_i2344_2_) {
		super(p_i2344_2_);
	}
}
