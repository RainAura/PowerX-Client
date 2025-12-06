package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Fly;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class BetterBeacons extends Mod {
	
	public Value<String> Mode = new Value<String>("BeaconsControl", "Effect", 0);

	public BetterBeacons() {
		super("BeaconsControl", Category.WORLD);
		
		this.Mode.mode.add("SPEED");
		this.Mode.mode.add("HASTE");
		this.Mode.mode.add("RESISTANCE");
		this.Mode.mode.add("JUMP_BOOST");
		this.Mode.mode.add("STRENGTH");
	}

	@EventTarget
	public void onPacket(EventPacket event) {
		if (event.getPacket() instanceof C17PacketCustomPayload) {
			
			C17PacketCustomPayload p = (C17PacketCustomPayload) event.getPacket();
			
			if(p.getChannelName().equals("MC|Beacon")) {
				
				event.setCancelled(true);
				
                PacketBuffer data = p.getBufferData();
                int i1 = data.readInt();
                int k1 = data.readInt();
                
                PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                buf.writeInt(this.getPotionID());
                buf.writeInt(k1);
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C17PacketCustomPayload("MC|Beacon", buf));
			}
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
	
    private int getPotionID() {
        switch (this.Mode.getModeAt(this.Mode.getCurrentMode())) {
            case "SPEED": {
                return 1;
            }
            case "HASTE": {
                return 3;
            }
            case "RESISTANCE": {
                return 11;
            }
            case "JUMP_BOOST": {
                return 8;
            }
            case "STRENGTH": {
                return 5;
            }
            default: {
                return -1;
            }
        }
    }
    
    private enum Effects
    {
        SPEED, 
        HASTE, 
        RESISTANCE, 
        JUMP_BOOST, 
        STRENGTH;
    }
}
