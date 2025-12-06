package cn.Power.mod.mods.WORLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Multimap;

import cn.Power.Value;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Scaffold;
import cn.Power.mod.mods.PLAYER.AutoArmor;
import cn.Power.ui.Gui.GuiInvManager;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class InventoryManager extends Mod {

	private static List<Block> blacklistedBlocks;

	public static List<Block> getBlacklistedBlocks() {
		return blacklistedBlocks;
	}

	public Value mode = new Value("InventoryManager", "Mode", 0);
	public Value<Double> BLOCKCAP = new Value<Double>("InventoryManager_Blockcap", 128.0, 0.0, 512.0, 8.0);
	public Value<Double> DELAY = new Value<Double>("InventoryManager_Delay", 1.0, 1.0, 10.0, 1.0);
	public Value<Boolean> INVCLEANER = new Value("InventoryManager_InvCleaner", true);
	public Value<Boolean> ARCHERY = new Value("InventoryManager_Archery", false);
	public Value<Boolean> FOOD = new Value("InventoryManager_Food", false);
	public Value<Boolean> UHC = new Value("InventoryManager_Uhc", false);
	public Value<Boolean> SWORD = new Value("InventoryManager_Sword", true);
	public Value<Boolean> OnlyWood = new Value("InventoryManager_OnlyWood", false);
	public Value<Boolean> KeepSpecial = new Value("InventoryManager_KeepSpecial", true);

	public static int weaponSlot = 36, gappleSlot = 37, gheadSlot = 38, pickaxeSlot = 39, axeSlot = 40, shovelSlot = 41;
	public static Timer timer = new Timer();
	ArrayList<Integer> whitelistedItems = new ArrayList<>();
	
	public static boolean LOCK;

	public InventoryManager() {
		super("InventoryManager", Category.WORLD);
		mode.mode.add("Basic");
		mode.mode.add("OpenInv");
		blacklistedBlocks = Arrays.asList(Blocks.crafting_table, Blocks.gold_ore, Blocks.anvil, Blocks.tnt,
				Blocks.gold_block, Blocks.iron_block, Blocks.diamond_block, Blocks.enchanting_table);
	} 
	
	@Override
	public void onEnable() {
		  GuiInvManager.loadConfig();
	}
	
	@Override
	public void onDisable() {
		LOCK = false;
	}

	@EventTarget
	public void onUpdate(EventUpdate em) {
		
		
		if(Minecraft.thePlayer.inventory.getItemStack() != null)
			return;
		
		LOCK = true;
		long delay = DELAY.getValueState().longValue() * 50;
		AutoArmor armor = (AutoArmor) ModManager.getModByClass(AutoArmor.class);
		long Adelay = armor.DELAY.getValueState().longValue() * 50;
		String Amode = armor.mode.getModeAt(this.mode.getCurrentMode());

		if (timer.check(Adelay) && armor.isEnabled()) {
			if (!Amode.equalsIgnoreCase("OpenInv") || mc.currentScreen instanceof GuiInventory) {
				if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory
						|| mc.currentScreen instanceof GuiChat) {
					getBestArmor();
				}
			}
		}
		if (armor.isEnabled())
			for (int type = 1; type < 5; type++) {
				if (Minecraft.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
					ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
					if (!AutoArmor.isBestArmor(is, type)) {
						LOCK = false;
						return;
					}
				} else if (invContainsType(type - 1)) {
					LOCK = false;
					return;
				}
			}
		if (mode.isCurrentMode("OpenInv") && !(mc.currentScreen instanceof GuiInventory)) {
			return;
		}

		if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory
				|| mc.currentScreen instanceof GuiChat) {
			if (timer.check(delay) && weaponSlot >= 36) {

				if (!Minecraft.thePlayer.inventoryContainer.getSlot(weaponSlot).getHasStack()) {
					getBestWeapon(weaponSlot);

				} else {
					if (!isBestWeapon(Minecraft.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) {
						getBestWeapon(weaponSlot);
					}
				}
			}
			if (timer.check(delay) && gappleSlot >= 36) {
				getBestgapple(gappleSlot);
			}
			if (timer.check(delay) && gheadSlot >= 36) {
				getBestHead(gheadSlot);
			}

			if (timer.check(delay) && pickaxeSlot >= 36) {
				getBestPickaxe(pickaxeSlot);
			}
			if (timer.check(delay) && shovelSlot >= 36) {
				getBestShovel(shovelSlot);
			}
			if (timer.check(delay) && axeSlot >= 36) {
				getBestAxe(axeSlot);
			}
			if (timer.check(delay) && INVCLEANER.getValueState())
				for (int i = 9; i < 45; i++) {
					if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
						ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
						if (shouldDrop(is, i)) {
							drop(i);
							timer.reset();
							if (delay > 0)
								break;
						}
					}
				}
		}
		
		LOCK = false;

	}

	public void shiftClick(int slot) {
		Minecraft.playerController.windowClick(Minecraft.thePlayer.inventoryContainer.windowId, slot, 0, 1, Minecraft.thePlayer);

        mc.playerController.updateController();
	}

	public void swap(int slot1, int hotbarSlot) {
		Minecraft.playerController.windowClick(Minecraft.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, Minecraft.thePlayer);
	
		 mc.playerController.updateController();
	}

	public void drop(int slot) {
		Minecraft.playerController.windowClick(Minecraft.thePlayer.inventoryContainer.windowId, slot, 1, 4, Minecraft.thePlayer);
	
		 mc.playerController.updateController();
	}

	public boolean isBestWeapon(ItemStack stack) {
		float damage = getDamage(stack);
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getDamage(is) > damage && (is.getItem() instanceof ItemSword || !SWORD.getValueState()))
					return false;
			}
		}
		if ((stack.getItem() instanceof ItemSword || !SWORD.getValueState())) {
			return true;
		} else {
			return false;
		}

	}

	public void getBestWeapon(int slot) {
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (isBestWeapon(is) && getDamage(is) > 0
						&& (is.getItem() instanceof ItemSword || !SWORD.getValueState())) {
					swap(i, slot - 36);
					timer.reset();
					break;
				}
			}
		}
	}

	private float getDamage(ItemStack stack) {
		float damage = 0;
		Item item = stack.getItem();
		if (item instanceof ItemTool) {
			ItemTool tool = (ItemTool) item;
			damage += tool.getDamage();
		}
		if (item instanceof ItemSword) {
			ItemSword sword = (ItemSword) item;
			damage += sword.getAttackDamage();
		}

		
        final Multimap<String, AttributeModifier> attributeModifierMap = stack.getAttributeModifiers();
        for (final String attributeName : attributeModifierMap.keySet()) {
            if (attributeName.equals("generic.attackDamage")) {
                final Iterator<AttributeModifier> attributeModifiers = attributeModifierMap.get(attributeName).iterator();
                if (attributeModifiers.hasNext()) {
                    damage += attributeModifiers.next().getAmount();
                    break;
                }
                break;
            }
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

	public boolean shouldDrop(ItemStack stack, int slot) {

		Item item = stack.getItem();

		int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
		int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
		int protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
		int infinity = EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack);
		
		
		if(item == Items.iron_sword
				&& (stack.getDisplayName().matches(".*\u00A7aApprentice Sword") || stack.getDisplayName().matches(".*\u00A7a学徒之剑"))
				&& getBestItem(ItemSword.class, Comparator.comparingDouble(this::getDamage)) != stack)
			return true;
		
		if(item == Items.iron_helmet
				&& (stack.getDisplayName().matches(".*\u00A7aApprentice Helmet") || stack.getDisplayName().matches(".*\u00A7a学徒之盔"))
				&& !AutoArmor.isBestArmor(stack, 1))
			return true;

		if(item == Items.rotten_flesh)
			return true;
		
		if(item == Items.spider_eye)
			return true;
		
		if(item == Items.potionitem)
			return false;
		

		if (SkyBlockUtils.isMWgame()) {
			if (stack.hasDisplayName() && stack.getDisplayName().startsWith("§"))
				return false;

			if (item instanceof ItemAppleGold || item instanceof ItemPotion || item instanceof ItemBucketMilk
					|| item instanceof ItemFood || item == Items.beef || item == Items.arrow || item == Items.bow
					|| item.getUnlocalizedName().toLowerCase().contains("ender"))
				return false;
		}

		if (item instanceof ItemSword)
			if ((stack.hasDisplayName() && ((ItemSword) item).material == Item.ToolMaterial.EMERALD)
					|| (getBestItem(ItemSword.class, Comparator.comparingDouble(this::getDamage)) == stack))
				return false;

		if (item instanceof ItemArmor)
			if ((stack.hasDisplayName() && ((ItemArmor) item).material == ArmorMaterial.DIAMOND))
				return false;

		if (item instanceof ItemPickaxe)
			if ((stack.hasDisplayName() && ((ItemPickaxe) item).getToolMaterial() == Item.ToolMaterial.EMERALD))
				return false;

		if (item instanceof ItemAxe)
			if ((stack.hasDisplayName() && ((ItemAxe) item).getToolMaterial() == Item.ToolMaterial.EMERALD))
				return false;

		if (item instanceof ItemSnowball)
			if (stack.hasDisplayName())
				return false;
		
		if (stack.getDisplayName().contains("Wither Skeleton Skull") || stack.getDisplayName().contains("Creeper Head")
				|| stack.getDisplayName().contains("Skeleton Skull")
				|| stack.getDisplayName().contains("Zombie Head")) {
			return true;
		}

		if (item instanceof ItemSkull)
			if (stack.hasDisplayName())
				return false;

		if (item instanceof ItemAnvilBlock)
			return false;

		if (item instanceof ItemFood)
			return false;
		
		if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() == Blocks.crafting_table)
			return false;

		if (stack.getDisplayName().toLowerCase().contains("dragon armor")
				|| stack.getDisplayName().toLowerCase().contains("death's scythe")
				|| stack.getDisplayName().toLowerCase().contains("barbarian chestplate")
				|| stack.getDisplayName().toLowerCase().contains("exodus")
				|| stack.getDisplayName().toLowerCase().contains("exper seal")
				|| stack.getDisplayName().toLowerCase().contains("úril")
				|| stack.getDisplayName().toLowerCase().contains("perun")
				|| stack.getDisplayName().toLowerCase().contains("leviathan")
				|| stack.getDisplayName().toLowerCase().contains("excalibur")
				|| stack.getDisplayName().toLowerCase().contains("\247cFusion")
				|| stack.getDisplayName().toLowerCase().contains("fate")) {
			return false;
		}

		if (stack.getDisplayName().toLowerCase().contains("dragon")) {
			return false;
		}

		if (stack.getDisplayName().toLowerCase().contains("\247a潮汐护腿")
				|| stack.getDisplayName().toLowerCase().contains("\247a永生帽")
				|| stack.getDisplayName().toLowerCase().contains("\247a霹隆之斧")
				|| stack.getDisplayName().toLowerCase().contains("\247a安都瑞尔之剑")
				|| stack.getDisplayName().toLowerCase().contains("\247a丰饶之角")
				|| stack.getDisplayName().toLowerCase().contains("\247a蛮族之甲")
				|| stack.getDisplayName().toLowerCase().contains("a巨龙之剑")
				|| stack.getDisplayName().contains("Axe of Perun")) {
			return false;
		}

		if (stack.getDisplayName().toLowerCase().contains("\247aforge")
				|| stack.getDisplayName().toLowerCase().contains("\247a锻炉")
				|| stack.getDisplayName().toLowerCase().contains("backpack")) {
			return false;
		}
		if (stack.getDisplayName().toLowerCase().contains("bloodlust")) {
			return false;
		}
		
		//The Mark
		if (stack.getItem() instanceof ItemSnowball && stack.hasDisplayName()) {
			return false;
		}
		
		if (stack.getDisplayName().contains("Wither Skeleton Skull") || stack.getDisplayName().contains("Creeper Head")
				|| stack.getDisplayName().contains("Skeleton Skull")
				|| stack.getDisplayName().contains("Zombie Head")) {
			return true;
		}
		if ((Item.getIdFromItem(item) == 276 && (sharpness >= 3 || fireAspect >= 1))) {
			return false;
		}
		if (stack.getItem() instanceof ItemEnchantedBook) {
			NBTTagList nbttaglist = Items.enchanted_book.getEnchantments(stack);
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				int ID = nbttaglist.getCompoundTagAt(i).getShort("id");
				int LVL = nbttaglist.getCompoundTagAt(i).getShort("lvl");
				if (ID == Enchantment.sharpness.effectId && LVL >= 3 || ID == Enchantment.fireAspect.effectId
						|| ID == Enchantment.efficiency.effectId && LVL >= 3 || ID == Enchantment.fortune.effectId
						|| ID == Enchantment.featherFalling.effectId && LVL >= 3
						|| ID == Enchantment.protection.effectId || ID == Enchantment.punch.effectId
						|| ID == Enchantment.flame.effectId || ID == Enchantment.infinity.effectId
						|| ID == Enchantment.depthStrider.effectId)
					return false;
			}
			return true;
		}
		if (stack.getDisplayName().toLowerCase().contains("k||")
				|| stack.getDisplayName().toLowerCase().contains("cornucopia")) {
			return false;
		}
