package cn.Power.ui.NewClickGui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.Font.FontManager;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.ClickGui;
import cn.Power.ui.Bloom;
import cn.Power.ui.Blur2pointZero;
import cn.Power.ui.BlurUtil;
import cn.Power.ui.CFont.CFontRenderer;
import cn.Power.ui.CFont.FontLoaders;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.handler.MouseInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Shader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author SuChen
 * 
 *         Time : 2020.01.05 12:01
 *
 */
public class NewClickGui extends GuiScreen {
	ArrayList<Mod> mods = new ArrayList(Client.instance.modMgr.getopenValues());
	private MouseInputHandler handlerMid = new MouseInputHandler(2);
	private MouseInputHandler handlerRight = new MouseInputHandler(1);
	private MouseInputHandler handler = new MouseInputHandler(0);

	public int moveX = 0;
	public int moveY = 0;

	public int startX = 160;
	public int startY = 60;

	public int selectCategory = 0;
	private float scrollY;
	private float modscrollY;

	public static boolean binding = false;
	public Mod bmod;
	Value v;
	Value mode;
	public static Mod currentMod = null;
	public boolean dragging;
	public boolean drag;
	public boolean Mdrag;
	

	Map<Category,Map<String, Mod>> map = new HashMap<>();
	
	


	private float anim;

	@Override
	public void initGui() {
		
		
		anim = 1;
	}

