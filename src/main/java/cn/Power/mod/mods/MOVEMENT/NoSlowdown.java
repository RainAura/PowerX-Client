package cn.Power.mod.mods.MOVEMENT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.util.MathUtils;
import cn.Power.util.PlayerUtil;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowdown extends Mod {
	public static Value<String> mode = new Value("NoSlowdown", "Mode", 0);

	public int tick;
	private boolean is_holding_sword() {
		return mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
	}
	public NoSlowdown() {
		super("NoSlowdown", Category.MOVEMENT);
		mode.addValue("Hypixel");
	}

	@EventTarget(4)
	public void onPre(EventPreMotion pre) {
		this.setDisplayName(this.mode.getModeAt(this.mode.getCurrentMode()));
		if (this.mc.thePlayer.isBlocking() && PlayerUtil.isMoving2() && MathUtils.isOnGround(0.01)
				&& KillAura.AutoBlockEntity == null && mode.isCurrentMode("Hypixel")) {
			PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
			useItem.write(Type.VAR_INT, 1);
			PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
			mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
		}
	}

	@EventTarget(4)
	public void onPost(EventPostMotion post) {
		if (KillAura.AutoBlockEntity == null && mode.isCurrentMode("Hypixel")) {
			if (!mc.thePlayer.isBlocking() || !mc.thePlayer.isMoving()) {

			}
		}
	}
	
	@EventTarget
	public void openPacket(EventPacket e) {
		if(e.getPacket() instanceof C07PacketPlayerDigging) {
			C07PacketPlayerDigging pk = (C07PacketPlayerDigging)e.getPacket();
		}
	}
	@EventTarget
	public void onPacketOutbounding(final EventPacket event) {
			if (event.getPacket() instanceof C02PacketUseEntity) {
				C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity) event.getPacket();
				if (c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
				}
			}
			if (is_holding_sword()) {
				if (event.getPacket() instanceof C07PacketPlayerDigging) {
					C07PacketPlayerDigging c07 = (C07PacketPlayerDigging) event.getPacket();
					if (c07.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
					}
				}
			}

		}

	@Override
	public void onDisable() {
		super.onDisable();

	}

	public void onEnable() {
		super.isEnabled();

	}

}
