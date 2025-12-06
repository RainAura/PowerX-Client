package cn.Power.mod.mods.PLAYER;

import java.util.Iterator;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.PlayerUtil;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

public class FastEat extends Mod {
	
	int tick ;
	boolean tickb = false;
	public FastEat() {
		super("FastEat", Category.PLAYER);
	}

	@native0
	public void donative1() {
		int currentItem = mc.thePlayer.inventory.currentItem;
		mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(currentItem));
		
		Minecraft.getNetHandler().getNetworkManager()
			.sendPacket(new C03PacketPlayer(Minecraft.thePlayer.onGround));
		
		mc.thePlayer.itemInUseCount = 32;
		
	}

	@EventTarget
	public void onUpdate(EventUpdate u) {
		if(mc.thePlayer == null) {
			return;
		}
		if(mc.thePlayer.inventory == null) {
			return;
		}
		if (mc.thePlayer.inventory.getCurrentItem() == null) {
			return;
		}

		Item item = mc.thePlayer.getHeldItem().getItem();
		if(item instanceof ItemFood || item instanceof ItemBucketMilk || item instanceof ItemPotion) {
			if (mc.gameSettings.keyBindUseItem.pressed) {
				if(!(mc.thePlayer.itemInUseCount >= 32)) return;
				
				if(!(ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() != 47)) {
					if(!(mc.thePlayer.itemInUseCount == 32)) return;
					 ClientUtil.sendChatMessage(String.valueOf("FastEat : Pls Use ViaVersion 1.11+ !"), ChatType.INFO);
					return;
				}
				
				donative1();
			}
		}

	}

}