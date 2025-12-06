package net.minecraft.client.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

import com.google.common.collect.Lists;

import cn.Power.Client;
import cn.Power.Font.FontManager;
import cn.Power.ui.Gui.GuiChangelog;
import cn.Power.ui.login.GuiAltManager;
import cn.Power.ui.particles.ParticleEngine;
import cn.Power.util.ClientUtil;
import cn.Power.util.Colors;
import cn.Power.util.Colors2;
import cn.Power.util.RenderUtil;
import cn.Power.util.RenderUtils;
import cn.Power.util.animations.AnimationUtil;
import cn.Power.util.animations.easings.Elastic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import net.optifine.reflect.Reflector;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
	private static final AtomicInteger field_175373_f = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	private static final Random RANDOM = new Random();

	/** Counts the number of screen updates. */
	private float updateCounter;

	/** The splash message. */
	private String splashText;
	private GuiButton buttonResetDemo;
	/** Timer used to rotate the panorama, increases every tick. */
	private int panoramaTimer;

	/**
	 * Texture allocated for the current viewport of the main menu's panorama
	 * background.
	 */
	private DynamicTexture viewportTexture;
	private boolean field_175375_v = true;

	/**
	 * The Object object utilized as a thread lock when performing non thread-safe
	 * operations
	 */
	private final Object threadLock = new Object();

	/** OpenGL graphics card warning. */
	private String openGLWarning1;

	/** OpenGL graphics card warning. */
	private String openGLWarning2;

	/** Link to the Mojang Support about minimum requirements */
	private String openGLWarningLink;
	private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
	private static final ResourceLocation minecraftTitleTextures = new ResourceLocation(
			"textures/gui/title/minecraft.png");

	/** An array of all the paths to the panorama pictures. */
	private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {
			new ResourceLocation("textures/gui/title/background/panorama_0.png"),
			new ResourceLocation("textures/gui/title/background/panorama_1.png"),
			new ResourceLocation("textures/gui/title/background/panorama_2.png"),
			new ResourceLocation("textures/gui/title/background/panorama_3.png"),
			new ResourceLocation("textures/gui/title/background/panorama_4.png"),
			new ResourceLocation("textures/gui/title/background/panorama_5.png") };
	public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here"
			+ EnumChatFormatting.RESET + " for more information.";
	private int field_92024_r;
	private int field_92023_s;
	private int field_92022_t;
	private int field_92021_u;
	private int field_92020_v;
	private int field_92019_w;
	private ResourceLocation backgroundTexture;

	/** Minecraft Realms button. */
	private GuiButton realmsButton;
	private boolean field_183502_L;
	private GuiScreen field_183503_M;
	private GuiButton modButton;
	private GuiScreen modUpdateNotification;

	private GuiButton field_146605_t;

	public GuiMainMenu() {
		this.openGLWarning2 = field_96138_a;
		this.field_183502_L = false;
		this.splashText = "missingno";
		BufferedReader bufferedreader = null;

		try {
			List<String> list = Lists.newArrayList();
			bufferedreader = new BufferedReader(new InputStreamReader(
					Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(),
					Charsets.UTF_8));
			String s;

			while ((s = bufferedreader.readLine()) != null) {
				s = s.trim();

				if (!s.isEmpty()) {
					list.add(s);
				}
			}

			if (!list.isEmpty()) {
				while (true) {
					this.splashText = list.get(RANDOM.nextInt(list.size()));

					if (this.splashText.hashCode() != 125780783) {
						break;
					}
				}
			}
		} catch (IOException var12) {
			;
		} finally {
			if (bufferedreader != null) {
				try {
					bufferedreader.close();
				} catch (IOException var111) {
					;
				}
			}
		}

		this.updateCounter = RANDOM.nextFloat();
		this.openGLWarning1 = "";

		if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
			this.openGLWarning1 = I18n.format("title.oldgl1");
			this.openGLWarning2 = I18n.format("title.oldgl2");
			this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
		}
	}

	private boolean func_183501_a() {
		return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS)
				&& this.field_183503_M != null;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		++this.panoramaTimer;

		if (this.func_183501_a()) {
			this.field_183503_M.updateScreen();
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */

	private int field_193978_M;
	private int field_193979_N;

	Random random = new Random();

	public ArrayList<Buton> buttons = new ArrayList<Buton>();
	public ParticleEngine pe = new ParticleEngine();
	
	public void initGui() {
		pe.particles.clear();
		this.buttons.clear();
		this.buttons.add(new Buton(new GuiSelectWorld(this), "Single Player"));
		this.buttons.add(new Buton(new GuiMultiplayer(this), "Multi Player"));
		this.buttons.add(new Buton(new GuiAltManager(this), "Alt Manager"));
		this.buttons.add(new Buton(new GuiChangelog(this), "Change Log"));
		this.buttons.add(new Buton(new GuiOptions(this, this.mc.gameSettings), "Options"));
		this.buttons.add(new Buton(null, "Shutdown"));
		ScaledResolution resolution = new ScaledResolution(this.mc);
		// this.field_193978_M = this.fontRendererObj.getStringWidth(GGMZ);
		this.field_193979_N = this.width - this.field_193978_M - 2;
		int p = 0;

		this.viewportTexture = new DynamicTexture(256, 256);
		this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background",
				this.viewportTexture);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		int i = 24;
		int j = this.height / 4 + 48;

		if (this.mc.isDemo()) {
			this.addDemoButtons(j, 24);
		} else {
			// this.addSingleplayerMultiplayerButtons(j, 24);
		}
//
//        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
//        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));
//        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));

		this.buttonList.add(this.field_146605_t = new GuiButton(998, this.width - 60, this.height - 20, 60, 20,
				Minecraft.getMinecraft().gameSettings.getKeyBinding(GameSettings.Options.BG_ENABLED)));

		synchronized (this.threadLock) {
			this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
			this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
			int k = Math.max(this.field_92023_s, this.field_92024_r);
			this.field_92022_t = (this.width - k) / 2;
			this.field_92021_u = (this.buttonList.get(0)).yPosition - 24;
			this.field_92020_v = this.field_92022_t + k;
			this.field_92019_w = this.field_92021_u + 24;
		}

		this.mc.setConnectedToRealms(false);

		if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS)
				&& !this.field_183502_L) {
			RealmsBridge realmsbridge = new RealmsBridge();
			this.field_183503_M = realmsbridge.getNotificationScreen(this);
			this.field_183502_L = true;
		}

		if (this.func_183501_a()) {
			this.field_183503_M.setGuiSize(this.width, this.height);
			this.field_183503_M.initGui();
		}
		this.animUtil = new AnimationUtil(Elastic.class);
	}

	/**
	 * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have
	 * bought the game.
	 */
	private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
		this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
		this.buttonList.add(
				new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer")));

//		if (Reflector.GuiModList_Constructor.exists()) {
//			this.buttonList.add(this.realmsButton = new GuiButton(14, this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2,
//					98, 20, I18n.format("menu.online").replace("Minecraft", "").trim()));
//			this.buttonList.add(this.modButton = new GuiButton(6, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98,
//					20, I18n.format("fml.menu.mods")));
//		} else {
			this.buttonList.add(this.realmsButton = new GuiButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2,
					I18n.format("menu.online")));
