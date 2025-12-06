package cn.Power.mod.mods.WORLD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventRender;
import cn.Power.events.EventRespawn;
import cn.Power.events.EventTick;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.util.Helper;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.Timer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;

public class ChestStealer extends Mod {

	private Timer timer = new Timer();
	private Timer stealTimer = new Timer();
	private boolean isStealing;
	public Value<Double> delay = new Value("Cheststeal_Delay", 2.0D, 1.0D, 10.0D, 1.0D);
	public Value<Boolean> silent = new Value<Boolean>("Cheststeal_Silent", false);
	public Value<Boolean> ignore = new Value("Cheststeal_IGNore", true);
	public Value<Boolean> drop = new Value("Cheststeal_Drop", false);
	public Value<Boolean> close = new Value("Cheststeal_Close", true);
	public Value<Boolean> chestaura = new Value("Cheststeal_ChestAura", false);
	public Value<Boolean> mega = new Value("Cheststeal_MegaWalls", false);


	public static GuiScreen GuiChest;
	
	public ChestStealer() {
		super("ChestSteal", Category.WORLD);
	}

	@EventTarget(0)
	public void Tick(EventRender event) {
		if (mc.currentScreen instanceof GuiChest) 
		if(silent.getValueState()) {
		KeyBinding[] key = { this.mc.gameSettings.keyBindForward, this.mc.gameSettings.keyBindBack,
				this.mc.gameSettings.keyBindLeft, this.mc.gameSettings.keyBindRight,
				this.mc.gameSettings.keyBindSprint, this.mc.gameSettings.keyBindJump };
		KeyBinding[] array;
		if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof GuiChat)) {
			for (int length = (array = key).length, i = 0; i < length; ++i) {
				KeyBinding b = array[i];
				KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
			}
		} else if (Objects.isNull(mc.currentScreen)) {
			for (int length = (array = key).length, i = 0; i < length; ++i) {
				KeyBinding b = array[i];
				if (!Keyboard.isKeyDown(b.getKeyCode())) {
					KeyBinding.setKeyBindState(b.getKeyCode(), false);
				}
			}
	}
		}
		if (mc.currentScreen instanceof GuiChest) {
			GuiChest guiChest = (GuiChest) mc.currentScreen;
			String name = guiChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase();
			String GuiName = guiChest.lowerChestInventory.getDisplayName().getUnformattedText();

			if (StatCollector.translateToLocal("container.chest").equalsIgnoreCase(GuiName) || StatCollector.translateToLocal("container.chestDouble").equalsIgnoreCase(GuiName)) {
				GuiChest = mc.currentScreen;
				if(silent.getValueState())
				this.mc.displayGuiScreen(null);
			}
		
		}
		
	}
	
    public static EnumFacing getFacing(BlockPos pos) {
        EnumFacing[] orderedValues = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.DOWN};
        EnumFacing[] var2 = orderedValues;
        int var3 = orderedValues.length;
        for(int var4 = 0; var4 < var3; ++var4) {
            EnumFacing facing = var2[var4];
            EntitySnowball temp = new EntitySnowball(Helper.world());
            temp.posX = (double)pos.getX() + 0.5D;
            temp.posY = (double)pos.getY() + 0.5D;
            temp.posZ = (double)pos.getZ() + 0.5D;
            temp.posX += (double)facing.getDirectionVec().getX() * 0.5D;
            temp.posY += (double)facing.getDirectionVec().getY() * 0.5D;
            temp.posZ += (double)facing.getDirectionVec().getZ() * 0.5D;
            if(Helper.mc().thePlayer.canEntityBeSeen(temp)) {
                return facing;
            }
        }

        return null;
    }
    
	@EventTarget
	public void onUpdate(EventUpdate event) {
		
	
		
		try {
		if (chestaura.getValueState()) {
			if (stealTimer.check(1500) && isStealing) {
				stealTimer.reset();
				isStealing = false;
			}

			for (Object o : mc.theWorld.loadedTileEntityList) {
				if (o instanceof TileEntityChest) {
					TileEntityChest chest = (TileEntityChest) o;
					float x = chest.getPos().getX();
					float y = chest.getPos().getY();
					float z = chest.getPos().getZ();
					if (chest.numPlayersUsing == 0 && !chest.isEmpty && mc.thePlayer.getDistance(x, y, z) < 4.0 && stealTimer.check(1700)
							&& GuiChest == null) {
				    	if(KillAura.Target == null && getFacing(chest.getPos()) != null) {
						
				    		if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, null, chest.getPos(), getFacing(chest.getPos()), 
				    				new Vec3( x + 0.6 - ThreadLocalRandom.current().nextInt(4) / 10,  y + 0.5 + ThreadLocalRandom.current().nextInt(4) / 10, z + 0.7 -ThreadLocalRandom.current().nextInt(4) / 10))) {
				    			chest.isEmpty = true;
				    			stealTimer.reset();
				    		}
				    	}
					} 
				}
			}
		}
			
