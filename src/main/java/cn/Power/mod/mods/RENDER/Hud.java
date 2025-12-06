package cn.Power.mod.mods.RENDER;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.Font.FontManager;
import cn.Power.Font.FontUtils;
import cn.Power.events.EventKeyboard;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender2D;
import cn.Power.events.EventRespawn;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.ClickGui;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.COMBAT.Tpaura;
import cn.Power.notification.Notification.Type;
import cn.Power.ui.SmoothColorProvider;
import cn.Power.ui.CFont.CFontRenderer;
import cn.Power.ui.CFont.FontLoaders;
import cn.Power.util.ClientUtil;
import cn.Power.util.Colors;
import cn.Power.util.FlatColors;
import cn.Power.util.MathUtils;
import cn.Power.util.Palette;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.TargetHUD;
import cn.Power.util.TargetHUDOld;
import cn.Power.util.animations.AnimationUtil;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class Hud extends Mod {

	public Value<String> logo_displaymode = new Value("Hud", "LogoStyle", 0);
	public Value<String> logo_mode = new Value("Hud", "TabGui", 0);
	public Value<String> Array_Font = new Value("Hud", "Font", 0);
	public Value<String> Array_mode = new Value("Hud", "Array", 0);
	public Value<String> Effect_mode = new Value("Hud", "Effect", 1);
	public Value<String> cm = new Value("Hud", "ChunkAnima", 4);
	public Value<Double> bg = new Value<Double>("Hud_background", 100.0, 0.0, 255.0, 1);
	public Value<Double> oldrainbow = new Value<Double>("Hud_OldRainBow", 4819.0, 0.0, 9999.0, 1);
	public static Value<Double> rainbow = new Value<Double>("Hud_RainBowHue", 3200.0, 0.0, 9999.0, 1);
	public static Value<Double> rainbow_Satu = new Value<Double>("Hud_RainBowSatu", 0.35, 0.0, 1.0, 0.01);
	public static Value<Double> rainbow_Bright = new Value<Double>("Hud_RainBowBright", 0.75, 0.0, 1.0, 0.01);
	public static Value<Double> rainbow_Step = new Value<Double>("Hud_RainBowStep", 25.0d, 1.0, 150.0, 1.0);

	public Value<Double> TabguiOffset = new Value<Double>("Hud_TabguiOffset", 10.0, 0.0, 20.0, 0.1);
	public Value<Double> ArraySpacing = new Value<Double>("Hud_ArrayTextSpacing", 4.6, -20.0, 20.0, 0.1);

	public Value<Boolean> logo = new Value("Hud_Logo", true);
	public Value<Boolean> hotbar = new Value("Hud_Hotbar", false);
	public Value<Boolean> OldRainBow = new Value("Hud_OldRainBow", false);
	public Value<Boolean> TabGui = new Value("Hud_TabGui", true);
	public Value<Boolean> array = new Value("Hud_ArrayList", true);
	public static Value<Boolean> ArmorHud = new Value("Hud_ArmorHud", true);
	public static Value<Boolean> info = new Value("Hud_info", true);
	public static Value<Boolean> Health = new Value("Hud_Health", false);
	public static Value<Boolean> Shadow = new Value("Hud_FontShadow", true);
	TimeHelper time = new TimeHelper();

	public static ResourceLocation inv = new ResourceLocation("textures/gui/container/inventory.png");

	private ArrayList blocks = new ArrayList();
	private double cSI = Double.NaN;
	private double cSJ = Double.NaN;
	public double cSK;

	public Minecraft mc = Minecraft.getMinecraft();

	FontUtils font; // = 
	CFontRenderer font1; // = 

	private int s = 0;
	int x = 0;
	public static float YPort;

	public int tick;

	public ArrayList categoryValues = new ArrayList();
	public int currentCategoryIndex = 0;
	public int currentModIndex = 0;
	public int currentSettingIndex = 0;
	public int screen = 0;

	private AnimationUtil animUtil;
	
	private static SmoothColorProvider color;
	
	public static int getOldColor() {
		int r = Client.r.getValueState().intValue();
		int g = Client.g.getValueState().intValue();
		int b = Client.b.getValueState().intValue();
		int color = Client.Rainbow.getValueState() ? RenderUtil.rainbow(300) : Colors.getColor(r, g, b);

		return color;
	}

	public Hud() {
		super("Hud", Category.RENDER);
		HideMod = true;
		this.categoryValues.addAll(Arrays.asList(Category.values()));
		this.logo_mode.mode.add("Power");
		this.logo_mode.mode.add("ETB");
		this.logo_mode.mode.add("Customize");
		this.logo_mode.mode.add("New");

		this.logo_displaymode.mode.add("None");
		this.logo_displaymode.mode.add("Power");
		this.logo_displaymode.mode.add("ETB");
		this.logo_displaymode.mode.add("Customize");

		this.Array_Font.mode.add("Baloo");
		this.Array_Font.mode.add("ArialBold");

		this.Array_mode.mode.add("Normal");
		this.Array_mode.mode.add("Side");
		this.Array_mode.mode.add("All");

		this.Effect_mode.mode.add("NONE");
		this.Effect_mode.mode.add("Text");
		this.Effect_mode.mode.add("Image");
		this.Effect_mode.mode.add("Picture Text");

		this.cm.mode.add("1");
		this.cm.mode.add("2");
		this.cm.mode.add("3");
		this.cm.mode.add("4");
		this.cm.mode.add("NONE");

		this.animUtil = new AnimationUtil(cn.Power.util.animations.easings.Cubic.class);
		animUtil.addProgression(1337);

//		if (ModList != FontManager.baloo18 && Array_Font.isCurrentMode("Baloo")) {
//			ModList = FontManager.baloo18;
//		} else if (ModList != FontManager.normal && Array_Font.isCurrentMode("ArialBold")) {
//			ModList = FontManager.normal;
//		}
	}

	FontRenderer fr = mc.fontRendererObj;
	boolean lowhealth = false;

	@EventTarget
	public void Code(EventPreMotion emu) {
		if (!Double.isNaN(this.cSI) && !Double.isNaN(this.cSJ)) {
			double v2 = Math.abs(this.cSI - Minecraft.getMinecraft().thePlayer.posX);
			double v4 = Math.abs(this.cSJ - Minecraft.getMinecraft().thePlayer.posZ);
			double v6 = Math.sqrt(v2 * v2 + v4 * v4) * 2.0D;
			this.blocks.add(Double.valueOf(v6));
			if (this.blocks.size() > 20) {
				this.blocks.remove(0);
			}
		}
		this.cSI = Minecraft.getMinecraft().thePlayer.posX;
		this.cSJ = Minecraft.getMinecraft().thePlayer.posZ;

	}

	public double ahC() {
		double v2 = 0.0D;

		double v4;
		for (Iterator var5 = this.blocks.iterator(); var5.hasNext(); v2 += v4) {
			v4 = ((Double) var5.next()).doubleValue();
		}

		return v2;
	}

	@Override
	public void onDisable() {

//		ModList = new FontUtils("Parke.ttf", 0, 9, 7, false);
		animUtil.getProgression(1337).setValue(0);
		if (mc.thePlayer == null || mc.theWorld == null)
			return;
		Client.instance.modMgr.modList.values().stream().forEach(module -> {
			if (!((Mod) module).isEnabled()) {
//				module.getModuleProgressionX().setValue(1);
				((Mod) module).getModuleProgressionY().setValue(1);
			} else {
//				module.getModuleProgressionX().setValue(0);
				((Mod) module).getModuleProgressionY().setValue(0);
			}
		});
	}

	@EventTarget(4)
	public void onRender2D(EventRender2D event) {
		
		if(font == null) {
			font = FontManager.normal;
		}
		
		if(font1 == null) {
			font1 = FontLoaders.kiona28;
		}
		
		ScaledResolution sr = new ScaledResolution(mc);


		if (!this.mc.gameSettings.showDebugInfo) {
			// Health low warning
			if (mc.thePlayer.getHealth() < 6 && !lowhealth) {
				Client.instance.getNotificationManager().addNotification("Your Health is Low!", Type.WARNING);
				lowhealth = true;
			}

			if (mc.thePlayer.getHealth() > 6 && lowhealth) {
				lowhealth = false;
			}

			if (this.array.getValueState().booleanValue()) {
//				drawSimpleton(sr);
				drawModList(sr);
			}

			this.renderPotionStatus(sr);
			this.renderPotionStatus1(sr);
			this.renderPotionStatus2(sr);

//			this.renderMods();

			// this.renderToggled(sr);
			if (this.logo.getValueState().booleanValue()) {
				this.renderLogo();
			} else {
				YPort = TabguiOffset.getValueState().floatValue();
			}
			if (this.TabGui.getValueState().booleanValue()) {
				renderTabgui();
			}
			if (this.ArmorHud.getValueState().booleanValue()) {
				renderStuffStatus(sr);
			}
		}
		if (this.Health.getValueState().booleanValue()) {
			renderHealth(sr);
		}
		String ETB = "XYZ " + ((Object) EnumChatFormatting.GRAY) + +MathHelper.floor_double(this.mc.thePlayer.posX)
				+ ", " + MathHelper.floor_double(this.mc.thePlayer.posY) + ", "
				+ MathHelper.floor_double(this.mc.thePlayer.posZ);

		String FPS = "FPS " + (Object) ((Object) EnumChatFormatting.GRAY) + Minecraft.getDebugFPS();

		if (MathUtils.round(this.ahC(), 2) > this.cSK) {
			this.cSK = MathUtils.round(this.ahC(), 2);
		}
		String speed = MathUtils.round(this.ahC(), 1) / 2 + " blocks/sec";
		if (this.info.getValueState().booleanValue()) {
			int ychat;
			int n = ychat = this.mc.ingameGUI.getChatGUI().getChatOpen() ? 26 : 12;

			FontManager.baloo18.drawStringWithShadow(ETB, (float) 2.0,
					new ScaledResolution(this.mc).getScaledHeight() - ychat, getColor());
			FontManager.baloo18.drawStringWithShadow(FPS, FontLoaders.kiona16.getStringWidth(ETB) + 8,
					new ScaledResolution(this.mc).getScaledHeight() - ychat, getColor());
			FontManager.baloo18.drawStringWithShadow(speed,
					FontLoaders.kiona16.getStringWidth(ETB)
							+ FontLoaders.kiona16.getStringWidth(FPS) + 12,
					new ScaledResolution(this.mc).getScaledHeight() - ychat, getColor());
		}
		String user = "\2477" + "Dev" + " - \247a" + Client.ClientUser;
		String build = "\2477" + "Build" + " - \247f" + Client.CLIENT_Bulid + " \2477- "
				+ "\2477ViaVersion \2477- \247f" + ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion();
		FontManager.sw18.drawStringWithShadow(user,
				(float) sr.getScaledWidth() - FontManager.sw18.getStringWidth(user) - 1.0F,
				(float) (sr.getScaledHeight() - (20)), Colors.getColor(255, 220));
		FontManager.sw18.drawStringWithShadow(build,
				(float) sr.getScaledWidth() - FontManager.sw18.getStringWidth(build) - 1.0F,
				(float) (sr.getScaledHeight() - (10)), Colors.getColor(255, 220));
	}

	public static int getColor() {
		int r = Client.r.getValueState().intValue();
		int g = Client.g.getValueState().intValue();
		int b = Client.b.getValueState().intValue();
		int color = Colors.getColor(r, g, b);

		return color;
	}

	String name;
	String ClientName;

	public void renderLogo() {

		if (this.logo_displaymode.isCurrentMode("Power")) {
			YPort = font1.getHeight() + 7.0F;
			DateFormat dft = new SimpleDateFormat("HH:mm:ss");
			Date time = Calendar.getInstance().getTime();
			String rendertime = dft.format(time);
			ClientName = "Power";
			name = ClientName.substring(0, 1);

			RenderUtils.drawRect(2.0, 2.0, 53.0, 19.0, ClientUtil.INSTANCE.reAlpha(Colors.BLACK.c, 0.6f));
			RenderUtils.drawRect(2.3, 2.3, 4.0, 18.3, (int) getColor());

			font1.drawString(name, 6.0f, 6.0f, getColor());
			font1.drawString(ClientName.substring(1), 7.0f + font1.getStringWidth(name), 6.0f, -1);
		}
		if (this.logo_displaymode.isCurrentMode("ETB")) {
			YPort = font1.getHeight();
			String dire = Direction.values()[MathHelper.floor_double(mc.thePlayer.rotationYaw * 4.0f / 180.0f + 0.5)
					& 0x7].name();
			String text = Client.CLIENT_name;
			String text_b = text.substring(1) + " " + Client.CLIENT_VER;
			fr.drawStringWithShadow("ETB \24770.6\247r [" + dire + "]", 2, 3, Colors.getColor(90, 169, 248));

		}

		if (this.logo_displaymode.isCurrentMode("Customize")) {
			YPort = font1.getHeight();
			if (Shadow.getValueState()) {
				FontManager.baloo18.drawStringWithShadow(Client.CLIENT_name.substring(0, 1), 2, 1, getColor());
				FontManager.baloo18.drawStringWithShadow(Client.CLIENT_name.substring(1),
						3 + FontLoaders.tahoma18.getStringWidth(Client.CLIENT_name.substring(0, 1)), 1,
						-1);
			} else {
				FontManager.baloo18.drawString(Client.CLIENT_name.substring(0, 1), 2, 1, getColor());
				FontManager.baloo18.drawString(Client.CLIENT_name.substring(1),
						3 + FontLoaders.tahoma18.getStringWidth(Client.CLIENT_name.substring(0, 1)), 1,
						-1);
			}

		}
	}

	public void renderTabgui() {

		int[] var12 = new int[1];
		byte var14;
		var14 = 0;
		byte var15 = 2;
		int var16 = (int) YPort + 2;

		if (this.logo_mode.isCurrentMode("New")) {
			if (time.isDelayComplete(2000L))
				RenderUtils.drawRect((float) var15, (float) var16, (float) (var15 + this.getWidestCategory() + 3),
						(float) (var16 + (this.categoryValues.size() - 1) * 11.7), new Color(40, 40, 40, 70).getRGB());
			else
				RenderUtils.drawRect((float) var15, (float) var16, (float) (var15 + this.getWidestCategory() + 3),
						(float) (var16 + (this.categoryValues.size() - 1) * 11.7), new Color(40, 40, 40, 130).getRGB());
		} else {
			RenderUtils.drawRect((float) var15, (float) var16, (float) (var15 + this.getWidestCategory() + 3),
					(float) (var16 + (this.categoryValues.size() - 1) * 11), Integer.MIN_VALUE);
		}

		for (Iterator var19 = this.categoryValues.iterator(); var19.hasNext(); ++var12[0]) {
			Category var17 = (Category) var19.next();
			if (var17 == Category.GLOBAL)
				continue;
			int var13;
			var13 = -1;

			if (this.getCurrentCategorry().equals(var17)) {
				if (this.logo_mode.isCurrentMode("Power"))
					RenderUtils.drawGradientSideways((float) var15 + 0.3, (float) var16 + 0.3,
							(float) (var15 + this.getWidestCategory() + 3) - 0.3, (float) (var16 + 9 + 2) - 0.3,
							ClientUtil.reAlpha(getColor(), 0.8F), ClientUtil.reAlpha(getColor(), 0.2F));
				if (this.logo_mode.isCurrentMode("ETB"))
					RenderUtils.drawGradientSideways((float) var15 + 0.3, (float) var16 + 0.3,
							(float) (var15 + this.getWidestCategory() + 3) - 0.3, (float) (var16 + 9 + 2) - 0.3,
							Colors.getColor(90, 169, 248), Colors.getColor(90, 169, 248));
				if (this.logo_mode.isCurrentMode("Customize"))
					RenderUtils.drawGradientSideways((float) var15 + 0.3, (float) var16 + 0.3,
							(float) (var15 + this.getWidestCategory() + 3) - 0.3, (float) (var16 + 9 + 2) - 0.3,
							new Color(0, 100, 242).getRGB(), Colors.getColor(0, 169, 255));
			}

			String var21 = var17.name();

			if (this.logo_mode.isCurrentMode("Power"))
				FontLoaders.tahoma18.drawString1(
						var21.substring(0, 1).toUpperCase() + var21.substring(1, var21.length()).toLowerCase(),
						(float) (var15 + 2), (float) ((float) var16 + (float) var14 * 1.5F + 1.5), var13);

			if (this.logo_mode.isCurrentMode("ETB"))
				fr.drawStringWithShadow(
						var21.substring(0, 1).toUpperCase() + var21.substring(1, var21.length()).toLowerCase(),
						(float) (var15 + 2), (float) ((float) var16 + (float) var14 * 1.5F + 1.5), var13);

			if (this.logo_mode.isCurrentMode("Customize")) {
				if (Shadow.getValueState())
					FontManager.baloo18.drawStringWithShadow(
							var21.substring(0, 1).toUpperCase() + var21.substring(1, var21.length()).toLowerCase(),
							(float) (var15 + 2), (float) ((float) var16 + (float) var14 - 1), var13);
				else
					FontManager.baloo18.drawString(
							var21.substring(0, 1).toUpperCase() + var21.substring(1, var21.length()).toLowerCase(),
							(float) (var15 + 2), (float) ((float) var16 + (float) var14 - 1), var13);

			}

			if (this.logo_mode.isCurrentMode("New"))
				FontManager.Volkswagen_Medium__.drawString(
						(this.getCurrentCategorry().equals(var17) ? "> " : "") + var21.substring(0, 1).toUpperCase()
								+ var21.substring(1, var21.length()).toLowerCase(),
						(float) (var15 + 2 + (this.getCurrentCategorry().equals(var17) ? 1 : 0)),
						(float) ((float) var16 + (float) var14 * 1.5F + 1.5), var13);

			var16 += 11;
		}

		if (this.screen == 1 || this.screen == 2) {
			int var18 = var15 + this.getWidestCategory() + 9;
			int var20 = 22 + this.currentCategoryIndex * 11;

			RenderUtils.drawRect((float) var18, (float) var20, (float) (var18 + this.getWidestMod() + 3),
					(float) (var20 + this.getModsForCurrentCategory().size() * 11), Integer.MIN_VALUE);

			for (Iterator var23 = this.getModsForCurrentCategory().iterator(); var23.hasNext(); var20 += 11) {
				Mod var22 = (Mod) var23.next();

				if (this.getCurrentModule().equals(var22)) {
					if (this.logo_mode.isCurrentMode("Power"))
						RenderUtils.drawGradientSideways((float) var18 + 0.3, (float) var20 + 0.3,
								(float) (var18 + this.getWidestMod() + 3) - 0.3, (float) (var20 + 9 + 2) - 0.3,
								ClientUtil.reAlpha(getColor(), 0.8F), ClientUtil.reAlpha(getColor(), 0.2F));
					if (this.logo_mode.isCurrentMode("ETB"))
						RenderUtils.drawGradientSideways((float) var18 + 0.3, (float) var20 + 0.3,
								(float) (var18 + this.getWidestMod() + 3) - 0.3, (float) (var20 + 9 + 2) - 0.3,
								Colors.getColor(90, 169, 248), Colors.getColor(90, 169, 248));
					if (this.logo_mode.isCurrentMode("Customize"))
						RenderUtils.drawGradientSideways((float) var18 + 0.3, (float) var20 + 0.3,
								(float) (var18 + this.getWidestMod() + 3) - 0.3, (float) (var20 + 9 + 2) - 0.3,
								new Color(0, 100, 242).getRGB(), Colors.getColor(0, 169, 255));

				}

				if (this.logo_mode.isCurrentMode("Power"))
					FontLoaders.tahoma18.drawString1(var22.getName(), (float) (var18 + 1),
							(float) var20 + (float) var14 * 1.5F + 1.5, var22.isEnabled() ? -1 : 11184810);// Color.GRAY.getRGB());

				if (this.logo_mode.isCurrentMode("ETB"))
					fr.drawStringWithShadow(var22.getName(), (float) (var18 + 2),
							(float) ((float) var20 + (float) var14 * 1.5F + 1.5), var22.isEnabled() ? -1 : 11184810);// Color.GRAY.getRGB());

				if (this.logo_mode.isCurrentMode("Customize")) {
					if (Shadow.getValueState())
						FontManager.baloo18.drawStringWithShadow(var22.getName(), (float) (var18 + 2),
								(float) ((float) var20 + (float) var14 - 1),
								var22.isEnabled() ? -1 : Color.GRAY.getRGB());
					else
						FontManager.baloo18.drawString(var22.getName(), (float) (var18 + 2),
								(float) ((float) var20 + (float) var14 - 1),
								var22.isEnabled() ? -1 : Color.GRAY.getRGB());

				}

				if (this.logo_mode.isCurrentMode("New"))
					FontLoaders.tahoma18.drawString1(
							(this.getCurrentModule().equals(var22) ? "> " : "") + var22.getName(), (float) (var18 + 1),
							(float) var20 + (float) var14 * 1.5F + 1.5
									+ (this.getCurrentModule().equals(var22) ? 1 : 0),
							var22.isEnabled() ? -1 : 11184810);// Color.GRAY.getRGB());

			}

		}

	}

	@EventTarget
	public void onKey(EventKeyboard e) {
		
		if (!this.TabGui.getValueState().booleanValue()) 
			return;
		
		switch (e.getKey()) {
		case Keyboard.KEY_UP:
			time.reset();
			this.up();
			break;
		case Keyboard.KEY_DOWN:
			time.reset();
			this.down();
			break;
		case Keyboard.KEY_RIGHT:
			time.reset();
			this.right(Keyboard.KEY_RIGHT);
			break;
		case Keyboard.KEY_LEFT:
			time.reset();
			this.left();
			break;
		case Keyboard.KEY_RETURN:
			time.reset();
			this.ok(Keyboard.KEY_RETURN);
			break;
		}
	}

	public void up() {
		if (this.currentCategoryIndex > 0 && this.screen == 0) {
			--this.currentCategoryIndex;
		} else if (this.currentCategoryIndex == 0 && this.screen == 0) {
			this.currentCategoryIndex = this.categoryValues.size() - 2;
		} else if (this.currentModIndex > 0 && this.screen == 1) {
			--this.currentModIndex;
		} else if (this.currentModIndex == 0 && this.screen == 1) {
			this.currentModIndex = this.getModsForCurrentCategory().size() - 1;
		} else if (this.currentSettingIndex > 0 && this.screen == 2) {
			--this.currentSettingIndex;
		}

	}

	public void down() {
		if (this.currentCategoryIndex < this.categoryValues.size() - 2 && this.screen == 0) {
			++this.currentCategoryIndex;
		} else if (this.currentCategoryIndex == this.categoryValues.size() - 2 && this.screen == 0) {
			this.currentCategoryIndex = 0;
		} else if (this.currentModIndex < this.getModsForCurrentCategory().size() - 1 && this.screen == 1) {
			++this.currentModIndex;
		} else if (this.currentModIndex == this.getModsForCurrentCategory().size() - 1 && this.screen == 1) {
			this.currentModIndex = 0;
		}

	}

	public void right(int var1) {
		if (this.screen == 0) {
			this.screen = 1;
		}
	}

	public void ok(int var1) {
		if (this.screen == 1 && this.getCurrentModule() != null) {
			this.getCurrentModule().toggle();
		}

	}

	public void left() {
		if (this.screen == 1) {
			this.screen = 0;
			this.currentModIndex = 0;
		} else if (this.screen == 2) {
			this.screen = 1;
			this.currentSettingIndex = 0;
		}

	}

	public Category getCurrentCategorry() {
		return (Category) this.categoryValues.get(this.currentCategoryIndex);
	}

	public Mod getCurrentModule() {
		return (Mod) this.getModsForCurrentCategory().get(this.currentModIndex);
	}

	public List<Mod> getModsForCurrentCategory() {
		
		
		Category var2 = this.getCurrentCategorry();
		
//		TreeMap<Float, Mod> modules = new TreeMap<Float, Mod>(Comparator.comparing(length -> -length));
//
//		.values().stream().forEach(mod -> {
//			
//			
//			float length = ModList.getStringWidth(mod.getName() + mod.getDisplayName());
//			
//			while(modules.containsKey(length))
//				length += 0.01f;
//			
//			modules.put( length , mod);
//
//		});
		
		yPos = 0;

		return ModManager.modList.values().stream().filter(module ->{
			
			
				return module.getCategory().equals(var2) && 
						module.getCategory() != Category.GLOBAL;
			
			
		}).collect(Collectors.toList());

	}

	public int getWidestCategory() {
		int var1 = 0;
		Iterator var3 = this.categoryValues.iterator();

		while (var3.hasNext()) {
			Category var2 = (Category) var3.next();
			if (var2 == Category.GLOBAL)
				continue;
			String var4 = var2.name();
			String p = ">";
			if (this.screen == 1 || this.screen == 2) {
				p = "<";
			} else {
				p = ">";
			}
			int var5 = FontLoaders.tahoma18.getStringWidth(
					var4.substring(0, 1).toUpperCase() + var4.substring(1, var4.length()).toLowerCase()) + 3;
			if (var5 > var1) {
				var1 = var5;
			}
		}

		return var1 + 2;
	}

	public int getWidestMod() {
		int var1 = 0;
		Iterator var3 = ModManager.modList.values().stream().iterator();

		while (var3.hasNext()) {
			Mod var2 = (Mod) var3.next();
			int var4 = FontLoaders.tahoma18.getStringWidth(var2.getName());
			if (var4 > var1) {
				var1 = var4;
			}
		}

		return var1;
	}

	public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos((double) (xCoord + 0.0F), (double) (yCoord + (float) maxV), (double) mc.ingameGUI.zLevel)
				.tex((double) ((float) (minU + 0) * f), (double) ((float) (minV + maxV) * f1)).endVertex();
		worldrenderer
				.pos((double) (xCoord + (float) maxU), (double) (yCoord + (float) maxV), (double) mc.ingameGUI.zLevel)
				.tex((double) ((float) (minU + maxU) * f), (double) ((float) (minV + maxV) * f1)).endVertex();
		worldrenderer.pos((double) (xCoord + (float) maxU), (double) (yCoord + 0.0F), (double) mc.ingameGUI.zLevel)
				.tex((double) ((float) (minU + maxU) * f), (double) ((float) (minV + 0) * f1)).endVertex();
		worldrenderer.pos((double) (xCoord + 0.0F), (double) (yCoord + 0.0F), (double) mc.ingameGUI.zLevel)
				.tex((double) ((float) (minU + 0) * f), (double) ((float) (minV + 0) * f1)).endVertex();
		tessellator.draw();
	}

