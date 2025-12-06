package cn.Power.mod.mods.PLAYER;

import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;

public class Kick extends Mod {

	public Kick() {
		super("Kick", Category.PLAYER);

	}

	@Override
	public void onEnable() {
		PacketWrapper exloit = PacketWrapper.create(0x99, null, mc.getNetHandler().getNetworkManager().user);
		exloit.write(Type.DOUBLE,  1.0);
		
		try {
			exloit.scheduleSendToServer(Protocol1_8To1_9.class, true);
		} catch (Throwable e) {
		}
		

		
	}

}
