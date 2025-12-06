package cn.Power.mod.mods.COMBAT;

import java.awt.Color;
import java.util.List;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender2D;
import cn.Power.events.EventRenderGui;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.Timer;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoHeal extends Mod {
	public final Value<String> hurtimeMode = new Value<String>("AutoHeal", "HurtTimeCheck", 0);
	private Value<Double> health = new Value<Double>("AutoHeal_Health", 6.0, 0.5, 20.0, 0.5);
	private Value<Double> delay = new Value<Double>("AutoHeal_Delay", 1500.0, 350.0, 3000.0, 10.0);
	private Value<Double> SLOT = new Value<Double>("AutoHeal_Slot", 7.0, 1.0, 9.0, 1.0);
	private Value<Double> hurtTime = new Value<Double>("AutoHeal_HurtTime", 16.0, 0.0, 20.0, 1.0);

	private Value<Boolean> aura = new Value<Boolean>("AutoHeal_OnlyAura", true);
	private Value<Boolean> Heads = new Value<Boolean>("AutoHeal_Heads", true);
	private Value<Boolean> GApple = new Value<Boolean>("AutoHeal_GApple", true);
	private Value<Boolean> allow_potion = new Value<Boolean>("AutoHeal_Potion", true);
	private Value<Boolean> Boost = new Value<Boolean>("AutoHeal_Boost", true);
	private Value<Boolean> hurtTimeCheck = new Value<Boolean>("AutoHeal_HurtTimeCheck", true);
	private Value<Boolean> AbsorptionCheck = new Value<Boolean>("AutoHeal_AbsorpChangeCheck", true);

	Timer timer = new Timer();
	Timer potiondelay = new Timer();
	public int Head, Gap, Godap, potion, milk_bukkit;

	public boolean SwitchBack;

	public int Hand;

	public boolean needsToHeal;

	public float lastAbsotion = 0;

	public AutoHeal() {
		super("AutoHeal", Category.COMBAT);

		this.hurtimeMode.mode.add(">=");
		this.hurtimeMode.mode.add("==");
		this.hurtimeMode.mode.add("<=");

	}

	public void SwitchBack() {
		if (SwitchBack) {
			SwitchBack = false;
			Client.blockActionsForHealing = false;

			Minecraft.thePlayer.inventory.currentItem = Hand;

			Minecraft.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(Hand));
		}
	}

	@EventTarget
	public void onPacketReceive(EventPacket e) {
		if (e.getPacket() instanceof S06PacketUpdateHealth) {
			final S06PacketUpdateHealth packetUpdateHealth = (S06PacketUpdateHealth) e.getPacket();

		}
	}

	@Override
	public void onDisable() {
		SwitchBack();

		Client.blockActionsForHealing = false;
	}

	@EventTarget(4)
	public void onPre(EventPreMotion event) {

		try {
			this.setDisplayName("Packet");

			int Slot = SLOT.getValueState().intValue() - 1;

			int gapSlot = getGAPFromInventory();
			int soupSlot = getSoupFromInventory();
			int godapSlot = getGGAPFromInventory();
			int potionSlot = getPotionFromInventory();
			int milkSlot = getMilkFromInventory();

			Gap = getGAPSlot();
			Head = getshopSlot();
			potion = getPotionSlot();
			Godap = getGGAPSlot();
			milk_bukkit = getMilkSlot();

			SwitchBack();

			if (Minecraft.thePlayer.getAbsorptionAmount() > this.lastAbsotion)
				this.lastAbsotion = Minecraft.thePlayer.getAbsorptionAmount();

			if (!ModManager.getModByClass(KillAura.class).isEnabled() && aura.getValueState()) {
				return;
			}

			boolean healthcheck = false;

			healthcheck = (Minecraft.thePlayer.getHealth()
					+ Minecraft.thePlayer.getAbsorptionAmount()) < (health.getValueState()).floatValue() * 2 + 1;

			boolean hurtTimeChecked = !this.hurtTimeCheck.getValueState();

			if (hurtTimeChecked) {

				if (hurtimeMode.isCurrentMode(">="))
					hurtTimeChecked = mc.thePlayer.hurtResistantTime >= hurtTime.getValueState().doubleValue();
				else if (hurtimeMode.isCurrentMode("=="))
					hurtTimeChecked = mc.thePlayer.hurtResistantTime == hurtTime.getValueState().doubleValue();
				else if (hurtimeMode.isCurrentMode("<="))
					hurtTimeChecked = mc.thePlayer.hurtResistantTime <= hurtTime.getValueState().doubleValue();

				if (healthcheck)
					healthcheck = (healthcheck && hurtTimeChecked);

			}

			if (AbsorptionCheck.getValueState() && Minecraft.thePlayer.getAbsorptionAmount() > 0
					&& CheckHealType() != HealType.MILK_BUKKIT) {

				healthcheck = healthcheck && Minecraft.thePlayer.getAbsorptionAmount() < this.lastAbsotion;

			}

			if (healthcheck) {

				if (invCheckHEALPOTION() && potionSlot != -1) {
					if (CheckHealType() == HealType.POTION)
						swap(potionSlot, Slot);
				}

				if (invCheckGAP() && gapSlot != -1) {
					if (CheckHealType() == HealType.GAPPLE) {
						swap(gapSlot, Slot);
					}
				}

				if (invCheck() && soupSlot != -1) {
					if (CheckHealType() == HealType.HEAD)
						swap(soupSlot, Slot);
				}

				if (invCheckGGAP() && godapSlot != -1) {
					if (CheckHealType() == HealType.GODAPPLE)
						swap(godapSlot, Slot);
				}

				if (this.invCheckMilkBukkit() && milkSlot != -1) {
					if (CheckHealType() == HealType.MILK_BUKKIT)
						swap(milkSlot, Slot);
				}

				if (Gap != -1 && CheckHealType() == HealType.GAPPLE && !SwitchBack
						&& timer.check(delay.getValueState().floatValue())) {
					Client.blockActionsForHealing = true;
					Hand = Minecraft.thePlayer.inventory.currentItem;
					Minecraft.thePlayer.inventory.currentItem = Gap;
					timer.reset();

					Minecraft.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(Gap));
					Minecraft.getNetHandler().getNetworkManager().sendPacket(
							new C08PacketPlayerBlockPlacement(Minecraft.thePlayer.inventory.getCurrentItem()));

					if (Boost.getValueState())
						Minecraft.getNetHandler().getNetworkManager()
								.sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));
					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C09PacketHeldItemChange(Minecraft.thePlayer.inventory.currentItem));

					mc.playerController.updateController();
					this.lastAbsotion = Minecraft.thePlayer.getAbsorptionAmount();

					SwitchBack = true;
					timer.reset();

				}

				if (milk_bukkit != -1 && CheckHealType() == HealType.MILK_BUKKIT && !SwitchBack
						&& timer.check(delay.getValueState().floatValue()) && potiondelay.check(1400L)) {
					Client.blockActionsForHealing = true;
					potiondelay.reset();
					timer.reset();
					Hand = Minecraft.thePlayer.inventory.currentItem;
					Minecraft.thePlayer.inventory.currentItem = milk_bukkit;

					Minecraft.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(milk_bukkit));
					Minecraft.getNetHandler().getNetworkManager().sendPacket(
							new C08PacketPlayerBlockPlacement(Minecraft.thePlayer.inventory.getCurrentItem()));
					Minecraft.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(milk_bukkit));

					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));
					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));

					this.lastAbsotion = Minecraft.thePlayer.getAbsorptionAmount();

					SwitchBack = true;
					potiondelay.reset();
					timer.reset();

				}

				if (potion != -1 && CheckHealType() == HealType.POTION && !SwitchBack
						&& timer.check(delay.getValueState().floatValue()) && potiondelay.check(1400L)) {
					Client.blockActionsForHealing = true;
					potiondelay.reset();
					timer.reset();
					Hand = Minecraft.thePlayer.inventory.currentItem;
					Minecraft.thePlayer.inventory.currentItem = potion;

					Minecraft.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(potion));
					Minecraft.getNetHandler().getNetworkManager().sendPacket(
							new C08PacketPlayerBlockPlacement(Minecraft.thePlayer.inventory.getCurrentItem()));
					Minecraft.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(potion));

					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));
					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));

					this.lastAbsotion = Minecraft.thePlayer.getAbsorptionAmount();

					SwitchBack = true;
					potiondelay.reset();
					timer.reset();

				}

				if (Godap != -1 && CheckHealType() == HealType.GODAPPLE && !SwitchBack
						&& timer.check(delay.getValueState().floatValue()) && potiondelay.check(1400L)) {
					Client.blockActionsForHealing = true;
					Hand = Minecraft.thePlayer.inventory.currentItem;
					Minecraft.thePlayer.inventory.currentItem = Godap;
					timer.reset();

					Minecraft.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(Godap));
					Minecraft.getNetHandler().getNetworkManager().sendPacket(
							new C08PacketPlayerBlockPlacement(Minecraft.thePlayer.inventory.getCurrentItem()));

					if (Boost.getValueState())
						Minecraft.getNetHandler().getNetworkManager()
								.sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));
					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C09PacketHeldItemChange(Minecraft.thePlayer.inventory.currentItem));

					this.lastAbsotion = Minecraft.thePlayer.getAbsorptionAmount();

					SwitchBack = true;
					potiondelay.reset();
					timer.reset();

				}

				if (SkyBlockUtils.isSkyWars() || SkyBlockUtils.isSkyBlock() || SkyBlockUtils.isMWgame()) {
					return;
				}
				if (Head != -1 && CheckHealType() == HealType.HEAD && timer.check(delay.getValueState().floatValue())) {
					Client.blockActionsForHealing = true;
					timer.reset();
					Minecraft.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(Head));
					Minecraft.getNetHandler().getNetworkManager().sendPacket(
							new C08PacketPlayerBlockPlacement(Minecraft.thePlayer.inventory.getCurrentItem()));
					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C08PacketPlayerBlockPlacement(
									new BlockPos(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY - 3,
											Minecraft.thePlayer.posZ),
									EnumFacing.UP.getIndex(), Minecraft.thePlayer.inventory.getCurrentItem(), 0.45678f,
									1.0f, 0.7654321f));
					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));
					Minecraft.getNetHandler().getNetworkManager()
							.sendPacket(new C09PacketHeldItemChange(Minecraft.thePlayer.inventory.currentItem));
					Client.blockActionsForHealing = false;

					mc.playerController.updateController();
					this.lastAbsotion = Minecraft.thePlayer.getAbsorptionAmount();

					timer.reset();

				}

			}

		} catch (Throwable c) {
			c.printStackTrace();
		}
	}

	private HealType CheckHealType() {
		int Type = 0;
		ItemStack[] arrayOfItemStack;
		int j = (arrayOfItemStack = Minecraft.thePlayer.inventory.mainInventory).length;
		
		

		for (int i = 0; i < j; i++) {
			ItemStack item = arrayOfItemStack[i];
			if (item == null)
				continue;
			if (ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() != 47 && SkyBlockUtils.isMWgame() && item.getItem() == Items.milk_bucket) {
				return HealType.MILK_BUKKIT;
			}
		}

		for (int i = 0; i < j; i++) {
			if (!allow_potion.getValueState())
				break;
			ItemStack item = arrayOfItemStack[i];
			if (item == null)
				continue;
			if (ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() != 47
					&& (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(Items.potionitem))
					&& item.getItem().getItemUseAction(arrayOfItemStack[i]) == EnumAction.DRINK
					&& isHealingPotion(arrayOfItemStack[i])) {
				return HealType.POTION;
			}
		}


		for (int i = 0; i < j; i++) {
			if (!GApple.getValueState())
				break;
			ItemStack item = arrayOfItemStack[i];
			if (item == null)
				continue;
			if (ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() != 47
					&& (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(Items.golden_apple)
							&& item.getRarity() != EnumRarity.EPIC)) {
				return HealType.GAPPLE;
			}
		}
		for (int i = 0; i < j; i++) {
			ItemStack item = arrayOfItemStack[i];
			if (item == null)
				continue;
			if (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(Items.skull)
					&& (!item.getUnlocalizedName().contains("item.skull.creeper")
							&& !item.getUnlocalizedName().contains("item.skull.zombie")
							&& !item.getUnlocalizedName().contains("item.skull.skeleton")
							&& !item.getUnlocalizedName().contains("item.skull.wither")
							&& !item.getDisplayName().contains("Backpack"))) {

				if ((item.hasDisplayName() && item.getDisplayName().contains("§6Golden Head"))
						|| !Minecraft.thePlayer.isPotionActive(Potion.regeneration)
						|| (Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								&& Minecraft.thePlayer.getActivePotionEffect(Potion.regeneration).getDuration() <= 1))
					return HealType.HEAD;
			}
		}
		for (int i = 0; i < j; i++) {
			if (!GApple.getValueState())
				break;
			ItemStack item = arrayOfItemStack[i];
			if (item == null)
				continue;
			if (ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() != 47
					&& (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(Items.golden_apple)
							&& item.getRarity() == EnumRarity.EPIC)) {
				return HealType.GODAPPLE;
			}
		}

		return HealType.NULL;

	}

	private int getshopSlot() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 36; i < 45; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (is.getUnlocalizedName().contains("item.skull.creeper")
						|| is.getUnlocalizedName().contains("item.skull.zombie")
						|| is.getUnlocalizedName().contains("item.skull.skeleton")
						|| is.getUnlocalizedName().contains("item.skull.wither")
						|| is.getDisplayName().contains("Backpack") || is.getDisplayName().contains("Profile")
						|| is.getDisplayName().contains("\u4e2a\u4eba\u6863\u6848")
						|| is.getDisplayName().contains("\u6211"))
					continue;
				boolean shouldApple = (Heads.getValueState())
						&& (Item.getIdFromItem(item) == Item.getIdFromItem(Items.skull)
								|| Item.getIdFromItem(item) == Item.getIdFromItem(Items.baked_potato))
						&& ((!Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								|| Minecraft.thePlayer.isPotionActive(Potion.regeneration) && Minecraft.thePlayer
										.getActivePotionEffect(Potion.regeneration).getDuration() <= 1
								|| (Minecraft.thePlayer.getHealth() + Minecraft.thePlayer
										.getAbsorptionAmount() <= health.getValueState().floatValue() * 2)
										&& is.stackSize >= 1));
				if (Item.getIdFromItem(item) == 282 || shouldApple) {
					soup = i - 36;
				}
			}
		}

		return soup;

	}

	private int getGAPSlot() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 36; i < 45; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				boolean shouldApple = (GApple.getValueState())
						&& (Item.getIdFromItem(item) == Item.getIdFromItem(Items.golden_apple)
								&& is.getRarity() != EnumRarity.EPIC)
						&& ((!Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								|| Minecraft.thePlayer.isPotionActive(Potion.regeneration) && Minecraft.thePlayer
										.getActivePotionEffect(Potion.regeneration).getDuration() <= 1
								|| (Minecraft.thePlayer.getHealth() + Minecraft.thePlayer
										.getAbsorptionAmount() <= health.getValueState().floatValue() * 2)
										&& is.stackSize >= 1));
				if (shouldApple) {
					soup = i - 36;
				}
			}
		}

		return soup;

	}

	public boolean isHealingPotion(ItemStack is) {
		Item item = is.getItem();

		if (!((ItemPotion) item).hasEffect(is))
			return false;

		List<PotionEffect> eff = ((ItemPotion) item).getEffects(is);
		for (PotionEffect ef : eff) {
			switch (ef.getPotionID()) {
			case 6: // heal
			case 10: // regeneration
			case 21: // healthBoost
			case 22: // absorption
				return true;

			}
		}

		return false;
	}

	private int getPotionSlot() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 36; i < 45; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				boolean shouldApple = (allow_potion.getValueState())
						&& (Item.getIdFromItem(item) == Item.getIdFromItem(Items.potionitem)
								&& item.getItemUseAction(is) == EnumAction.DRINK && isHealingPotion(is))
						&& ((!Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								|| Minecraft.thePlayer.isPotionActive(Potion.regeneration) && Minecraft.thePlayer
										.getActivePotionEffect(Potion.regeneration).getDuration() <= 1
								|| (Minecraft.thePlayer.getHealth() + Minecraft.thePlayer
										.getAbsorptionAmount() <= health.getValueState().floatValue() * 2)
										&& is.stackSize >= 1));
				if (shouldApple) {
					soup = i - 36;
				}
			}
		}

		return soup;

	}

	private int getMilkSlot() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 36; i < 45; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();

				if (item == Items.milk_bucket && SkyBlockUtils.isMWgame()) {
					soup = i - 36;
				}
			}
		}

		return soup;

	}

	private int getGGAPSlot() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 36; i < 45; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();

				boolean shouldApple = (GApple.getValueState())
						&& (Item.getIdFromItem(item) == Item.getIdFromItem(Items.golden_apple))
						&& ((!Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								&& is.getRarity() == EnumRarity.EPIC
								|| Minecraft.thePlayer.isPotionActive(Potion.regeneration) && Minecraft.thePlayer
										.getActivePotionEffect(Potion.regeneration).getDuration() <= 1
								|| (Minecraft.thePlayer.getHealth() + Minecraft.thePlayer
										.getAbsorptionAmount() <= health.getValueState().floatValue() * 2)
										&& is.stackSize >= 1));
				if (shouldApple) {
					soup = i - 36;
				}
			}
		}

		return soup;

	}

	private boolean invCheck() {
		int i = 36;
		while (i < 45) {
			ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
			Item item;
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					&& (item = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack()
							.getItem()) instanceof ItemSkull
					&& (!is.getUnlocalizedName().contains("item.skull.creeper")
							&& !is.getUnlocalizedName().contains("item.skull.zombie")
							&& !is.getUnlocalizedName().contains("item.skull.skeleton")
							&& !is.getUnlocalizedName().contains("item.skull.wither")
							&& !is.getDisplayName().contains("Backpack"))) {
				return false;
			}
			++i;
		}
		return true;
	}

	private boolean invCheckGAP() {
		int i = 36;
		while (i < 45) {
			ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
			Item item;
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					&& (item = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack()
							.getItem()) instanceof ItemAppleGold
					&& is.getRarity() != EnumRarity.EPIC) {
				return false;
			}
			++i;
		}
		return true;
	}

	private boolean invCheckHEALPOTION() {
		int i = 36;
		while (i < 45) {
			ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
			Item item;
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					&& (item = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack()
							.getItem()) instanceof ItemPotion
					&& item.getItemUseAction(is) == EnumAction.DRINK && isHealingPotion(is)) {
				return false;
			}
			++i;
		}
		return true;
	}

	private boolean invCheckMilkBukkit() {
		int i = 36;
		while (i < 45) {
			ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
			Item item;
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					&& (item = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack()
							.getItem()) instanceof ItemBucketMilk) {
				return false;
			}
			++i;
		}
		return true;
	}

	private boolean invCheckGGAP() {
		int i = 36;
		while (i < 45) {
			ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
			Item item;

			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					&& (item = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack()
							.getItem()) instanceof ItemAppleGold
					&& is.getRarity() == EnumRarity.EPIC) {
				return false;
			}
			++i;
		}
		return true;
	}

	protected void swap(int slot, int hotbarNum) {

//		if (!(mc.currentScreen instanceof GuiInventory))
//			Minecraft.getNetHandler().addToSendQueue(
//					new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));

		Minecraft.playerController.windowClick(Minecraft.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2,
				Minecraft.thePlayer);
	}

	private int getGAPFromInventory() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 9; i < 36; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				boolean shouldApple = (GApple.getValueState())
						&& (Item.getIdFromItem(item) == Item.getIdFromItem(Items.golden_apple)
								&& is.getRarity() != EnumRarity.EPIC)
						&& (!Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								|| Minecraft.thePlayer.isPotionActive(Potion.regeneration) && Minecraft.thePlayer
										.getActivePotionEffect(Potion.regeneration).getDuration() <= 1
								|| !Minecraft.thePlayer.isPotionActive(Potion.absorption) && is.stackSize > 1);
				if (Item.getIdFromItem(item) == 282 || shouldApple) {
					soup = i;
				}
			}
		}
		return soup;
	}

	private int getGGAPFromInventory() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 9; i < 36; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				boolean shouldApple = (GApple.getValueState())
						&& (Item.getIdFromItem(item) == Item.getIdFromItem(Items.golden_apple)
								&& is.getRarity() == EnumRarity.EPIC)
						&& (!Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								|| Minecraft.thePlayer.isPotionActive(Potion.regeneration) && Minecraft.thePlayer
										.getActivePotionEffect(Potion.regeneration).getDuration() <= 1
								|| !Minecraft.thePlayer.isPotionActive(Potion.absorption) && is.stackSize > 1);
				if (Item.getIdFromItem(item) == 282 || shouldApple) {
					soup = i;
				}
			}
		}
		return soup;
	}

	private int getSoupFromInventory() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 9; i < 36; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (is.getUnlocalizedName().contains("item.skull.creeper")
						|| is.getUnlocalizedName().contains("item.skull.zombie")
						|| is.getUnlocalizedName().contains("item.skull.skeleton")
						|| is.getUnlocalizedName().contains("item.skull.wither")
						|| is.getDisplayName().contains("Backpack")
						|| is.getDisplayName().contains("(Bring to shopkeeper)"))
					continue;
				boolean shouldApple = (Heads.getValueState())
						&& (Item.getIdFromItem(item) == Item.getIdFromItem(Items.skull)
								|| Item.getIdFromItem(item) == Item.getIdFromItem(Items.baked_potato))
						&& (!Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								|| Minecraft.thePlayer.isPotionActive(Potion.regeneration) && Minecraft.thePlayer
										.getActivePotionEffect(Potion.regeneration).getDuration() <= 1
								|| !Minecraft.thePlayer.isPotionActive(Potion.absorption) && is.stackSize > 1);
				if (Item.getIdFromItem(item) == 282 || shouldApple) {
					soup = i;
				}
			}
		}
		return soup;
	}

	private int getPotionFromInventory() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 9; i < 36; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				boolean shouldApple = (allow_potion.getValueState())
						&& (Item.getIdFromItem(item) == Item.getIdFromItem(Items.potionitem)
								&& item.getItemUseAction(is) == EnumAction.DRINK && isHealingPotion(is))
						&& (!Minecraft.thePlayer.isPotionActive(Potion.regeneration)
								|| Minecraft.thePlayer.isPotionActive(Potion.regeneration) && Minecraft.thePlayer
										.getActivePotionEffect(Potion.regeneration).getDuration() <= 1
								|| !Minecraft.thePlayer.isPotionActive(Potion.absorption) && is.stackSize > 1);
				if (Item.getIdFromItem(item) == 282 || shouldApple) {
					soup = i;
				}

			}
		}
		return soup;
	}

	private int getMilkFromInventory() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		int bukkit = -1;
		for (int i = 9; i < 36; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();

				if (item == Items.milk_bucket && SkyBlockUtils.isMWgame()) {
					soup = i;
				}

			}
		}
		return soup;
	}

	enum HealType {
		NULL, GAPPLE, HEAD, GODAPPLE, POTION, MILK_BUKKIT;
	}

}