	@Override
	public void onGuiClosed() {
		
		drag = false;
		super.onGuiClosed();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (binding) {
			if (keyCode != 1 && keyCode != 211) {
				ClientUtil.sendChatMessage(
						"Bound '" + this.bmod.getName() + "'" + " to '" + Keyboard.getKeyName(keyCode) + "'",
						ChatType.INFO);
				this.bmod.setKey(keyCode);
				Client.instance.fileMgr.saveKeys();
			} else if (keyCode == 211) {
				ClientUtil.sendChatMessage("Unbound '" + this.bmod.getName() + "'", ChatType.WARN);
				this.bmod.setKey(Keyboard.KEY_NONE);
				Client.instance.fileMgr.saveKeys();
			}
			binding = false;
		}
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (dragging) {
			dragging = false;
		}
		if (drag) {
			drag = false;
		}
		super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution rs = new ScaledResolution(Minecraft.getMinecraft());

		ClickGui clickgui = (ClickGui) ModManager.getModByClass(ClickGui.class);

		try {
			
		
		// Bloom.a();

		if (isHovered(startX, startY - 8, startX + 300, startY + 5, mouseX, mouseY)
				&& !isHovered(startX + 289, startY - 8, startX + 296, startY + 0, mouseX, mouseY)) {
			if (handler.canExcecute())
				dragging = true;
		}
		if (dragging) {
			if (moveX == 0 && moveY == 0) {
				moveX = mouseX - startX;
				moveY = mouseY - startY;
			} else {
				startX = mouseX - moveX;
				startY = mouseY - moveY;
			}
		} else {
			if (moveX != 0 || moveY != 0) {
				moveX = 0;
				moveY = 0;
			}
		}
		if (startX > (float) (rs.getScaledWidth() - 303)) {
			startX = rs.getScaledWidth() - 303;
		}
		if (startX < 3) {
			startX = 3;
		}
		if (startY > (float) (rs.getScaledHeight() - 190)) {
			startY = rs.getScaledHeight() - 190;
		}
		if (startY < 12.0f) {
			startY = 12;
		}
		
		if(clickgui.BackGround.getValueState())
			RenderUtil.drawRoundedRect((int)startX, startY - 9, startX + 300, startY + clickgui.MaxHeight.getValueState().intValue() - 1, (float) clickgui.BackGround_Round.getValueState().floatValue(), new Color(25, 18, 31 , 250).getRGB());
		

		GL11.glPushMatrix();
		erase(false);
	//	RenderUtil.drawImage(new ResourceLocation("Power/clickgui/menu.png"), startX - 10, startY - 18, 320, 216,
	//			new Color(155, 155, 155 , 120));
	//	RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelright.png"), startX + 59, startY + 5, 9, 182);
	//	RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelright.png"), startX + 59, startY + 5, 9, 182);
	//	RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelright.png"), startX + 59, startY + 5, 9, 182);
		
		
		GL11.glEnable(3089);
		
		RenderUtil.doGlScissor1(startX - 1, startY - 10, startX + 301, startY + clickgui.MaxHeight.getValueState().intValue());

		// Category Recode by SuChen
		int CY = 0;
		Category[] arrcategory = Category.values();

		int n2 = arrcategory.length;

		for (int i = 0; i < n2; ++i) {
			Category c = Category.values()[i];
			String name = c.name().replaceAll("MOVEMENT", "MOVE");
			RenderUtil.drawImage(new ResourceLocation("Power/clickgui/" + c.name() + ".png"), startX + 4,
					startY + 17 + CY, 12, 12, selectCategory == i ? new Color(150, 170, 155) : new Color(170, 170, 170));
			FontManager.big.drawCenteredString(name.substring(0, 1) + name.toLowerCase().substring(1, name.length()),
					startX + 36, startY + 16 + CY,
					selectCategory == i ? new Color(0, 170, 255).getRGB() : new Color(170, 170, 170).getRGB());
			if (isHovered(startX + 3, startY + 14 + CY, startX + 50, startY + 32 + CY, mouseX, mouseY)
					&& handler.canExcecute()) {
				
				this.scrollY = 0;
				this.modscrollY = 0;
				
				selectCategory = i;
			}
			CY += 26;
		}

		int x = startX + 64;
		int y = startY + 10;
		int vY = startY + 12;
		// for head
		if (selectCategory == 5) {
			for (final Value value : Value.list) {
				if (value.getValueName().split("_")[0].equalsIgnoreCase("Global") && value.isValueMode) {
					String modeName = value.getModeAt(value.getCurrentMode());
					String NameText = String.valueOf((Object) value.getModeTitle());
					String modeCountText = String.valueOf((int) (value.getCurrentMode() + 1)) + "/" + value.mode.size();

					RenderUtil.drawImage(new ResourceLocation("Power/clickgui/Mode_Boolean_Left.png"), x + 144,
							(int) ((int) vY + this.scrollY - 2), 10, 10);
		//			RenderUtil.drawImage(new ResourceLocation("Power/clickgui/Mode_bg.png"), x + 154,
		//					(int) (vY + this.scrollY - 4), 54, 14);
					RenderUtil.drawImage(new ResourceLocation("Power/clickgui/Mode_Boolean_Right.png"), x + 208,
							(int) (vY + this.scrollY - 2), 10, 10);

					FontManager.tiny.drawString(modeName,
							x + 180 - (int) FontManager.tiny.getStringWidth("" + modeName) / 2, vY + scrollY - 1,
							new Color(200, 200, 200).getRGB());
					FontManager.tiny.drawString(NameText, x + 22, vY + scrollY, new Color(153, 153, 169).getRGB());

					FontManager.tiny.drawString(modeCountText,
							x + 230 - (int) FontManager.tiny.getStringWidth(modeCountText), vY + scrollY - 1,
							new Color(153, 153, 169).getRGB());

					if (this.isHovered(this.startX + 151, this.startY + 5, this.startX + 300, this.startY + clickgui.MaxHeight.getValueState().intValue(), mouseX,
							mouseY)
							&& this.isHovered(x + 144, vY + this.scrollY - 1, x + 153, vY + 7 + this.scrollY, mouseX,
									mouseY)
							&& this.handler.canExcecute())
						if (value.getCurrentMode() > 0 && value.getCurrentMode() != 0) {
							value.setCurrentMode(value.getCurrentMode() - 1);
							Client.instance.fileMgr.saveValues();
						} else {
							value.setCurrentMode(value.mode.size() - 1);
							Client.instance.fileMgr.saveValues();
						}

					if (this.isHovered(this.startX + 151, this.startY + 5, this.startX + 300, this.startY + clickgui.MaxHeight.getValueState().intValue(), mouseX,
							mouseY)
							&& this.isHovered(x + 208, vY + this.scrollY - 1, x + 217, vY + 7 + this.scrollY, mouseX,
									mouseY)
							&& this.handler.canExcecute())
						if (value.getCurrentMode() < value.mode.size() - 1) {
							value.setCurrentMode(value.getCurrentMode() + 1);
							Client.instance.fileMgr.saveValues();
						} else {
							value.setCurrentMode(0);
							Client.instance.fileMgr.saveValues();
						}
					vY += 18;
				}
			}

			for (final Value value : Value.list) {
				if (value.getValueName().split("_")[0].equalsIgnoreCase("Global") && value.isValueDouble) {
					this.width = 100;
					float lastMouseX = -1.0f;
					final double val = (double) value.getValueState();
					final double min = (double) value.getValueMin();
					final double max = (double) value.getValueMax();
					
					double percSlider = ((double) value.getValueState() - (double) value.getValueMin())
							/ ((double) value.getValueMax() - (double) value.getValueMin());

					final double valAbs = mouseX - (x + 145);
					double perc = valAbs / 83;
					perc = Math.min(Math.max(0.0, perc), 1.0);
					final double valRel = ((double) value.getValueMax() - (double) value.getValueMin()) * perc;
					double valuu = (double) value.getValueMin() + valRel;
					double valu = (x + 145) + 83 * percSlider;
					// down bar
					RenderUtil.drawRect(x + 145, vY + 3 + scrollY, x + 230, vY + 5 + scrollY,
							new Color(253, 254, 253).getRGB());
					RenderUtil.drawRect((float) x + 145.5f, (float) vY + 3.5f + scrollY, (float) valu + 2f,
							(float) vY + 4.5f + scrollY, new Color(160, 200, 142).getRGB());
					RenderUtil.drawImage(new ResourceLocation("Power/clickgui/Slider.png"), (int) valu - 1,
							(int) (vY + 2 + scrollY), 4, 4, new Color(160, 200, 142));

					FontManager.tiny.drawString("" + (Double) value.getValueState(),
							x + 230 - FontManager.tiny.getStringWidth("" + value.getValueState()), vY - 5 + scrollY,
							new Color(153, 153, 169).getRGB());
					FontManager.tiny.drawString(value.getValueName().split("_")[1], x + 22, vY + scrollY - 1,
							new Color(153, 153, 169).getRGB());
					if (isHovered(startX + 151, startY + 5, startX + 300, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
							&& isHovered(x + 145, vY + 1 + scrollY, x + 230, vY + 7 + scrollY, mouseX, mouseY)
							&& handler.canExcecute()) {
						v = value;
						drag = true;
					}
					if (drag && value == v) {
						lastMouseX = (Math.min(Math.max(x + 155, mouseX), x + 145 + 100) - (float) x + 145) / 100;
						valuu = Math.round(valuu * (1.0 / value.getSteps())) / (1.0 / value.getSteps());
						value.setValueState(valuu);
						Client.instance.fileMgr.saveValues();
					} else {
						valuu = Math.round((double) value.getValueState() * (1.0 / value.getSteps()))
								/ (1.0 / value.getSteps());
						value.setValueState(valuu);
					}
					vY += 18;
				}
			}

			for (final Value value : Value.list) {
				if (value.getValueName().split("_")[0].equalsIgnoreCase("Global") && value.isValueBoolean) {
					RenderUtil.drawImage(new ResourceLocation("Power/clickgui/ValueBoolean_bg.png"), x + 214,
							(int) (vY + scrollY - 1), 16, 8, new Color(255, 255, 255));
					if ((Boolean) value.getValueState()) {
						RenderUtil.drawImage(new ResourceLocation("Power/clickgui/ValueBoolean_button.png"), x + 222,
								(int) (vY + scrollY - 1), 8, 8, new Color(255, 255, 255, 240));
					} else {
						RenderUtil.drawImage(new ResourceLocation("Power/clickgui/ValueBoolean_button.png"), x + 214,
								(int) (vY + scrollY - 1), 8, 8, new Color(0, 0, 0, 110));
					}
					if (isHovered(startX + 151, startY + 5, startX + 300, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
							&& isHovered(x + 214, vY + scrollY - 1, x + 230, vY + 7 + scrollY, mouseX, mouseY)
							&& handler.canExcecute()) {
						value.setValueState(!(Boolean) value.getValueState());
						Client.instance.fileMgr.saveValues();
					}
					FontManager.tiny.drawString(value.getValueName().split("_")[1], x + 22, vY + scrollY - 1,
							new Color(153, 153, 169).getRGB());

					vY += 18;
				}
			}

			float MaxScrollY = (startY + clickgui.MaxHeight.getValueState().intValue());
			if (getGlobalList().size() > 10 && vY > startY + clickgui.MaxHeight.getValueState().intValue()
					&& isHovered(startX + 60, startY - 8, startX + 300, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)) {
				final float scroll = (float) Mouse.getDWheel();
				this.scrollY += scroll / 10.0f;
			}
			if (scrollY > 0.0) {
				scrollY = 0.0F;
			}
			if (getGlobalList().size() > 10 && scrollY < (getGlobalList().size() - 10) * -18) {
				scrollY = (getGlobalList().size() - 10) * -18;
			}
		}
		for (int i = 0; i < getModsInCategory(Category.values()[selectCategory]).size(); ++i) {
			Mod mod = (Mod) getModsInCategory(Category.values()[selectCategory]).get(i);

			// TODO Mod scroll
			if (isHovered(startX + 60, startY + 5, startX + 150, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
					&& getModsInCategory(Category.values()[selectCategory]).size() > 11
					&& isHovered(x, y - 2, x + 82, y + 12, mouseX, mouseY)) {
				float modscroll = (float) Mouse.getDWheel();
				this.modscrollY += modscroll / 10.0f;
			}
			if (getModsInCategory(Category.values()[selectCategory]).size() <= 11) {
				modscrollY = 0.0F;
			}
			if (modscrollY > 0.0) {
				modscrollY = 0.0F;
			}
			// RenderUtils.drawBorderedRect(w / 2 - 120, h / 2 - 80, w / 2 + 120, h / 2 +
			// 80, 0.5, 0x00000000, HUD.getColor());
			if (getModsInCategory(Category.values()[selectCategory]).size() > 11
					&& modscrollY < (getModsInCategory(Category.values()[selectCategory]).size() - 11) * -16) {
				modscrollY = (getModsInCategory(Category.values()[selectCategory]).size() - 11) * -16;
			}

			// mod backgorund
			if (isHovered(startX + 60, startY + 5, startX + 150, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
					&& isHovered(x, y - 2 + modscrollY, x + 82, y + 12 + modscrollY, mouseX, mouseY)) {
				RenderUtil.drawImage(new ResourceLocation("Power/clickgui/MOD.png"), x, (int) (y - 2 + modscrollY), 82,
						14, new Color(20, 20, 20, 140));
			} else {
				RenderUtil.drawImage(new ResourceLocation("Power/clickgui/MOD.png"), x, (int) (y - 2 + modscrollY), 82,
						14, mod.isEnabled() ? new Color(210, 210, 210, 70) : new Color(71, 71, 71, 10));
			}

			FontManager.icon10.drawString("k", x + 4, y + 2 + modscrollY,
					mod.isEnabled() ? new Color(0, 124, 255).getRGB() : new Color(153, 153, 153).getRGB());

			// mod name
			FontManager.baloo18.drawCenteredString(
					binding ? mod == bmod ? "Binding Key" : mod.getName() : mod.getName(), x + 40, y - 1 + modscrollY,
							isHovered(startX + 60, startY + 5, startX + 150, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
							&& isHovered(x, y - 2 + modscrollY, x + 82, y + 12 + modscrollY, mouseX, mouseY) ? (mod.isEnabled() ? ( new Color(240, 240, 240, 240).getRGB()) : new Color(220, 220, 220).getRGB()) : (mod.isEnabled() ? ( new Color(220, 220, 220, 140).getRGB()) : new Color(240, 240, 240).getRGB()));
			// binding
			FontManager.big.drawCenteredString(mod.openValues ? "-" : "+", x + 74, y - 3 + modscrollY,
					new Color(153, 153, 153).getRGB());

			if (isHovered(startX + 60, startY + 5, startX + 150, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
					&& isHovered(x, y - 2 + modscrollY, x + 82, y + 12 + modscrollY, mouseX, mouseY)
					&& handlerMid.canExcecute()) {
				binding = true;
				bmod = mod;
			}

			// mod open
			if (isHovered(startX + 60, startY + 5, startX + 150, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
					&& isHovered(x, y - 2 + modscrollY, x + 82, y + 12 + modscrollY, mouseX, mouseY)
					&& handler.canExcecute()) {
				mod.set(!mod.isEnabled());
			}
			String ValueName;
			// Open Value
			if (isHovered(startX + 60, startY + 5, startX + 150, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
					&& isHovered(x, y - 2 + modscrollY, x + 82, y + 12 + modscrollY, mouseX, mouseY)
					&& handlerRight.canExcecute() && !mod.openValues) {
				for (Object m1 : ModManager.modList.values().toArray()) {
					Mod mods = (Mod) m1;
					if (mods.openValues) {
						mods.openValues = false;
					}
				}
				mod.openValues = true;
				currentMod = mod;
				Mdrag = false;
				this.scrollY = 0.0F;
				Client.instance.fileMgr.saveValues();
			}
			if (mod.openValues) {
				if (isHovered(startX + 151, startY + 5, startX + 300, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
						&& isHovered(x + 219, vY + scrollY - 1, x + 228, vY + 7 + scrollY, mouseX, mouseY)
						&& handler.canExcecute()) {
					if (mod.isEnabled()) {
						mod.getModuleProgressionX().setValue(0);
						mod.getModuleProgressionY().setValue(0);
					}
					mod.HideMod = !mod.HideMod;
					Client.instance.fileMgr.saveHideMods();
				}

				FontManager.tiny.drawString("HideModList", x + 95, vY + scrollY - 1, new Color(153, 153, 153).getRGB());
				drawRect(x + 219, (vY + scrollY - 1), x + 227, (vY + scrollY - 1) + 8, new Color(53, 53, 53).getRGB());
				if (mod.HideMod) {
					this.drawLine2D(x + 219.5, (vY + scrollY - 1) + 3.0f, x + 220 + 2.0f, (vY + scrollY - 1) + 6.0f,
							1.5f, new Color(0, 153, 255).getRGB());
					this.drawLine2D(x + 220 + 2.0f, (vY + scrollY - 1) + 6.0f, x + 220 + 6.5f,
							(vY + scrollY - 1) + 1.0f, 1.5f, new Color(0, 153, 255).getRGB());
				}
				vY += 18;
				for (final Value value : Value.list) {

					if (value.getValueName().split("_")[0].equalsIgnoreCase(mod.getName()) && value.isValueMode) {
						if (value.getCurrentMode() >= value.mode.size())
							value.setCurrentMode(0);
						String modeName = value.getModeAt(value.getCurrentMode());
						String NameText = String.valueOf((Object) value.getModeTitle());
						String modeCountText = String.valueOf((int) (value.getCurrentMode() + 1)) + "/"
								+ value.mode.size();

						RenderUtil.drawImage(new ResourceLocation("Power/clickgui/Mode_Boolean_Left.png"), x + 144,
								(int) ((int) vY + this.scrollY - 2), 10, 10);
						RenderUtil.drawImage(new ResourceLocation("Power/clickgui/Mode_bg.png"), x + 154,
								(int) (vY + this.scrollY - 4), 54, 14);
						RenderUtil.drawImage(new ResourceLocation("Power/clickgui/Mode_Boolean_Right.png"), x + 208,
								(int) (vY + this.scrollY - 2), 10, 10);

						FontManager.tiny.drawString(modeName,
								x + 180 - (int) FontManager.tiny.getStringWidth("" + modeName) / 2, vY + scrollY - 1,
								new Color(200, 200, 200).getRGB());
						FontManager.tiny.drawString(NameText, x + 95, vY + scrollY, new Color(153, 153, 169).getRGB());

						FontManager.tiny.drawString(modeCountText,
								x + 230 - (int) FontManager.tiny.getStringWidth(modeCountText), vY + scrollY - 1,
								new Color(153, 153, 169).getRGB());

						if (this.isHovered(this.startX + 151, this.startY + 5, this.startX + 300, this.startY + clickgui.MaxHeight.getValueState().intValue(),
								mouseX, mouseY)
								&& this.isHovered(x + 144, vY + this.scrollY - 1, x + 153, vY + 7 + this.scrollY,
										mouseX, mouseY)
								&& this.handler.canExcecute())
							if (value.getCurrentMode() > 0 && value.getCurrentMode() != 0) {
								value.setCurrentMode(value.getCurrentMode() - 1);
								Client.instance.fileMgr.saveValues();
							} else {
								value.setCurrentMode(value.mode.size() - 1);
								Client.instance.fileMgr.saveValues();
							}

						if (this.isHovered(this.startX + 151, this.startY + 5, this.startX + 300, this.startY + clickgui.MaxHeight.getValueState().intValue(),
								mouseX, mouseY)
								&& this.isHovered(x + 208, vY + this.scrollY - 1, x + 217, vY + 7 + this.scrollY,
										mouseX, mouseY)
								&& this.handler.canExcecute())
							if (value.getCurrentMode() < value.mode.size() - 1) {
								value.setCurrentMode(value.getCurrentMode() + 1);
								Client.instance.fileMgr.saveValues();
							} else {
								value.setCurrentMode(0);
								Client.instance.fileMgr.saveValues();
							}
						vY += 18;
					}
				}

				for (final Value value : Value.list) {
					if (value.getValueName().split("_")[0].equalsIgnoreCase(mod.getName()) && value.isValueDouble) {
						this.width = 100;
						float lastMouseX = -1.0f;
						final double val = (double) value.getValueState();
						final double min = (double) value.getValueMin();
						final double max = (double) value.getValueMax();
						double percSlider = ((double) value.getValueState() - (double) value.getValueMin())
								/ ((double) value.getValueMax() - (double) value.getValueMin());

						final double valAbs = mouseX - (x + 145);
						double perc = valAbs / 83;
						perc = Math.min(Math.max(0.0, perc), 1.0);
						final double valRel = ((double) value.getValueMax() - (double) value.getValueMin()) * perc;
						double valuu = (double) value.getValueMin() + valRel;
						double valu = (x + 145) + 83 * percSlider;
						// down bar
						RenderUtil.drawRect(x + 145, vY + 3 + scrollY, x + 230, vY + 5 + scrollY,
								new Color(153, 154, 153).getRGB());
						RenderUtil.drawRect((float) x + 145.5f, (float) vY + 3.5f + scrollY, (float) valu + 2f,
								(float) vY + 4.5f + scrollY, new Color(220, 200, 242).getRGB());
						RenderUtil.drawImage(new ResourceLocation("Power/clickgui/Slider.png"), (int) valu - 1,
								(int) (vY + 2 + scrollY), 4, 4, new Color(220, 200, 242));

						FontManager.tiny.drawString("" + (Double) value.getValueState(),
								x + 230 - FontManager.tiny.getStringWidth("" + value.getValueState()), vY - 5 + scrollY,
								new Color(153, 153, 169).getRGB());
						FontManager.tiny.drawString(value.getValueName().split("_")[1], x + 95, vY + scrollY - 1,
								new Color(153, 153, 169).getRGB());
						if (isHovered(startX + 151, startY + 5, startX + 300, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
								&& isHovered(x + 145, vY + 1 + scrollY, x + 230, vY + 7 + scrollY, mouseX, mouseY)
								&& handler.canExcecute()) {
							v = value;
							drag = true;
						}
						if (drag && value == v) {
							lastMouseX = (Math.min(Math.max(x + 155, mouseX), x + 145 + 100) - (float) x + 145) / 100;
							valuu = Math.round(valuu * (1.0 / value.getSteps())) / (1.0 / value.getSteps());
							value.setValueState(valuu);
							Client.instance.fileMgr.saveValues();
						} else {
							valuu = Math.round((double) value.getValueState() * (1.0 / value.getSteps()))
									/ (1.0 / value.getSteps());
							value.setValueState(valuu);
						}
						vY += 18;
					}
				}

				for (final Value value : Value.list) {
					if (value.getValueName().split("_")[0].equalsIgnoreCase(mod.getName()) && value.isValueBoolean) {
						RenderUtil.drawImage(new ResourceLocation("Power/clickgui/ValueBoolean_bg.png"), x + 214,
								(int) (vY + scrollY - 1), 16, 8, new Color(255, 255, 255));
						if ((Boolean) value.getValueState()) {
							RenderUtil.drawImage(new ResourceLocation("Power/clickgui/ValueBoolean_button.png"),
									x + 222, (int) (vY + scrollY - 1), 8, 8, new Color(255, 255, 255, 240));
							// RenderUtil.drawRect(x + 220, vY + scrollY, x + 229, vY + 6 + scrollY,new
							// Color(0, 100, 242).getRGB());
						} else {
							RenderUtil.drawImage(new ResourceLocation("Power/clickgui/ValueBoolean_button.png"),
									x + 214, (int) (vY + scrollY - 1), 8, 8, new Color(0, 0, 0, 110));
							// RenderUtil.drawRect(x + 211, vY + scrollY, x + 220, vY + 6 + scrollY,new
							// Color(200,200,200).getRGB());
						}

						if (isHovered(startX + 151, startY + 5, startX + 300, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)
								&& isHovered(x + 214, vY + scrollY - 1, x + 230, vY + 7 + scrollY, mouseX, mouseY)
								&& handler.canExcecute()) {
							value.setValueState(!(Boolean) value.getValueState());
							Client.instance.fileMgr.saveValues();
						}
						FontManager.tiny.drawString(value.getValueName().split("_")[1], x + 95, vY + scrollY - 1,
								new Color(153, 153, 169).getRGB());

						vY += 18;
					}

				}

//		       }
				float MaxScrollY = (startY + clickgui.MaxHeight.getValueState().intValue());
				if (getValueList(mod).size() > 9 && vY > startY + clickgui.MaxHeight.getValueState().intValue()
						&& isHovered(startX + 151, startY - 8, startX + 300, startY + clickgui.MaxHeight.getValueState().intValue(), mouseX, mouseY)) {
					final float scroll = (float) Mouse.getDWheel();
					this.scrollY += scroll / 10.0f;
				}
				if (scrollY > 0.0) {
					scrollY = 0.0F;
				}
				if (getValueList(mod).size() > 9 && scrollY < (getValueList(mod).size() - 10) * -18 - 18) {
					scrollY = (getValueList(mod).size() - 10) * -18 - 18;
				}
			}
			y += 16;
		}

		// top

		GL11.glDisable(3089);
		GL11.glPopMatrix();
		
	//	RenderUtil.drawRect(startX + 60, startY - 8, startX + 300, startY + 4, new Color(27, 27, 27).getRGB());
		if (isHovered(startX + 289, startY - 8, startX + 296, startY + 0, mouseX, mouseY)) {
			RenderUtil.drawImage(new ResourceLocation("Power/clickgui/open.png"), startX + 288, startY - 8, 10, 10,
					new Color(255, 0, 0));
			if (handler.canExcecute()) {
				this.mc.displayGuiScreen(null);
				this.mc.setIngameFocus();
			}
		} else {
			RenderUtil.drawImage(new ResourceLocation("Power/clickgui/open.png"), startX + 288, startY - 8, 10, 10,
					new Color(0, 125, 255));
		}

		FontManager.sw18.drawCenteredString("Power X", startX + 6 * 5, startY - 7, new Color(170, 170, 170).getRGB());
		FontManager.baloo16.drawString(Category.values()[selectCategory].name(), startX + 64, startY - 8,
				new Color(153, 153, 159).getRGB());
		// if(currentMod!= null)
		// font2.drawString("Module:"+currentMod.getName(),startX + 152, startY - 5 ,
		// new Color(153,153,159).getRGB());
		RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelbottom.png"), 0, 0, 0, 9);

		if (selectCategory != 5) {
			RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelright.png"), 0, 0, 0,
					0);
			RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelleft.png"), 0, 0, 0,
					0);
		}
		RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelleft.png"), 0, 0, 0, 0);
		RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelright.png"), 0, 0, 0, 0);

		RenderUtil.drawImage(new ResourceLocation("Power/clickgui/paneltop.png"), 0, 0, 0, 0);
		
		
		
		

	//	 RenderUtil.drawImage(new ResourceLocation("Power/clickgui/panelleft.png"),
	//	  startX+142, startY+5, 10, clickgui.MaxHeight.getValueState().intValue(), new Color(255,255,255));
		 
		
	}catch(Throwable c) {c.printStackTrace();}
	
	}

	public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		return (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2);
	}

	public static void erase(boolean invert) {
//		GL11.glStencilFunc(invert ? GL11.GL_EQUAL : GL11.GL_NOTEQUAL, (int) 1, (int) 65535);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0f);
	}

	// Create By suchen
	public static List getValueList(Mod mods) {
		List modList = new ArrayList();
		Iterator var3 = Value.list.iterator();

		while (var3.hasNext()) {
			Value mod = (Value) var3.next();
			if (currentMod == null) {
				return null;
			}
			if (mod.getValueName().split("_")[0].equalsIgnoreCase(mods.getName())) {
				modList.add(mod);
			}
		}
		return modList;
	}

	public static List getGlobalList() {
		List modList = new ArrayList();
		for (final Value value : Value.list) {
			if (value.getValueName().split("_")[0].equalsIgnoreCase("Global")) {
				modList.add(value);
			}
		}
		return modList;
	}

	public List getModsInCategory(Category category) {

		if (map.isEmpty() || map.getOrDefault(category, null) == null) {
			
			
			
			
			ArrayList<Mod> clip = (ArrayList<Mod>) ModManager.modList.values().stream()
					.collect(Collectors.toCollection(ArrayList::new));

			TreeMap<String, Mod> con = new TreeMap<>();
			
			
			clip.forEach(m -> {
				
				if(m.getCategory().equals(category)) { 
					
					
					con.put(m.getName(), m);
					
					
				
				}
				
			});
			
			map.put(category, con);
			
			
			return con.values().stream()
					.collect(Collectors.toCollection(ArrayList::new));
			
		
			
		}
		
		List ccc = map.entrySet().stream().filter(category2 -> category2.getKey().equals(category)).map(Map.Entry::getValue).collect(Collectors.toCollection(ArrayList::new));
		
		TreeMap<String, Mod> con = (TreeMap<String, Mod>) ccc.get(0);
		
		return con.values().stream().collect(Collectors.toCollection(ArrayList::new));
	}

	public static void drawLine2D(double x1, double y1, double x2, double y2, float width, int color) {
		enableRender2D();
		setColor(color);
		GL11.glLineWidth(width);
		GL11.glBegin(1);
		GL11.glVertex2d(x1, y1);
		GL11.glVertex2d(x2, y2);
		GL11.glEnd();
		disableRender2D();
	}

	public static void enableRender2D() { 
		GL11.glEnable(3042);
		GL11.glDisable(2884);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glBlendFunc(770, 771);
		GL11.glLineWidth(1.0f);
	}

	public static void disableRender2D() {
		GL11.glDisable(3042);
		GL11.glEnable(2884);
		GL11.glEnable(3553);
		GL11.glDisable(2848);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}

	public static void setColor(int colorHex) {
		float alpha = (colorHex >> 24 & 0xFF) / 255.0f;
		float red = (colorHex >> 16 & 0xFF) / 255.0f;
		float green = (colorHex >> 8 & 0xFF) / 255.0f;
		float blue = (colorHex & 0xFF) / 255.0f;
		GL11.glColor4f(red, green, blue, (alpha == 0.0f) ? 1.0f : alpha);
	}
}