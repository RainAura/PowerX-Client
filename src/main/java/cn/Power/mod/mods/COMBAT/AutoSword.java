package cn.Power.mod.mods.COMBAT;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import com.darkmagician6.eventapi.types.Priority;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Scaffold;
import cn.Power.notification.Notification.Type;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.ChatUtil;
import cn.Power.util.misc.STimer;
import cn.Power.util.timeUtils.TimerUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class AutoSword extends Mod {

	private TimerUtil timer = new TimerUtil();

	cn.Power.util.misc.Timer timers = new cn.Power.util.misc.Timer();
	private int old;
	public static boolean switching = false;

	public static ItemStack anduStack;

	public AutoSword() {
		super("AutoAnduril", Category.COMBAT);
	}

	public static STimer SIGMATIMER = new STimer();
	public static int weaponSlot1 = 36;

	@Override
	public void onDisable() {
		if (this.switching) {
			this.switching = false;

			

			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(old));
		}

		super.onDisable();
	}

	@EventTarget
	private void packet(EventPacket e) {
		if (e.getPacket() instanceof C09PacketHeldItemChange) {

			C09PacketHeldItemChange packet = (C09PacketHeldItemChange) e.getPacket();
			old = packet.getSlotId();

		
			KillAura.Blockreach = false;
			
			timer.reset();

			this.switching = false;
		} else if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {

			C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) e.getPacket();

			packet.setStack(switching && anduStack != null ? anduStack : packet.getStack());
		}
	}

	@EventTarget(Priority.LOWEST)
	public void onPre(EventPreMotion event) {

		if (SkyBlockUtils.isBlitz() && SkyBlockUtils.isBlitzGame()) {
			return;
		}

		if (SkyBlockUtils.isSkyBlock() || SkyBlockUtils.isMurder()) {
			return;
		}

		if (ModManager.getModByClass(Scaffold.class).isEnabled()) {

			return;
		}

		int i = this.mc.thePlayer.inventory.currentItem;

		if (i != mc.playerController.currentPlayerItem) {

			mc.playerController.syncCurrentPlayItem();

			return;
		}

		if (mc.thePlayer.isEating())
			this.switching = false;

		if (mc.thePlayer.isEating() || mc.thePlayer.isSneaking()) {
			return;
		}

		if (this.switching) {


			this.switching = false;


			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(old));
			
			KillAura.Blockreach = false;
			
			mc.playerController.updateController();

		}

		if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)
//				|| (mc.thePlayer.experience == 1.0 && SkyBlockUtils.isMWgame())
				) {
			if (invCheck()) {
				ChangeAndúril();
			}
		}
		for (final PotionEffect pot : this.mc.thePlayer.getActivePotionEffects()) {
			if (pot.getPotionID() == 1 && pot.getDuration() <= ThreadLocalRandom.current().nextInt(7, 14)) {
				if (invCheck() && !mc.thePlayer.isEating()
						&& !mc.thePlayer.isSneaking()) {
					ChangeAndúril();
				}
			}
		}
	}

	public static boolean isBestWeapon(ItemStack stack) {
		float damage = getDamage(stack);
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getDamage(is) > damage && (is.getItem() instanceof ItemSword))
					return false;
			}
		}
		if ((stack.getItem() instanceof ItemSword)) {
			return true;
		} else {
			return false;
		}
	}

	public void swap(int slot1, int hotbarSlot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
	}

	private static float getDamage(ItemStack stack) {
		float damage = 0;
		Item item = stack.getItem();
		if (item instanceof ItemTool) {
			ItemTool tool = (ItemTool) item;
			damage += tool.getDamage();
		}
		if (item instanceof ItemSword) {
			ItemSword sword = (ItemSword) item;
			damage += sword.getAttackDamage()
					+ (item.itemRegistry.toString().toLowerCase().contains("\u00A77Weaponsmith Ultimate") ? 1f : 0d);
		}
		damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f
				+ EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;

		return damage;
	}

	private boolean invCheck() {
		for (int i = 36; i < 45; ++i) {
			ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			Item item;
			if (is != null && is.getDisplayName().contains("Andúril")) {
				return true;
			}
//			if (is != null && is.getDisplayName().contains("Hunter Bow") && mc.thePlayer.experience == 1.0 && KillAura.Target != null) {
//				return true;
//			}
		}

		anduStack = null;

		return false;
	}

	private int getAndúril() {
		Minecraft mc = Minecraft.getMinecraft();
		int soup = -1;
		for (int i = 36; i < 45; ++i) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (!is.getDisplayName().contains("Andúril") /* && !is.getDisplayName().contains("Hunter Bow") */)
					continue;
				if (Item.getIdFromItem(item) == Item.getIdFromItem(Items.iron_sword)
		/*				|| Item.getIdFromItem(item) == Item.getIdFromItem(Items.bow) */) {
					soup = i - 36;

					anduStack = is;
				}
			}
		}
		return soup;
	}

	private void ChangeAndúril() {
		if (timers.check(500) && !this.switching) {
			if (getAndúril() != -1 && !mc.thePlayer.isEating()) {

				old = mc.thePlayer.inventory.currentItem;

				

	//			if (getAndúril() != mc.thePlayer.inventory.currentItem || !SkyBlockUtils.isMWgame()) 
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(getAndúril()));
				
				KillAura.Blockreach = false;
				
	//			if(SkyBlockUtils.isMWgame())
	//				mc.thePlayer.swingItem();
				
				mc.playerController.updateController();

				this.switching = true;
			}
			timer.reset();
		}
	}

}