//			if ((slot == weaponSlot1 + AutoSword.ASlot.getValueState().intValue() - 1
//					&& isBestWeapon(mc.thePlayer.inventoryContainer
//							.getSlot(weaponSlot1 + AutoSword.ASlot.getValueState().intValue() - 1).getStack()))) {
//				return false;
//			}
		if ((slot == weaponSlot && isBestWeapon(Minecraft.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack()))
				|| (slot == pickaxeSlot
						&& isBestPickaxe(Minecraft.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack())
						&& pickaxeSlot >= 0)
				|| (slot == axeSlot && isBestAxe(Minecraft.thePlayer.inventoryContainer.getSlot(axeSlot).getStack())
						&& axeSlot >= 0)
				|| (slot == shovelSlot && isBestShovel(Minecraft.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack())
						&& shovelSlot >= 0)) {
			return false;
		}
		if (stack.getItem() instanceof ItemArmor) {
			for (int type = 1; type < 5; type++) {
				if (Minecraft.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
					ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
					if (AutoArmor.isBestArmor(is, type)) {
						continue;
					}
				}
				if (AutoArmor.isBestArmor(stack, type)) {
					return false;
				}
			}
		}
		if (stack.getItem() instanceof ItemBlock && UHC.getValueState()
				&& (blacklistedBlocks.contains(((ItemBlock) stack.getItem()).getBlock()))) {
			return false;
		}
		if (stack.getItem() instanceof ItemBlock && (getBlockCount() > (BLOCKCAP.getValueState()).intValue()
				|| Scaffold.blacklisted.contains(((ItemBlock) stack.getItem()).getBlock()))) {

			if (!OnlyWood.getValueState() || ( !(((ItemBlock) stack.getItem()).getBlock() instanceof BlockLog)
					&& !(((ItemBlock) stack.getItem()).getBlock() instanceof BlockPlanks)))
				return true;
		}
		
		if (stack.getItem() instanceof ItemPotion) {
			if (isBadPotion(stack)) {
				return true;
			}
		}
		if (Item.getIdFromItem(item) == 263 && getCoal() <= 64 && UHC.getValueState()) {
			return false;
		}
		if (Item.getIdFromItem(item) == 345 && getZNZ() <= 1 && UHC.getValueState()) {
			return false;
		}
		if (Item.getIdFromItem(item) == 265 && getiron() <= 64 && UHC.getValueState()) {
			return false;
		}
		if (Item.getIdFromItem(item) == 326 && getSHUI() <= 1 && UHC.getValueState()) {
			return false;
		}
		if ((stack.getItem() instanceof ItemAppleGold)
				|| (stack.getItem().getUnlocalizedName().contains("apple") && UHC.getValueState())) {
			return false;
		}
		if (stack.getItem() instanceof ItemFood && FOOD.getValueState()
				&& !(stack.getItem() instanceof ItemAppleGold)) {
			return true;
		}
		if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool
				|| stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemArmor) {
			
			if(!stack.hasDisplayName() && !AutoArmor.Look)
				return true;
		}
		
		
		if ((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedName().contains("arrow"))
				&& ARCHERY.getValueState()) {
			return true;
		}
		
		if(KeepSpecial.getValueState() && stack.hasDisplayName()) {
			if(stack.getItem() instanceof ItemEnchantedBook && stack.getDisplayName().contains("(Right Click)"))
				return true;
			else
				return false;
		}
		
		if ((
				 (stack.getItem().getUnlocalizedName().contains("egg"))
				|| (stack.getItem().getUnlocalizedName().contains("string"))
				|| (stack.getItem().getUnlocalizedName().contains("cake"))
				|| (stack.getItem().getUnlocalizedName().contains("mushroom"))
				|| (stack.getItem().getUnlocalizedName().contains("flint"))
				|| (stack.getItem().getUnlocalizedName().contains("compass"))
				|| (stack.getItem().getUnlocalizedName().contains("dyePowder"))
				|| (stack.getItem().getUnlocalizedName().contains("feather"))
				|| (stack.getItem().getUnlocalizedName().contains("chest")
						&& !stack.getDisplayName().toLowerCase().contains("collect"))
				|| (stack.getItem().getUnlocalizedName().contains("snow"))
				|| (stack.getItem().getUnlocalizedName().contains("fish"))
				|| (stack.getItem().getUnlocalizedName().contains("shears"))
				|| (stack.getItem().getUnlocalizedName().contains("torch"))
				|| (stack.getItem().getUnlocalizedName().contains("seeds"))
				|| (stack.getItem().getUnlocalizedName().contains("leather"))
				|| (stack.getItem().getUnlocalizedName().contains("reeds"))
				|| (stack.getItem().getUnlocalizedName().contains("Iron") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("Tag") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("slime") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("sign") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("blaze") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("horse") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("coal") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("bone") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("Gunpowder") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("paper") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("book") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("redstone") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("skull") && !UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("record"))
				|| (stack.getItem().getUnlocalizedName().contains("snowball"))
				|| (stack.getItem() instanceof ItemGlassBottle)
				|| (stack.getItem().getUnlocalizedName().contains("piston")))) {
			return true;
		}

		return false;
	}

	private int getCoal() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (Item.getIdFromItem(item) == 263) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private int getSHUI() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (Item.getIdFromItem(item) == 326) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private int getZNZ() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (Item.getIdFromItem(item) == 345) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private int getiron() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (Item.getIdFromItem(item) == 265) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	public ArrayList<Integer> getWhitelistedItem() {
		return whitelistedItems;
	}

	private int getBlockCount() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (is.getItem() instanceof ItemBlock
						&& !Scaffold.blacklisted.contains(((ItemBlock) item).getBlock())) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private void getBestPickaxe(int slot) {
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (isBestPickaxe(is) && pickaxeSlot != i) {
					if (!isBestWeapon(is))
						if (!Minecraft.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getHasStack()) {
							swap(i, pickaxeSlot - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						} else if (!isBestPickaxe(Minecraft.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack())) {
							swap(i, pickaxeSlot - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						}

				}
			}
		}
	}

	private void getBestgapple(int slot) {
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (isgapple(is) && gappleSlot != i) {
					if (!Minecraft.thePlayer.inventoryContainer.getSlot(gappleSlot).getHasStack()) {
						swap(i, gappleSlot - 36);
						timer.reset();
						if (DELAY.getValueState().longValue() > 0)
							return;
					} else if (!isgapple(Minecraft.thePlayer.inventoryContainer.getSlot(gappleSlot).getStack())) {
						swap(i, gappleSlot - 36);
						timer.reset();
						if (DELAY.getValueState().longValue() > 0)
							return;
					}
				}
			}
		}
	}

	private void getBestHead(int slot) {
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (isghead(is) && gheadSlot != i) {
					if (!Minecraft.thePlayer.inventoryContainer.getSlot(gheadSlot).getHasStack()) {
						swap(i, gheadSlot - 36);
						timer.reset();
						if (DELAY.getValueState().longValue() > 0)
							return;
					} else if (!isghead(Minecraft.thePlayer.inventoryContainer.getSlot(gheadSlot).getStack())) {
						swap(i, gheadSlot - 36);
						timer.reset();
						if (DELAY.getValueState().longValue() > 0)
							return;
					}

				}
			}
		}
	}

	private void getBestShovel(int slot) {
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (isBestShovel(is) && shovelSlot != i) {
					if (!isBestWeapon(is))
						if (!Minecraft.thePlayer.inventoryContainer.getSlot(shovelSlot).getHasStack()) {
							swap(i, shovelSlot - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						} else if (!isBestShovel(Minecraft.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack())) {
							swap(i, shovelSlot - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						}

				}
			}
		}
	}

	private void getBestAxe(int slot) {

		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (isBestAxe(is) && axeSlot != i) {
					if (!isBestWeapon(is))
						if (!Minecraft.thePlayer.inventoryContainer.getSlot(axeSlot).getHasStack()) {
							swap(i, axeSlot - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						} else if (!isBestAxe(Minecraft.thePlayer.inventoryContainer.getSlot(axeSlot).getStack())) {
							swap(i, axeSlot - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						}

				}
			}
		}
	}

	private boolean isBestPickaxe(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof ItemPickaxe))
			return false;
		float value = getToolEffect(stack);
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
					return false;
				}

			}
		}
		return true;
	}

	private boolean isgapple(ItemStack stack) {
		Item item = stack.getItem();
		if (!(Item.getIdFromItem(item) == Item.getIdFromItem(Items.golden_apple))) {
			return false;
		}
		float value = getappleEffect(stack);
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getappleEffect(is) > value
						&& Item.getIdFromItem(is.getItem()) == Item.getIdFromItem(Items.golden_apple)) {
					return false;
				}

			}
		}
		return true;
	}

	private boolean isghead(ItemStack stack) {
		Item item = stack.getItem();
		if (!(Item.getIdFromItem(item) == Item.getIdFromItem(Items.skull))) {
			return false;
		}
		if (stack.getItemDamage() != 3) {
			return false;
		}
		if (stack.getDisplayName().contains("Backpack")) {
			return false;
		}

		float value = getHeadEffect(stack);
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getHeadEffect(is) > value && Item.getIdFromItem(is.getItem()) == Item.getIdFromItem(Items.skull)) {
					return false;
				}

			}
		}
		return true;
	}

	private boolean isBestShovel(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof ItemSpade))
			return false;
		float value = getToolEffect(stack);
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
					return false;
				}

			}
		}
		return true;
	}

	private boolean isBestAxe(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof ItemAxe))
			return false;
		float value = getToolEffect(stack);
		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
					return false;
				}

			}
		}
		return true;
	}

	private float getappleEffect(ItemStack stack) {
		Item item = stack.getItem();
		if (!(Item.getIdFromItem(item) == Item.getIdFromItem(Items.golden_apple)))
			return 0;
		if (stack.getRarity() != EnumRarity.EPIC)
			return 10f;
		else
			return 1f;
	}

	private float getHeadEffect(ItemStack stack) {
		Item item = stack.getItem();
		if (!(Item.getIdFromItem(item) == Item.getIdFromItem(Items.skull)))
			return 0;
		if (stack.getDisplayName().contains("\2476Golden Head"))
			return 10f;
		else
			return 1f;
	}

	private float getToolEffect(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof ItemTool))
			return 0;
		String name = item.getUnlocalizedName();
		ItemTool tool = (ItemTool) item;
		float value = 1;
		if (item instanceof ItemPickaxe) {
			value = tool.getStrVsBlock(stack, Blocks.stone);
			if (name.toLowerCase().contains("gold")) {
				value -= 5;
			}
		} else if (item instanceof ItemSpade) {
			value = tool.getStrVsBlock(stack, Blocks.dirt);
			if (name.toLowerCase().contains("gold")) {
				value -= 5;
			}
		} else if (item instanceof ItemAxe) {
			value = tool.getStrVsBlock(stack, Blocks.log);
			if (name.toLowerCase().contains("gold")) {
				value -= 5;
			}
		} else
			return 1f;
		value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D;
		value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100d;
		
		if(stack.isItemStackDamageable() && stack.isItemDamaged())
			value -= stack.getItemDamage() * 0.000005;
		
		return value;
	}

	private boolean isBadPotion(ItemStack stack) {
		if (stack != null && stack.getItem() instanceof ItemPotion) {
			final ItemPotion potion = (ItemPotion) stack.getItem();
			if (potion.getEffects(stack) == null)
				return true;
			for (final Object o : potion.getEffects(stack)) {
				final PotionEffect effect = (PotionEffect) o;
				if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId()
						|| effect.getPotionID() == Potion.moveSlowdown.getId()
						|| effect.getPotionID() == Potion.weakness.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	boolean invContainsType(int type) {

		for (int i = 9; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (item instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) item;
					if (type == armor.armorType) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void getBestArmor() {
		for (int type = 1; type < 5; type++) {
			if (Minecraft.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
				ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
				if (AutoArmor.isBestArmor(is, type)) {
					continue;
				} else {
					drop(4 + type);
				}
			}
			for (int i = 9; i < 45; i++) {
				if (Minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
					if (AutoArmor.isBestArmor(is, type) && AutoArmor.getProtValue(is) > 0) {
						shiftClick(i);
						timer.reset();
						if (DELAY.getValueState().longValue() > 0)
							return;
					}
				}
			}
		}
	}

	private ItemStack getBestItem(Class<? extends Item> itemType, Comparator comparator) {
		@SuppressWarnings("unchecked")
		Optional<ItemStack> bestItem = ((List<Slot>) Minecraft.thePlayer.inventoryContainer.inventorySlots).stream()
				.map(Slot::getStack).filter(Objects::nonNull)
				.filter(itemStack -> itemStack.getItem().getClass().equals(itemType)).max(comparator);

		return bestItem.orElse(null);
	}


}