//		}
	}

	/**
	 * Adds Demo buttons on Main Menu for players who are playing Demo.
	 */
	private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
		this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo")));
		this.buttonList.add(this.buttonResetDemo = new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1,
				I18n.format("menu.resetdemo")));
		ISaveFormat isaveformat = this.mc.getSaveLoader();
		WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

		if (worldinfo == null) {
			this.buttonResetDemo.enabled = false;
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		}

		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (button.id == 14 && this.realmsButton.visible) {
			this.switchToRealms();
		}

		if (button.id == 4) {
			this.mc.shutdown();
		}

		if (button.id == 11) {
			this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
		}

		if (button.id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

			if (worldinfo != null) {
				GuiYesNo guiyesno = GuiSelectWorld.makeDeleteWorldYesNo(this, worldinfo.getWorldName(), 12);
				this.mc.displayGuiScreen(guiyesno);
			}
		}
		if (button.id == 998) {
			Minecraft.getMinecraft().gameSettings.setOptionValue(GameSettings.Options.BG_ENABLED, 1);
			this.field_146605_t.displayString = Minecraft.getMinecraft().gameSettings
					.getKeyBinding(GameSettings.Options.BG_ENABLED);
		}
	}

	private void switchToRealms() {
		RealmsBridge realmsbridge = new RealmsBridge();
		realmsbridge.switchToRealms(this);
	}

	public void confirmClicked(boolean result, int id) {
		if (result && id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			isaveformat.flushCache();
			isaveformat.deleteWorldDirectory("Demo_World");
			this.mc.displayGuiScreen(this);
		} else if (id == 13) {
			if (result) {
				try {
					Class<?> oclass = Class.forName("java.awt.Desktop");
					Object object = oclass.getMethod("getDesktop").invoke((Object) null);
					oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
				} catch (Throwable throwable1) {
					logger.error("Couldn't open link", throwable1);
				}
			}

			this.mc.displayGuiScreen(this);
		}
	}

	/**
	 * Draws the main menu panorama
	 */
	private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		int i = 8;
		int j = 64;
		CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

		if (custompanoramaproperties != null) {
			j = custompanoramaproperties.getBlur1();
		}

		for (int k = 0; k < j; ++k) {
			GlStateManager.pushMatrix();
			float f = ((float) (k % i) / (float) i - 0.5F) / 64.0F;
			float f1 = ((float) (k / i) / (float) i - 0.5F) / 64.0F;
			float f2 = 0.0F;
			GlStateManager.translate(f, f1, f2);
			GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F,
					1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-((float) this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int l = 0; l < 6; ++l) {
				GlStateManager.pushMatrix();

				if (l == 1) {
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 3) {
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 4) {
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (l == 5) {
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				ResourceLocation[] aresourcelocation = titlePanoramaPaths;

				if (custompanoramaproperties != null) {
					aresourcelocation = custompanoramaproperties.getPanoramaLocations();
				}

				this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				int i1 = 255 / (k + 1);
				float f3 = 0.0F;
				worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, i1).endVertex();
				worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, i1).endVertex();
				worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, i1).endVertex();
				worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, i1).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
	}

	/**
	 * Rotate and blurs the skybox view in the main menu
	 */
	private void rotateAndBlurSkybox(float p_73968_1_) {
		this.mc.getTextureManager().bindTexture(this.backgroundTexture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.disableAlpha();
		int i = 3;
		int j = 3;
		CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

		if (custompanoramaproperties != null) {
			j = custompanoramaproperties.getBlur2();
		}

		for (int k = 0; k < j; ++k) {
			float f = 1.0F / (float) (k + 1);
			int l = this.width;
			int i1 = this.height;
			float f1 = (float) (k - i / 2) / 256.0F;
			worldrenderer.pos((double) l, (double) i1, (double) this.zLevel).tex((double) (0.0F + f1), 1.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos((double) l, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 1.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 0.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos(0.0D, (double) i1, (double) this.zLevel).tex((double) (0.0F + f1), 0.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
		}

		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	/**
	 * Renders the skybox in the main menu
	 */
	private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
		this.mc.getFramebuffer().unbindFramebuffer();
		GlStateManager.viewport(0, 0, 256, 256);
		this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
		this.rotateAndBlurSkybox(p_73971_3_);
		int i = 3;
		CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

		if (custompanoramaproperties != null) {
			i = custompanoramaproperties.getBlur3();
		}

		for (int j = 0; j < i; ++j) {
			this.rotateAndBlurSkybox(p_73971_3_);
			this.rotateAndBlurSkybox(p_73971_3_);
		}

		this.mc.getFramebuffer().bindFramebuffer(true);
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		float f2 = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
		float f = (float) this.height * f2 / 256.0F;
		float f1 = (float) this.width * f2 / 256.0F;
		int k = this.width;
		int l = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(0.0D, (double) l, (double) this.zLevel).tex((double) (0.5F - f), (double) (0.5F + f1))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos((double) k, (double) l, (double) this.zLevel).tex((double) (0.5F - f), (double) (0.5F - f1))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos((double) k, 0.0D, (double) this.zLevel).tex((double) (0.5F + f), (double) (0.5F - f1))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (0.5F + f), (double) (0.5F + f1))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	AnimationUtil animUtil;

	double scaling;
    public static float animatedMouseX;
    public static float animatedMouseY;

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

//        GlStateManager.disableAlpha();
//        this.renderSkybox(mouseX, mouseY, partialTicks);
//        GlStateManager.enableAlpha();

		scaling = animUtil.easeOut(0, -400, 400, .6);
		scaling = MathHelper.clamp_double((int) scaling, -400, 400);

		this.drawBackground(0);

//        this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/mainmenubg.png"));
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
//        this.drawModalRectWithCustomSizedTexture(-1177/2 - 372 - animatedMouseX + sr.getScaledWidth(), -34/2 +8 - animatedMouseY/9.5f + sr.getScaledHeight()/19 - 19, 0, 0, 3841/2, 1194/2, 3841/2, 1194/2);
//       
        
	      FontManager.sw15.drawStringWithAlpha(this.bing.text, 10.0F,
					height - 25, Colors.getColor(255, 222, 222, 2), 0.6F);
		
		GL11.glPushMatrix();

		GL11.glTranslated(scaling, scaling, 0);

		int h = new ScaledResolution(this.mc).getScaledHeight();
		int w = new ScaledResolution(this.mc).getScaledWidth();

		ScaledResolution res = new ScaledResolution(this.mc);

		 pe.render(animatedMouseX, animatedMouseY);

//        	RenderUtils.drawRoundedRect(w / 2 - 120, h / 2 -80, w / 2 + 120, h / 2 + 80,new Color(0, 0, 0,150).getRGB());

		RenderUtil.rectangleBordered(w / 2 - 70, h / 2 - 70, w / 2 + 70, h / 2 + 75, -0.5, Colors2.getColor(60, 255),
				Colors2.getColor(61, 255));
		RenderUtils.drawRect(w / 2 - 70, h / 2 - 50, w / 2 + 70, h / 2 + 75, Colors.getColor(90, 90, 90, 255));

		
		FontManager.baloo18.drawCenteredStringWithAlpha("Power X Client", w / 2, h / 2 - 66,
				Colors2.getColor(170, 230, 255), 1F);

		drawGradientRects(w / 2 - 70, h / 2 - 50, w / 2 + 70, h / 2 - 47, Colors2.getColor(90, 0),
				Colors2.getColor(0, 200));

		float startY = height / 2.0f - 45.0f;
		for (Buton b : this.buttons) {
			b.draw(width / 2.0f - 65.0f, startY, mouseX, mouseY);
			startY += 20.0f;
		}
		Color Color = new Color(255, 255, 255);
		Color OnColor = new Color(0, 123, 255);
		GL11.glPopMatrix();
		int fonthigh = (int) FontManager.sw15.FONT_HEIGHT;
		FontManager.sw15.drawStringWithAlpha("Welcome, \247a" + Client.ClientUser + " ", 10.0F,
				height - 36.54F - (float) (fonthigh * 0), Colors.WHITE.c, 0.6F);

        animatedMouseX += ((mouseX-animatedMouseX) / 1.8) + 0.1;
        animatedMouseY += ((mouseY-animatedMouseY) / 1.8) + 0.1;
        
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static void Rect(double x, double y, double w, double h, int color) {
		Gui.drawRect(x, y, w + x, h + y, color);
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (Buton b : this.buttons) {
			if (!b.isHovered)
				continue;
			b.onClick();
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);

		synchronized (this.threadLock) {
			if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v
					&& mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
				GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
				guiconfirmopenlink.disableSecurityWarning();
				this.mc.displayGuiScreen(guiconfirmopenlink);
			}
		}

		/*
		 * if (this.func_183501_a()) { this.field_183503_M.mouseClicked(mouseX, mouseY,
		 * mouseButton); }
		 */
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		if (this.field_183503_M != null) {
			this.field_183503_M.onGuiClosed();
		}
	}

	protected static void drawGradientRects(float left, float top, float right, float bottom, int startColor,
			int endColor) {
		float f = (startColor >> 24 & 0xFF) / 255.0f;
		float f2 = (startColor >> 16 & 0xFF) / 255.0f;
		float f3 = (startColor >> 8 & 0xFF) / 255.0f;
		float f4 = (startColor & 0xFF) / 255.0f;
		float f5 = (endColor >> 24 & 0xFF) / 255.0f;
		float f6 = (endColor >> 16 & 0xFF) / 255.0f;
		float f7 = (endColor >> 8 & 0xFF) / 255.0f;
		float f8 = (endColor & 0xFF) / 255.0f;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(left, bottom, 0.0).tex(0.0, 1.0).color(f2, f3, f4, f).endVertex();
		worldrenderer.pos(right, bottom, 0.0).tex(1.0, 1.0).color(f2, f3, f4, f).endVertex();
		worldrenderer.pos(right, top, 0.0).tex(1.0, 0.0).color(f6, f7, f8, f5).endVertex();
		worldrenderer.pos(left, top, 0.0).tex(0.0, 0.0).color(f6, f7, f8, f5).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public class Buton {
		public float x;
		public float y;
		public GuiScreen screen;
		public String text;
		public boolean isHovered;

		public Buton(GuiScreen s, String t) {
			this.isHovered = false;
			this.screen = s;
			this.text = t;
		}

		public void draw(float x, float y, int mouseX, int mouseY) {
			this.x = x;
			this.y = y;
			if (RenderUtil.isHovering(mouseX, mouseY, x, y, x + 120.0f, y + 16.0f)) {
				this.isHovered = true;
			} else {
				this.isHovered = false;
			}
			RenderUtil.drawRect(x, y, x + 130.0f, y + 16.0f, ClientUtil.reAlpha(Colors.BLACK.c, 0.5f));
			FontManager.baloo18.drawCenteredStringWithAlpha(this.text, x + 65.0f, y + 2f, Colors.WHITE.c,
					this.isHovered ? 1.0f : 0.6f);
		}

		public void onClick() {
			if (this.screen == null) {
				Minecraft.getMinecraft().shutdown();
			} else {
				playPressSound(Minecraft.getMinecraft().getSoundHandler());
//            	 Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
				Minecraft.getMinecraft().displayGuiScreen(this.screen);
			}
		}

		public void playPressSound(SoundHandler soundHandlerIn) {
			soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
		}

		public void drawRoundedRect(float left, float top, float right, float bottom, int color) {
			RenderUtil.drawRect(left - 0.5f, top + 0.5f, left, bottom - 0.5f, color);
			RenderUtil.drawRect(left + 0.5f, top - 0.5f, right - 0.5f, top, color);
			RenderUtil.drawRect(right, top + 0.5f, right + 0.5f, bottom - 0.5f, color);
			RenderUtil.drawRect(left + 0.5f, bottom, right - 0.5f, bottom + 0.5f, color);
			RenderUtil.drawRect(left, top, right, bottom, color);
		}
	}

}