//药水显示

	public void renderPotionStatus2(ScaledResolution sr) {
		if (this.Effect_mode.isCurrentMode("NONE"))
			return;
		if (!this.Effect_mode.isCurrentMode("Text")) {
			return;
		}
		GL11.glPushMatrix();

		FontRenderer font = mc.fontRendererObj;
		ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>();
		for (Object o : mc.thePlayer.getActivePotionEffects()) {
			potions.add((PotionEffect) o);
		}
		potions.sort(Comparator.comparingDouble(effect -> {

			String PType = "";
			if (effect.getAmplifier() == 1) {
				name = name + " II";
			} else if (effect.getAmplifier() == 2) {
				name = name + " III";
			} else if (effect.getAmplifier() == 3) {
				name = name + " IV";
			}

			String effectString = Potion.getDurationString(effect);

			if (effectString.length() < 5)
				effectString = "" + effectString;

			if (effect.getDuration() < 600 && effect.getDuration() > 300) {
				PType = PType + "\u00a76 " + effectString;
			} else if (effect.getDuration() < 300) {
				PType = PType + "\u00a7c " + effectString;
			} else if (effect.getDuration() > 600) {
				PType = PType + "\u00a77 " + effectString;
			}

			return -font.getStringWidth(
					I18n.format(Potion.potionTypes[effect.getPotionID()].getName(), new Object[0]) + PType);

		}));

		float pY = -20.0f;
		for (PotionEffect effect2 : potions) {
			Potion potion = Potion.potionTypes[effect2.getPotionID()];
			String name = I18n.format(potion.getName(), new Object[0]);
			String PType = "";
			if (effect2.getAmplifier() == 1) {
				name = name + " II";
			} else if (effect2.getAmplifier() == 2) {
				name = name + " III";
			} else if (effect2.getAmplifier() == 3) {
				name = name + " IV";
			}
			String effectString = Potion.getDurationString(effect2);

			if (effectString.length() < 5)
				effectString = "" + effectString;

			if (effect2.getDuration() < 600 && effect2.getDuration() > 300) {
				PType = PType + "\u00a76 " + effectString;
			} else if (effect2.getDuration() < 300) {
				PType = PType + "\u00a7c " + effectString;
			} else if (effect2.getDuration() > 600) {
				PType = PType + "\u00a77 " + effectString;
			}

			float x = (float) sr.getScaledWidth() - font.getStringWidth(name + PType);

			float y = (float) (sr.getScaledHeight() - 9);

			this.mc.getTextureManager().bindTexture(inv);

			if (potion.hasStatusIcon()) {
				final int var12 = potion.getStatusIconIndex();

				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

				GL11.glPushMatrix();
				GlStateManager.scale(0.5f, 0.5f, 0.5f);

				((Gui) this.mc.ingameGUI).drawTexturedModalRect(x * 2 - 68, (int) ((y - 12 + pY) * 2.0) + 24,
						0 + var12 % 8 * 18, 198 + var12 / 8 * 18, 18, 18);

				GL11.glPopMatrix();
			}

			Color c = new Color(potion.getLiquidColor());
			font.drawStringWithShadow(name, x - 9, y + pY, Colors.getColor(c.getRed(), c.getGreen(), c.getBlue()));
			font.drawStringWithShadow(PType, sr.getScaledWidth() - font.getStringWidth(PType) - 2, y + pY, -1);
			pY -= 12.5f;
		}

		GL11.glPopMatrix();
		;

	}

	public void renderPotionStatus(ScaledResolution sr) {

		if (this.Effect_mode.isCurrentMode("NONE"))
			return;
		if (!this.Effect_mode.isCurrentMode("Image")) {
			return;
		}
		final int var1 = sr.getScaledWidth() + 98;
		int var2 = sr.getScaledHeight() - 49;
		final int w = 182;
		final int h = 36;
		final boolean flippedVer = false;
		final boolean flippedHor = false;
		if (flippedVer) {
			var2 = var2 + h - 30;
		}
		final Collection var3 = this.mc.thePlayer.getActivePotionEffects();
		if (!var3.isEmpty()) {
			GlStateManager.color(1.0f, 1.0f, 1.0f, 0.9f);
			GL11.glDisable(2896);
			int var4 = 27;
			final int defaultEffectAmount = 182 / var4;
			if (var3.size() > defaultEffectAmount) {
				var4 = 182 / var3.size();
			}
			final int totalSize = var3.size() * var4;
			int currentX = var1 - w / 2 - totalSize / 2 - (var4 - 18) / 2;
			final int currentY = var2 + 7;
			for (final PotionEffect var10 : this.mc.thePlayer.getActivePotionEffects()) {
				if (var10.getEffectName().contains("night"))
					continue;
				final int duration2 = var10.getDuration();
				// GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				if (duration2 >= 300) {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 0.75f);
				} else if (duration2 >= 150) {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 0.4f);
				} else {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 0.2f);
				}

				final Potion var11 = Potion.potionTypes[var10.getPotionID()];
				this.mc.getTextureManager().bindTexture(inv);
				if (var11.hasStatusIcon()) {
					final int var12 = var11.getStatusIconIndex();
					((Gui) this.mc.ingameGUI).drawTexturedModalRect(currentX, currentY, 0 + var12 % 8 * 18,
							198 + var12 / 8 * 18, 18, 18);
				}
				final String var13 = Potion.getDurationString(var10);
				this.mc.fontRendererObj.drawStringWithShadow(var13,
						(float) (currentX + 10 - this.mc.fontRendererObj.getStringWidth(var13) / 2),
						(float) (currentY + 15), 16777214);
				currentX -= var4;
			}

		}
	}

	public void renderPotionStatus1(ScaledResolution sr) {
		if (this.Effect_mode.isCurrentMode("NONE")) {
			return;
		}
		if (!this.Effect_mode.isCurrentMode("Picture Text")) {
			return;
		}
		final int var1 = sr.getScaledWidth() + 98;
		int var2 = sr.getScaledHeight() - 49;
		final int w = 182;
		final int h = 36;
		final boolean flippedVer = false;
		final boolean flippedHor = false;
		if (flippedVer) {
			var2 = var2 + h - 30;
		}
		final Collection collection = Minecraft.thePlayer.getActivePotionEffects();

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableLighting();
		int k = 25;
		if (collection.size() > 5) {
			k = 132 / (collection.size() - 1);
		}
		int i = 0;
		int j = sr.getScaledHeight() / 2 - 60;
		for (final Object object : collection) {
			final PotionEffect potioneffect = (PotionEffect) object;
			if (potioneffect.getEffectName().startsWith("potion.night")) {

				if (collection.size() == 6) {
					k = 25;
				}
				continue;
			}

			if (true) {
				final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
				boolean flag = (potioneffect.getDuration() / 20 <= 6 && System.currentTimeMillis() % 2000L > 1000L);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				mc.getTextureManager().bindTexture(inv);
				mc.fontRendererObj.drawString("", 0, 0, 1118481);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

				if (potion.hasStatusIcon()) {
					final int l = potion.getStatusIconIndex();
					GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
					mc.ingameGUI.drawTexturedModalRect(i + 6, j + 7, 0 + l % 8 * 18, 198 + l / 8 * 18, 18, 18);
				}
				String s1 = I18n.format(potion.getName(), new Object[0]);
				if (potioneffect.getAmplifier() == 1) {
					s1 = String.valueOf(s1) + " " + I18n.format("enchantment.level.2", new Object[0]);
				} else if (potioneffect.getAmplifier() == 2) {
					s1 = String.valueOf(s1) + " " + I18n.format("enchantment.level.3", new Object[0]);
				} else if (potioneffect.getAmplifier() == 3) {
					s1 = String.valueOf(s1) + " " + I18n.format("enchantment.level.4", new Object[0]);
				}
				mc.fontRendererObj.drawStringWithShadow(s1, i + 10 + 18, j + 6,
						new Color(211, flag ? 0 : 211, flag ? 0 : 211, 0).getRGB());
				final String s2 = Potion.getDurationString(potioneffect);
				mc.fontRendererObj.drawStringWithShadow(s2, i + 10 + 18, j + 6 + 10,
						new Color(211, flag ? 0 : 211, flag ? 0 : 211, 0).getRGB());
			}
			j += k;
		}
		GL11.glDisable(2896);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

	}

	// 盔甲显示
	private void renderStuffStatus(ScaledResolution sr) {
		GL11.glPushMatrix();
		ArrayList<ItemStack> stuff = new ArrayList<ItemStack>();
		boolean onwater = mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water);
		int x = sr.getScaledWidth() / 2 - 6;
		int y = sr.getScaledHeight();

		for (int index = 3; index >= 0; --index) {
			ItemStack armer = this.mc.thePlayer.inventory.armorInventory[index];
			if (armer == null)
				continue;
			stuff.add(armer);
		}
		if (this.mc.thePlayer.getCurrentEquippedItem() != null) {
			stuff.add(this.mc.thePlayer.getCurrentEquippedItem());
		}
		for (ItemStack errything : stuff) {
			if (this.mc.theWorld != null) {
				RenderHelper.enableGUIStandardItemLighting();
				x += 16;

				mc.getRenderItem().renderItemAndEffectIntoGUI(errything, x,
						y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
				mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, errything, x,
						y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));

				RenderHelper.disableStandardItemLighting();
				int y1 = 1;
				if (errything != null) {
					int sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, errything);
					int fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, errything);
					int kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, errything);
					if (sLevel > 0) {
						drawEnchantTags("Sh" + getColor(sLevel) + sLevel, x,
								y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
						y1 += 4;
					}
					if (fLevel > 0) {
						drawEnchantTags("Fir" + getColor(fLevel) + fLevel, x,
								y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
						y1 += 4;
					}
					if (kLevel > 0) {
						drawEnchantTags("Kb" + getColor(kLevel) + kLevel, x,
								y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));

					} else if ((errything.getItem() instanceof ItemBook)) {

					} else if ((errything.getItem() instanceof ItemTool)) {
						int eLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, errything);
						int FLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, errything);
						int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, errything);
						int tepLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, errything);
						if (eLevel > 0) {
							drawEnchantTags("Ef" + getColor(eLevel) + eLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (FLevel > 0) {
							drawEnchantTags("For" + getColor(FLevel) + FLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (tepLevel > 0) {
							drawEnchantTags("Tou" + getColor(tepLevel) + tepLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (uLevel > 0) {
							drawEnchantTags("Unb" + getColor(uLevel) + uLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
						}

					} else if ((errything.getItem() instanceof ItemArmor)) {
						int pLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, errything);
						int tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, errything);
						int FLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId,
								errything);
						int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, errything);
						int DepLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.depthStrider.effectId,
								errything);
						if (pLevel > 0) {
							drawEnchantTags("Pr" + getColor(pLevel) + pLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (tLevel > 0) {
							drawEnchantTags("Th" + getColor(tLevel) + tLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (DepLevel > 0) {
							drawEnchantTags("Dep" + getColor(DepLevel) + DepLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (FLevel > 0) {
							drawEnchantTags("Fea" + getColor(FLevel) + FLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (uLevel > 0) {
							drawEnchantTags("Unb" + getColor(uLevel) + uLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
						}
					} else if ((errything.getItem() instanceof ItemBow)) {
						int powLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, errything);
						int punLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, errything);
						int fireLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, errything);
						int infinityLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId,
								errything);
						if (powLevel > 0) {
							drawEnchantTags("Pow" + getColor(powLevel) + powLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (punLevel > 0) {
							drawEnchantTags("Pun" + getColor(punLevel) + punLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (fireLevel > 0) {
							drawEnchantTags("Fla" + getColor(fireLevel) + fireLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
							y1 += 4;
						}
						if (infinityLevel > 0) {
							drawEnchantTags("Inf" + getColor(infinityLevel) + infinityLevel, x,
									y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
						}
					} else if (errything.getRarity() == EnumRarity.EPIC) {
						drawEnchantTags("\2476\247nGod", x,
								y1 + y - (onwater ? 65 : this.mc.playerController.shouldDrawHUD() ? 55 : 40));
					}
				}
			}

		}
		GL11.glPopMatrix();

	}

	private static void drawEnchantTag(String text, int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		x = (int) (x * 2);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x, y * 2, Colors.getColor(255));
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	private static void drawEnchantTags(String Enchant, int x, int y) {
		String Enchants = Enchant;
		String[] LIST = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f",
				"m", "o", "r", "g" };
		for (String str : LIST) {
			Enchant = Enchant.replaceAll("§" + str, "");
		}
		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		x = (int) (x * 2);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x + 1, y * 2, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x - 1, y * 2, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x, y * 2 + 1, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchant, x, y * 2 - 1, 0);
		Minecraft.getMinecraft().fontRendererObj.drawString(Enchants, x, y * 2, Colors.getColor(255));
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	private String getColor(int level) {
		if (level == 1) {

		} else if (level == 2) {
			return "\247a";
		} else if (level == 3) {
			return "\2473";
		} else if (level == 4) {
			return "\2474";
		} else if (level >= 5) {
			return "\2476";
		}
		return "\247b";
	}

	private void renderHealth(ScaledResolution sr) {

		int width = sr.getScaledWidth() / 2;
		int height = sr.getScaledHeight() / 2;
		String XD = "" + (int) mc.thePlayer.getHealth();
		int XDDD = mc.fontRendererObj.getStringWidth(XD);
		float health = mc.thePlayer.getHealth();
		if (health > 20.0f) {
			health = 20.0f;
		}
		int red = (int) Math.abs(health * 5.0f * 0.01f * 0.0f + (1.0f - health * 5.0f * 0.01f) * 255.0f);
		int green = (int) Math.abs(health * 5.0f * 0.01f * 255.0f + (1.0f - health * 5.0f * 0.01f) * 0.0f);
		Color customColor = new Color(red, green, 0).brighter();
		mc.fontRendererObj.drawStringWithShadow(XD, (-XDDD) / 2 + width, height - 17, customColor.getRGB());
	}

//分割线
	public void drawFullCircle(int cx, int cy, double r, int c) {

		r *= 2.0;
		cx *= 2;
		cy *= 2;
		float f = (float) (c >> 24 & 255) / 255.0f;
		float f1 = (float) (c >> 16 & 255) / 255.0f;
		float f2 = (float) (c >> 8 & 255) / 255.0f;
		float f3 = (float) (c & 255) / 255.0f;
		RenderUtils.R2DUtils.enableGL2D();
		GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
		GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
		GL11.glBegin((int) 6);
		int i = 0;
		while (i <= 360) {
			double x = Math.sin((double) ((double) i * 3.141592653589793 / 180.0)) * r;
			double y = Math.cos((double) ((double) i * 3.141592653589793 / 180.0)) * r;
			GL11.glVertex2d((double) ((double) cx + x), (double) ((double) cy + y));
			++i;
		}
		GL11.glEnd();
		GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
		RenderUtils.R2DUtils.disableGL2D();
	}

	public void drawArc(float cx, float cy, double r, int c, int startpoint, double arc, int linewidth) {

		r *= 2.0;
		cx *= 2.0f;
		cy *= 2.0f;
		float f = (float) (c >> 24 & 255) / 255.0f;
		float f1 = (float) (c >> 16 & 255) / 255.0f;
		float f2 = (float) (c >> 8 & 255) / 255.0f;
		float f3 = (float) (c & 255) / 255.0f;
		RenderUtils.R2DUtils.enableGL2D();
		GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
		GL11.glLineWidth((float) linewidth);
		GL11.glEnable((int) 2848);
		GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
		GL11.glBegin((int) 3);
		int i = startpoint;
		while ((double) i <= arc) {
			double x = Math.sin((double) ((double) i * 3.141592653589793 / 180.0)) * r;
			double y = Math.cos((double) ((double) i * 3.141592653589793 / 180.0)) * r;
			GL11.glVertex2d((double) ((double) cx + x), (double) ((double) cy + y));
			++i;
		}
		GL11.glEnd();
		GL11.glDisable((int) 2848);
		GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
		RenderUtils.R2DUtils.disableGL2D();
	}

	public int transitionTo(int from, int to) {
		int i;
		if (from < to && Minecraft.getDebugFPS() >= 60) {
			i = 0;
			while (i < 3) {
				++from;
				++i;
			}
		}
		if (from > to && Minecraft.getDebugFPS() >= 60) {
			i = 0;
			while (i < 3) {
				--from;
				++i;
			}
		}
		if (from < to && Minecraft.getDebugFPS() >= 40 && Minecraft.getDebugFPS() <= 59) {
			i = 0;
			while (i < 4) {
				++from;
				++i;
			}
		}
		if (from > to && Minecraft.getDebugFPS() >= 40 && Minecraft.getDebugFPS() <= 59) {
			i = 0;
			while (i < 4) {
				--from;
				++i;
			}
		}
		if (from < to && Minecraft.getDebugFPS() >= 0 && Minecraft.getDebugFPS() <= 39) {
			i = 0;
			while (i < 6) {
				++from;
				++i;
			}
		}
		if (from > to && Minecraft.getDebugFPS() >= 0 && Minecraft.getDebugFPS() <= 39) {
			i = 0;
			while (i < 6) {
				--from;
				++i;
			}
		}
		return from;
	}

	public enum Direction {
		S("S", 0), SW("SW", 1), W("W", 2), NW("NW", 3), N("N", 4), NE("NE", 5), E("E", 6), SE("SE", 7);

		private Direction(final String s, final int n) {

		}
	}

	private int getRainbow(int speed, int offset) {
		float hue = (System.currentTimeMillis() + offset) % speed;
		hue /= speed;
		return Color.getHSBColor(hue, 0.7f, 1f).getRGB();

	}

	public static int getRainbow(int speed) {
		double hue = Math.ceil((double) (System.currentTimeMillis() + (long) speed) / 20.0D);
		hue %= 360.0D;
		return Color.getHSBColor((float) (hue / 360.0D), 0.8F, 0.9F).getRGB();
	}

	public static Color rainbow(long time, float count, float fade) {
		float hue = ((float) time + (10.0E-10F + count) * 4.0E8F) / (8.75f * 0.2E10F) * 3;
		long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 0.5F, 1F)).intValue()), 16);
		Color c = new Color((int) color);
		return new Color((float) c.getRed() / 255.0F * fade, (float) c.getGreen() / 255.0F * fade,
				(float) c.getBlue() / 255.0F * fade, (float) c.getAlpha() / 255.0F);
	}

	private float hue = 1.0F;
	
	//Draw mod list
	private double yPos;

	private Mod getNextEnabledModule(final List<Mod> modules, final int startingIndex) {
		for (int modulesSize = modules.size(), i = startingIndex; i < modulesSize; ++i) {
			final Mod module = modules.get(i);
			if (module.isEnabled()) {
				return module;
			}
		}
		return null;
	}

	public static CFontRenderer ModList;

	public void drawModList(final ScaledResolution sr) {
//		if (ModList != FontManager.baloo18 && Array_Font.isCurrentMode("Baloo")) {
//			ModList = FontManager.baloo18;
//		} else if (ModList != FontManager.sw18 && Array_Font.isCurrentMode("ArialBold")) {
//			ModList = FontManager.sw18;
//		}

		ModList = FontLoaders.tahoma18;

		final double screenWidth = sr.getScaledWidth();

//		if(mc.thePlayer != null && mc.thePlayer.ticksExisted < 10)
//			ModManager.modList_sorted.sort(Comparator
//				.comparingDouble(module -> -Hud.ModList.getStringWidth(module.getDisplayName().isEmpty() ? module.getName()
//						: String.format("%s%s", module.getName(), module.getDisplayName()))));
//
//		
//		ArrayList<Mod> modules = ModManager.modList_sorted;


		
		
		TreeMap<Float, Mod> modules = new TreeMap<Float, Mod>(Comparator.comparing(length -> -length));

		ModManager.modList.values().stream().forEach(mod -> {
			
			
			float length = ModList.getStringWidth(mod.getName() + mod.getDisplayName());
			
			while(modules.containsKey(length))
				length += 0.01f;
			
			modules.put( length , mod);

		});


		
		yPos = 4.5;
		
		for (Iterator<Entry<Float, Mod>> i = modules.entrySet().iterator(); i.hasNext(); ) { 
		   Entry<Float, Mod> data = i.next(); 
		    
		    Mod module = (Mod) data.getValue();
			

			if ((module != ModManager.getModByClass(ClickGui.class)) && !module.HideMod) {

				final String moduleLabel = module.getDisplayName().isEmpty() ? module.getName()
						: String.format("%s%s", module.getName(), module.getDisplayName());
				final double moduleWidth = ModList
						.getStringWidth(module.getDisplayName().isEmpty() ? module.getName()
								: String.format("%s%s", module.getName(), module.getDisplayName()))
						+ 2 + (this.Array_mode.isCurrentMode("Normal") ? -1 : 0);
				final double moduleHeight = 8 - (Array_Font.isCurrentMode("Baloo") ? 3.76 : 3.76);
				if (module.isEnabled()) {
					module.setAnimX(moduleWidth - animUtil.easeOut(module.getModuleProgressionX(), 0, moduleWidth, .3));
					module.setAnimY(
							moduleHeight - animUtil.easeOut(module.getModuleProgressionY(), 0, moduleHeight + 6.7, .3));
				} else {
					module.setAnimX(animUtil.easeOut(module.getModuleProgressionX(), 0, moduleWidth, .3));
					module.setAnimY(animUtil.easeOut(module.getModuleProgressionY(), 0, moduleHeight + 6.7, .3));
				}

				module.setAnimX(MathHelper.clamp_double(module.getAnimX(), 0, moduleWidth));
				module.setAnimY(MathHelper.clamp_double(module.getAnimY(), 0, moduleHeight + 6.7));

				if (module.isEnabled() || (module.getAnimX() != 0 && module.getAnimX() != moduleWidth)) {
					int color = Client.Rainbow.getValueState()
							? this.OldRainBow.getValueState() ? RenderUtil.rainbow((int) (yPos * (this.oldrainbow.getValueState().intValue()))) : astofloc((int) (yPos * rainbow_Step.getValueState().intValue()))
							:
					// Palette
					// .fade(
					new Color(Client.r.getValueState().intValue(), Client.g.getValueState().intValue(),
							Client.b.getValueState().intValue(), Client.alpha.getValueState().intValue())// , 1, /*
																											// modules.indexOf(module)
																											// */ i
																											// * 2 +
																											// 10)
									.getRGB();
					if (bg.getValueState().intValue() != 0)
						RenderUtil.drawRect(screenWidth - moduleWidth + module.getAnimX() - 2, yPos - 2.7,
								screenWidth + moduleWidth + module.getAnimX(), yPos + moduleHeight + 4.3,
								new Color(0, 0, 0, bg.getValueState().intValue()).getRGB());
					if (this.Array_mode.isCurrentMode("Side") || this.Array_mode.isCurrentMode("All"))
						RenderUtil.drawRect(screenWidth + module.getAnimX() - 1, yPos, screenWidth + module.getAnimX(),
								yPos + moduleHeight, color);

					final double offsetX = screenWidth < sr.getScaledWidth() / 2 ? 2 : 0;
//					if (Shadow.getValueState())
						ModList.drawStringWithShadow(moduleLabel,
								(float) (screenWidth - moduleWidth + module.getAnimX() + offsetX),
								(float) yPos - (Array_Font.isCurrentMode("Baloo") ? 1 : 0), color);
//					else
//						ModList.drawString(moduleLabel,
//								(float) (screenWidth - moduleWidth + module.getAnimX() + offsetX),
//								(float) yPos - (Array_Font.isCurrentMode("Baloo") ? 1 : 0), color);
					
					yPos += moduleHeight - module.getAnimY() + 2.3 + ArraySpacing.getValueState().doubleValue();
				}
			}
		}

//		.stream().forEachOrdered(en -> {
//			Mod module = (Mod) en.getValue();
//	
//
//			if ((module != ModManager.getModByClass(ClickGui.class)) && !module.HideMod) {
//
//				final String moduleLabel = module.getDisplayName().isEmpty() ? module.getName()
//						: String.format("%s%s", module.getName(), module.getDisplayName());
//				final double moduleWidth = ModList
//						.getStringWidth(module.getDisplayName().isEmpty() ? module.getName()
//								: String.format("%s%s", module.getName(), module.getDisplayName()))
//						+ 2 + (this.Array_mode.isCurrentMode("Normal") ? -1 : 0);
//				final double moduleHeight = 8 - (Array_Font.isCurrentMode("Baloo") ? 3.76 : 3.76);
//				if (module.isEnabled()) {
//					module.setAnimX(moduleWidth - animUtil.easeOut(module.getModuleProgressionX(), 0, moduleWidth, .3));
//					module.setAnimY(
//							moduleHeight - animUtil.easeOut(module.getModuleProgressionY(), 0, moduleHeight + 6.7, .3));
//				} else {
//					module.setAnimX(animUtil.easeOut(module.getModuleProgressionX(), 0, moduleWidth, .3));
//					module.setAnimY(animUtil.easeOut(module.getModuleProgressionY(), 0, moduleHeight + 6.7, .3));
//				}
//
//				module.setAnimX(MathHelper.clamp_double(module.getAnimX(), 0, moduleWidth));
//				module.setAnimY(MathHelper.clamp_double(module.getAnimY(), 0, moduleHeight + 6.7));
//
//				if (module.isEnabled() || (module.getAnimX() != 0 && module.getAnimX() != moduleWidth)) {
//					int color = Client.Rainbow.getValueState()
//							? this.OldRainBow.getValueState() ? RenderUtil.rainbow((int) (yPos * (this.oldrainbow.getValueState().intValue()))) : astofloc((int) (yPos * rainbow_Step.getValueState().intValue()))
//							:
//					// Palette
//					// .fade(
//					new Color(Client.r.getValueState().intValue(), Client.g.getValueState().intValue(),
//							Client.b.getValueState().intValue(), Client.alpha.getValueState().intValue())// , 1, /*
//																											// modules.indexOf(module)
//																											// */ i
//																											// * 2 +
//																											// 10)
//									.getRGB();
//					if (bg.getValueState().intValue() != 0)
//						RenderUtil.drawRect(screenWidth - moduleWidth + module.getAnimX() - 2, yPos - 2.7,
//								screenWidth + moduleWidth + module.getAnimX(), yPos + moduleHeight + 4.3,
//								new Color(0, 0, 0, bg.getValueState().intValue()).getRGB());
//					if (this.Array_mode.isCurrentMode("Side") || this.Array_mode.isCurrentMode("All"))
//						RenderUtil.drawRect(screenWidth + module.getAnimX() - 1, yPos, screenWidth + module.getAnimX(),
//								yPos + moduleHeight, color);
//
//					final double offsetX = screenWidth < sr.getScaledWidth() / 2 ? 2 : 0;
////					if (Shadow.getValueState())
//						ModList.drawStringWithShadow(moduleLabel,
//								(float) (screenWidth - moduleWidth + module.getAnimX() + offsetX),
//								(float) yPos - (Array_Font.isCurrentMode("Baloo") ? 1 : 0), color);
////					else
////						ModList.drawString(moduleLabel,
////								(float) (screenWidth - moduleWidth + module.getAnimX() + offsetX),
////								(float) yPos - (Array_Font.isCurrentMode("Baloo") ? 1 : 0), color);
//					
//					yPos += moduleHeight - module.getAnimY() + 2.3 + ArraySpacing.getValueState().doubleValue();
//				}
//			}
//		});
		
	}
	
    public static int astofloc(int delay) {
	    float speed = rainbow.getValueState().floatValue();
	    float hue = (float)(System.currentTimeMillis() % (int)speed) + (delay / 2);
	    while (hue > speed)
	      hue -= speed; 
	    hue /= speed;
	    if (hue > 0.5D)
	      hue = 0.5F - hue - 0.5F; 
	    hue += 0.5F;
	    return Color.HSBtoRGB(hue, rainbow_Satu.getValueState().floatValue(), rainbow_Bright.getValueState().floatValue());
    }

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}
		if (top < bottom) {
			double j = top;
			top = bottom;
			bottom = j;
		}
		float f3 = (float) (color >> 24 & 0xFF) / 255.0f;
		float f = (float) (color >> 16 & 0xFF) / 255.0f;
		float f1 = (float) (color >> 8 & 0xFF) / 255.0f;
		float f2 = (float) (color & 0xFF) / 255.0f;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate((int) 770, (int) 771, (int) 1, (int) 0);
		GlStateManager.color((float) f, (float) f1, (float) f2, (float) f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0).endVertex();
		worldrenderer.pos(right, bottom, 0.0).endVertex();
		worldrenderer.pos(right, top, 0.0).endVertex();
		worldrenderer.pos(left, top, 0.0).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

}
