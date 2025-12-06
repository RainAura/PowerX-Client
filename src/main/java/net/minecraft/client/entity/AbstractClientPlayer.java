package net.minecraft.client.entity;

import java.io.File;
import java.lang.ref.WeakReference;

import com.mojang.authlib.GameProfile;

import cn.Power.Client;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.ClickGui;
import cn.Power.mod.mods.RENDER.NoFov;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.optifine.player.CapeUtils;
import net.optifine.player.PlayerConfigurations;

public abstract class AbstractClientPlayer extends EntityPlayer {
	private NetworkPlayerInfo playerInfo;
	private WeakReference<ResourceLocation> locationOfCape = null;
	private long reloadCapeTimeMs = 0L;
	private boolean elytraOfCape = false;
	private String nameClear = null;

	public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
		this.nameClear = playerProfile.getName();

		if (this.nameClear != null && !this.nameClear.isEmpty()) {
			this.nameClear = StringUtils.stripControlCodes(this.nameClear);
		}

		CapeUtils.downloadCape(this);
		PlayerConfigurations.getPlayerConfiguration(this);
	}

	/**
	 * Returns true if the player is in spectator mode.
	 */
	@Override
	public boolean isSpectator() {
		Minecraft.getMinecraft();
		NetworkPlayerInfo networkplayerinfo = Minecraft.getNetHandler().getPlayerInfo(this.getGameProfile().getId());
		return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.SPECTATOR;
	}

	/**
	 * Checks if this instance of AbstractClientPlayer has any associated player
	 * data.
	 */
	public boolean hasPlayerInfo() {
		return this.getPlayerInfo() != null;
	}

	protected NetworkPlayerInfo getPlayerInfo() {
		if (this.playerInfo == null) {
			Minecraft.getMinecraft();
			this.playerInfo = Minecraft.getNetHandler().getPlayerInfo(this.getUniqueID());
		}

		return this.playerInfo;
	}

	/**
	 * Returns true if the player has an associated skin.
	 */
	public boolean hasSkin() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
	}

	/**
	 * Returns true if the player instance has an associated skin.
	 */
	public ResourceLocation getLocationSkin() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID())
				: networkplayerinfo.getLocationSkin();
	}

	public ResourceLocation getLocationCape() {
		ResourceLocation loc = new ResourceLocation("Power/PowerX.png");

		if (!Config.isShowCapes()) {
			return null;
		} else {
			if (this.reloadCapeTimeMs != 0L && System.currentTimeMillis() > this.reloadCapeTimeMs) {
				CapeUtils.reloadCape(this);
				this.reloadCapeTimeMs = 0L;
			}

			if (this.locationOfCape != null) {
				return this.locationOfCape.get() == null ? null
						: (this.locationOfCape.get() == null && Client.Cape.getValueState().booleanValue()
								&& this.getUniqueID().equals(Minecraft.thePlayer.getUniqueID())) ? loc
										: this.locationOfCape.get();
			} else {
				NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
				Minecraft.getMinecraft();
				return networkplayerinfo == null ? null
						: (networkplayerinfo.getLocationCape() == null && Client.Cape.getValueState().booleanValue()
								&& this.getUniqueID().equals(Minecraft.thePlayer.getUniqueID())) ? loc
										: networkplayerinfo.getLocationCape();
			}
		}
	}

	public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		ITextureObject itextureobject = texturemanager.getTexture(resourceLocationIn);

		if (itextureobject == null) {
			itextureobject = new ThreadDownloadImageData((File) null,
					String.format("http://skins.minecraft.net/MinecraftSkins/%s.png",
							StringUtils.stripControlCodes(username)),
					DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new ImageBufferDownload());
			texturemanager.loadTexture(resourceLocationIn, itextureobject);
		}

		return (ThreadDownloadImageData) itextureobject;
	}

	/**
	 * Returns true if the username has an associated skin.
	 */
	public static ResourceLocation getLocationSkin(String username) {
		return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
	}

	public String getSkinType() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID())
				: networkplayerinfo.getSkinType();
	}

	public float getFovModifier() {
		NoFov nf;
		
		nf = (NoFov) ModManager.getModByClass(NoFov.class);
		
		float f = nf.isEnabled() ? NoFov.Fov.getValueState().floatValue() : 1.00F;

		if (this.capabilities.isFlying) {
			f *= 1.1F;
		}

		IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		if (!nf.isEnabled()) {
			f = (float) (f
					* ((iattributeinstance.getAttributeValue() / this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));
		}

		if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
			int i = this.getItemInUseDuration();
			float f1 = i / 20.0F;

			if (f1 > 1.0F) {
				f1 = 1.0F;
			} else {
				f1 = f1 * f1;
			}

			f *= 1.0F - f1 * 0.15F;
		}

		return f;
	}

	public String getNameClear() {
		return this.nameClear;
	}

	public ResourceLocation getLocationOfCape() {
		return this.locationOfCape.get();
	}

	public void setLocationOfCape(ResourceLocation p_setLocationOfCape_1_) {
		this.locationOfCape = new WeakReference<>(p_setLocationOfCape_1_);
	}

	public boolean hasElytraCape() {
		ResourceLocation resourcelocation = this.getLocationCape();

		if (resourcelocation == null) {
			return false;
		} else {
			return resourcelocation == this.locationOfCape.get() ? this.elytraOfCape : true;
		}
	}

	public void setElytraOfCape(boolean p_setElytraOfCape_1_) {
		this.elytraOfCape = p_setElytraOfCape_1_;
	}

	public boolean isElytraOfCape() {
		return this.elytraOfCape;
	}

	public long getReloadCapeTimeMs() {
		return this.reloadCapeTimeMs;
	}

	public void setReloadCapeTimeMs(long p_setReloadCapeTimeMs_1_) {
		this.reloadCapeTimeMs = p_setReloadCapeTimeMs_1_;
	}

	/**
	 * interpolated look vector
	 */
	@Override
	public Vec3 getLook(float partialTicks) {
		return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
	}
}