//		if (mc.currentScreen instanceof GuiChest) {
//			GuiChest guiChest = (GuiChest) mc.currentScreen;
//			String name = guiChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase();
//			String GuiName = guiChest.lowerChestInventory.getDisplayName().getUnformattedText();
//
//			if (StatCollector.translateToLocal("container.chest").equalsIgnoreCase(GuiName) || StatCollector.translateToLocal("container.chestDouble").equalsIgnoreCase(GuiName)) {
//				GuiChest = mc.currentScreen;
//				if(silent.getValueState())
//				this.mc.displayGuiScreen(null);
//			}
//			
//		
//		}
			
			if (GuiChest instanceof GuiChest) {
			GuiChest guiChest = (GuiChest) this.GuiChest;
			String name = guiChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase();
			String GuiName = guiChest.lowerChestInventory.getDisplayName().getUnformattedText();

			if (StatCollector.translateToLocal("container.chest").equalsIgnoreCase(GuiName)
					|| StatCollector.translateToLocal("container.chestDouble").equalsIgnoreCase(GuiName)) {

				isStealing = true;
				boolean full = true;
				ItemStack[] arrayOfItemStack;
				int j = (arrayOfItemStack = mc.thePlayer.inventory.mainInventory).length;
				for (int i = 0; i < j; i++) {
					ItemStack item = arrayOfItemStack[i];
					if (item == null) {
						full = false;
						break;
					}
				}
				
		
				
				boolean containsItems = false;
				if (!full) {
					for (int index = guiChest.lowerChestInventory.getSizeInventory() + 1; index > -1; index--) {
						ItemStack stack = guiChest.lowerChestInventory.getStackInSlot(index);
						if (stack != null && !isBad(stack)) {
							containsItems = true;
							break;
						}
					}

					if (containsItems) {
						
						
						
						List<Integer> List = new ArrayList<Integer>();
						for (int index = guiChest.lowerChestInventory.getSizeInventory() + 1; index > -1; index--) {
							ItemStack stack = guiChest.lowerChestInventory.getStackInSlot(index);
							if (stack != null && !isBad(stack)) {
								List.add(index);
							}
							Collections.shuffle(List);
						}
						for (int i = 0; i < List.size(); i++) {
							int DELAY = 50 * delay.getValueState().intValue();
							if (timer.check(DELAY)) {
								
								
								
								timer.reset();
								mc.playerController.windowClick(guiChest.inventorySlots.windowId, List.get(i), 1,drop.getValueState() ? 0 : 1, mc.thePlayer);
								if (drop.getValueState()) {
									mc.playerController.windowClick(guiChest.inventorySlots.windowId, -999, 0, 0,mc.thePlayer);
								}
								
								mc.playerController.updateController();
								
							}
						}
					} else if (((Boolean) close.getValueState())) {
						mc.thePlayer.closeScreen(guiChest.inventorySlots.windowId);
						GuiChest = null;
						isStealing = false;
					}
				} else if (((Boolean) close.getValueState())) {
					mc.thePlayer.closeScreen(guiChest.inventorySlots.windowId);
					GuiChest = null;
					isStealing = false;
				}
			} else {
				isStealing = false;
				}
			}
		}catch(Throwable c) {c.printStackTrace();}
	}

	private EnumFacing getFacingDirection(final BlockPos pos) {
		EnumFacing direction = null;
		if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isBlockNormalCube()) {
			direction = EnumFacing.UP;
		}
		final MovingObjectPosition rayResult = mc.theWorld.rayTraceBlocks(
				new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
				new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
		if (rayResult != null) {
			return rayResult.sideHit;
		}
		return direction;
	}

	private boolean isBad(ItemStack item) {
		
		ItemStack is = null;
		
		if(this.mega.getValueState() && item != null && SkyBlockUtils.isMWgame()) {
			if(!(item.getItem() instanceof ItemPotion) && !(item.getItem() instanceof ItemAppleGold)) {
				return true;
			}
		}
		
		if (!(Boolean) ignore.getValueState())
			return false;

		float lastDamage = -1;
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is1 = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (is1.getItem() instanceof ItemSword && item.getItem() instanceof ItemSword) {
					if (lastDamage < getDamage(is1)) {
						lastDamage = getDamage(is1);
						is = is1;
					}
				}
			}
		}

		if (is != null && is.getItem() instanceof ItemSword && item.getItem() instanceof ItemSword) {
			float currentDamage = getDamage(is);
			float itemDamage = getDamage(item);
			if (itemDamage > currentDamage) {
				return false;
			}
		}

		return item != null && ((item.getItem().getUnlocalizedName().contains("tnt"))
				|| (item.getItem().getUnlocalizedName().contains("stick"))
				|| (item.getItem().getUnlocalizedName().contains("egg")
						&& !item.getItem().getUnlocalizedName().contains("leg"))
				|| (item.getItem().getUnlocalizedName().contains("string"))
				|| (item.getItem().getUnlocalizedName().contains("flint"))
				|| (item.getItem().getUnlocalizedName().contains("compass"))
				|| (item.getItem().getUnlocalizedName().contains("feather"))
				|| (item.getItem().getUnlocalizedName().contains("bucket"))
				|| (item.getItem().getUnlocalizedName().contains("snow"))
				|| (item.getItem().getUnlocalizedName().contains("fish"))
				|| (item.getItem().getUnlocalizedName().contains("enchant"))
				|| (item.getItem().getUnlocalizedName().contains("exp"))
				|| (item.getItem().getUnlocalizedName().contains("shears"))
				|| (item.getItem().getUnlocalizedName().contains("anvil"))
				|| (item.getItem().getUnlocalizedName().contains("torch"))
				|| (item.getItem().getUnlocalizedName().contains("seeds"))
				|| (item.getItem().getUnlocalizedName().contains("leather"))
				|| ((item.getItem() instanceof ItemPickaxe)) || ((item.getItem() instanceof ItemGlassBottle))
				|| ((item.getItem() instanceof ItemTool)) || (item.getItem().getUnlocalizedName().contains("piston"))
				|| ((item.getItem().getUnlocalizedName().contains("potion")) && (isBadPotion(item))));
	}

	private boolean isBadPotion(ItemStack stack) {
		if (stack != null && stack.getItem() instanceof ItemPotion) {
			final ItemPotion potion = (ItemPotion) stack.getItem();
			if (ItemPotion.isSplash(stack.getMetadata())) {
				for (final Object o : potion.getEffects(stack)) {
					final PotionEffect effect = (PotionEffect) o;
					if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId()
							|| effect.getPotionID() == Potion.moveSlowdown.getId()
							|| effect.getPotionID() == Potion.weakness.getId()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private float getDamage(ItemStack stack) {
		if (!(stack.getItem() instanceof ItemSword)) {
			return 0;
		}
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f
				+ ((ItemSword) stack.getItem()).getDamageVsEntity();
	}
}
