package net.minecraft.client.gui;

import java.awt.Color;

import cn.Power.Client;
import cn.Power.util.RenderUtil;
import cn.Power.util.animations.AnimationUtil;
import cn.Power.util.animations.easings.Quint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiButton extends Gui {
	protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

	/** Button width in pixels */
	protected int width = 200;

	/** Button height in pixels */
	protected int height = 20;

	/** The x position of this control. */
	public int xPosition;

	/** The y position of this control. */
	public int yPosition;

	/** The string displayed on this control. */
	public String displayString;
	public int id;

	/** True if this control is enabled, false to disable. */
	public boolean enabled = true;

	/** Hides the button completely if false. */
	public boolean visible = true;
	protected boolean hovered;

	AnimationUtil animUtil;

	private Color baseColor;

	public GuiButton(int buttonId, double x, double y, String buttonText) {
		this(buttonId, x, y, 200, 20, buttonText);
		this.animUtil = new AnimationUtil(Quint.class);
		this.animUtil.addProgression(0).setValue(0);
		this.animUtil.addProgression(1).setValue(1);
		this.baseColor = new Color(0, 0, 0, 200);
	}

	public GuiButton(int buttonId, double x, double y, double widthIn, double heightIn, String buttonText) {
		this.animUtil = new AnimationUtil(Quint.class);
		this.animUtil.addProgression(0).setValue(0);
		this.animUtil.addProgression(1).setValue(1);
		this.width = 200;
		this.height = 20;
		this.enabled = true;
		this.visible = true;
		this.id = buttonId;
		this.xPosition = (int) x;
		this.yPosition = (int) y;
		this.width = (int) widthIn;
		this.height = (int) heightIn;
		this.displayString = buttonText;
		this.baseColor = new Color(0, 0, 0, 120);
	}

	public GuiButton(int buttonId, double x, double y, String buttonText, boolean enabled) {
		this(buttonId, x, y, 200, 20, buttonText);
		this.animUtil = new AnimationUtil(Quint.class);
		this.animUtil.addProgression(0).setValue(0);
		this.animUtil.addProgression(1).setValue(1);
		this.enabled = enabled;
		this.baseColor = new Color(0, 0, 0, 120);
	}

	public GuiButton(int buttonId, double x, double y, double widthIn, double heightIn, String buttonText,
			boolean enabled) {
		this.animUtil = new AnimationUtil(Quint.class);
		this.animUtil.addProgression(0).setValue(0);
		this.animUtil.addProgression(1).setValue(1);
		this.width = 200;
		this.height = 20;
		this.visible = true;
		this.id = buttonId;
		this.xPosition = (int) x;
		this.yPosition = (int) y;
		this.width = (int) widthIn;
		this.height = (int) heightIn;
		this.displayString = buttonText;
		this.enabled = enabled;
		this.baseColor = new Color(0, 0, 0, 120);
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this
	 * button and 2 if it IS hovering over this button.
	 */
	protected int getHoverState(boolean mouseOver) {
		int i = 1;

		if (!this.enabled) {
			i = 0;
		} else if (mouseOver) {
			i = 2;
		}

		return i;
	}

	private float alpha = 120;

	/**
	 * Draws this button to the screen.
	 */
//    public void drawButtonOld(Minecraft mc, int mouseX, int mouseY)
//    {
//        if (this.visible)
//        {
//            FontRenderer fontrenderer = mc.fontRendererObj;
//            mc.getTextureManager().bindTexture(buttonTextures);
//            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
//            int i = this.getHoverState(this.hovered);
//            GlStateManager.enableBlend();
//            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//            GlStateManager.blendFunc(770, 771);
////            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
////            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
//            final int delta = (int) Client.instance.delta;
//
//            if (enabled && hovered) {
//               cut += 0.05F * delta;
//
//               if (cut >= 4) cut = 4;
//
//               alpha += 0.3F * delta;
//
//               if (alpha >= 210) alpha = 210;
//            } else {
//               cut -= 0.05F * delta;
//
//               if (cut <= 0) cut = 0;
//
//               alpha -= 0.3F * delta;
//
//               if (alpha <= 120) alpha = 120;
//            }
//            
//            Gui.drawRect(this.xPosition + (int) this.cut, this.yPosition,
//                    this.xPosition + this.width - (int) this.cut, this.yPosition + this.height,
//                    this.enabled ? new Color(0F, 0F, 0F, this.alpha / 255F).getRGB() :
//                            new Color(0.5F, 0.5F, 0.5F, 0.5F).getRGB());
//            
//            this.mouseDragged(mc, mouseX, mouseY);
//            int j = 14737632;
//
//            if (!this.enabled)
//            {
//                j = 10526880;
//            }
//            else if (this.hovered)
//            {
//                j = 16777120;
//            }
//
//            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
//        }
//    }

	double pennerX;

	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
				&& mouseY < this.yPosition + this.height) && enabled;
		if (hovered) {
			animUtil.getProgression(1).setValue(0);
			pennerX = animUtil.easeOut(0, 0, 1, .3);
		} else {
			animUtil.getProgression(0).setValue(0);
			pennerX = 1 - animUtil.easeOut(1, 0, 1, .3);
		}
		pennerX = MathHelper.clamp_double(pennerX, 0, 1);

		try {
		final int delta = (int) Client.instance.delta;

		if (enabled && hovered) {
			alpha += 0.3F * delta;
			if (alpha >= 180)
				alpha = 180;
		} else {
			alpha -= 0.3F * delta;
			if (alpha <= 120)
				alpha = 120;
		}
		
		Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height,
				this.enabled ? new Color(0F, 0F, 0F, this.alpha / 255F).getRGB()
						: new Color(0.5F, 0.5F, 0.5F, 0.5F).getRGB());

		}catch(Throwable c) {}
		
		RenderUtil.rect(xPosition, yPosition + height / 2 - 10 * pennerX, 1, 20 * pennerX, new Color(0, 153, 255));
		RenderUtil.rect(xPosition + width, yPosition + height / 2 - 10 * pennerX, -1, 20 * pennerX,
				new Color(0, 153, 255));

		int j = 14737632;

		if (!this.enabled) {
			j = 10526880;
		} else if (this.hovered) {
			j = 16777120;
		}
		this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2,
				this.yPosition + (this.height - 8) / 2, j);
		this.mouseDragged(mc, mouseX, mouseY);
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
	}

	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	public void mouseReleased(int mouseX, int mouseY) {
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent e).
	 */
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition
				&& mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}

	/**
	 * Whether the mouse cursor is currently over the button.
	 */
	public boolean isMouseOver() {
		return this.hovered;
	}

	public void drawButtonForegroundLayer(int mouseX, int mouseY) {
	}

	public void playPressSound(SoundHandler soundHandlerIn) {
		soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
	}

	public int getButtonWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
