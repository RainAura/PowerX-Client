package cn.Power.mod.mods.PLAYER;

import java.util.Random;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.WORLD.InventoryManager;
import cn.Power.util.misc.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmor extends Mod {
	private Timer timer = new Timer();

	// #SKIDDED IT LOL
	private int[] bestArmor;
	int delay;
	public static boolean ec = false;
	private int num = 5;
	int j;
	double maxValue = -1;
	double mv;
	int item = -1;
	
	public static boolean Look;

	private final int[] boots = { 313, 309, 317, 305, 301 };
	private final int[] chestplate = { 311, 307, 315, 303, 299 };
	private final int[] helmet = { 310, 306, 314, 302, 298 };
	private final int[] leggings = { 312, 308, 316, 304, 300 };

	public Value<Double> DELAY = new Value<Double>("AutoArmor_Delay", 1.0, 1.0, 10.0, 1.0);
	
	public Value mode = new Value("AutoArmor", "Mode", 0);
	
	public AutoArmor() {
		super("AutoArmor", Category.PLAYER);
		mode.mode.add("Basic");
		mode.mode.add("OpenInv");
	}

	@Override
	public void onDisable() {
		Look = false;
	}
	
	public final boolean IsInvFull() {
		for (int i = 9; i < 45; i++) {
			final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if (stack == null) {
				return false;
			}
		}
		return true;
	}
	
	@EventTarget
	public void onTick(EventPreMotion e) {
		
		
		if(Minecraft.thePlayer.inventory.getItemStack() != null)
			return;
		
		InventoryManager inv = (InventoryManager) ModManager.getModByClass(InventoryManager.class);
        
        if(inv.isEnabled())return;
        
        if(mode.isCurrentMode("OpenInv") && !(mc.currentScreen instanceof GuiInventory)){
           	return;
           }
        
		if ((mc.thePlayer.capabilities.isCreativeMode)
				|| (mc.thePlayer.openContainer != null) && (mc.thePlayer.openContainer.windowId != 0)) {
			
			Look = false;
			
			return;
		}

		if (this.delay >= 6 + new Random().nextInt(4)) {
			delay = 0;
			maxValue = -1;
			item = -1;
			for (int i = 9; i < 45; i++) {
				if ((mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null)) {
					if (canEquip(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) != -1
							&& (canEquip(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) == num))
						change(num, i);
				}
			}
			if (item != -1) {
				Look = true;
				

				if (mc.thePlayer.inventoryContainer.getSlot(item).getStack() != null
						&& mc.thePlayer.inventoryContainer.getSlot(num).getStack() != null)
					if(IsInvFull() && KillAura.Target == null)
						mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, num, 1, 4, mc.thePlayer);
					else
						mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, num, 0, 1, mc.thePlayer);
					
				 mc.playerController.updateController();
				
				mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, item, 0, 1, mc.thePlayer);
				
				 mc.playerController.updateController();
				 
		
				 
				delay = 0;
			} else {
				Look = false;
			}
			
			if (num == 8) {
				num = 5;
			} else {
				num++;
			}

		} else
			delay++;
	}
	
	public static boolean isBestArmor(ItemStack stack, int type) {
		float prot = getProtValue(stack);
		String strType = "";
		if (type == 1) {
			strType = "helmet";
		} else if (type == 2) {
			strType = "chestplate";
		} else if (type == 3) {
			strType = "leggings";
		} else if (type == 4) {
			strType = "boots";
		}
		if (!stack.getUnlocalizedName().contains(strType)) {
			return false;
		}
		for (int i = 5; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getProtValue(is) > prot && is.getUnlocalizedName().contains(strType))
					return false;
			}
		}
		return true;
	}
	

	private int canEquip(ItemStack stack) {
		for (int id : this.boots)
			if (stack.getItem().getIdFromItem(stack.getItem()) == id) {
				return 8;
			}
		for (int id : this.leggings)
			if (stack.getItem().getIdFromItem(stack.getItem()) == id) {
				return 7;
			}
		for (int id : this.chestplate)
			if (stack.getItem().getIdFromItem(stack.getItem()) == id) {
				return 6;
			}
		for (int id : this.helmet)
			if (stack.getItem().getIdFromItem(stack.getItem()) == id) {
				return 5;
			}

		return -1;
	}

	private void change(int numy, int i) {
		if (maxValue == -1) {
			if (mc.thePlayer.inventoryContainer.getSlot(numy).getStack() != null) {
				mv = getProtValue(mc.thePlayer.inventoryContainer.getSlot(numy).getStack());
			} else
				mv = maxValue;
		} else {
			mv = maxValue;
		}
		if (mv <= getProtValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
			if (mv == getProtValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
				int currentD = (mc.thePlayer.inventoryContainer.getSlot(numy).getStack() != null
						? mc.thePlayer.inventoryContainer.getSlot(numy).getStack().getItemDamage()
						: 999);
				int newD = (mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null
						? mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItemDamage()
						: 500);
				if (newD <= currentD) {
					if (newD == currentD) {
					} else {
						item = i;
						maxValue = getProtValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
					}
				}
			} else {
				item = i;
				maxValue = getProtValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
			}
		}
	}

	public static float getProtValue(ItemStack stack) {
		
		if(stack == null)
			return 0;
		
		float prot = 0;
		if ((stack.getItem() instanceof ItemArmor)) {
			ItemArmor armor = (ItemArmor) stack.getItem();
			prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount)
					* EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D;
			prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.depthStrider.effectId, stack) / 45d;
			prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 40d;
			prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100d;
			prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100d;
			prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100d;
			prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50d;
			prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack) / 100d;
			prot += stack.getDisplayName().toLowerCase().contains("\247abarbarian chestplate") ? 3.5d : 0d;
			prot += stack.getDisplayName().toLowerCase().contains("\2476exodus") ? 4d : 0d;
			prot += stack.getDisplayName().toLowerCase().contains("\247ashoes of vidar") ? 4d : 0d;
			prot += stack.getDisplayName().toLowerCase().contains("\247ahermes' boots") ? 3.5d : 0d;
			prot += stack.getDisplayName().toLowerCase().contains("\247ahide of leviathan") ? 3.5d : 0d;
			prot += stack.getDisplayName().toLowerCase().contains("\247aseven") ? 3.5d : 0d;
			if(stack.isItemStackDamageable() && stack.isItemDamaged())
				prot -= stack.getItemDamage() * 0.000005;
		}
		return prot;
	}
}