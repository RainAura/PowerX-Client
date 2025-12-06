package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.notification.Notification.Type;
import cn.Power.util.friendManager.Friend;
import cn.Power.util.friendManager.FriendManager;
import cn.Power.util.handler.MouseInputHandler;
import net.minecraft.entity.player.EntityPlayer;

public class MCF extends Mod {
	private MouseInputHandler handler = new MouseInputHandler(2);

	public MCF() {
		super("MCF", Category.WORLD);
	}

	@EventTarget
	public void onUpdate(EventPreMotion event) {
		if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.entityHit != null
				&& this.mc.objectMouseOver.entityHit instanceof EntityPlayer) {
			String name = this.mc.objectMouseOver.entityHit.getName();
			if (this.handler.canExcecute()) {
				if (FriendManager.isFriend(name)) {
					for (int i = 0; i < FriendManager.getFriends().size(); ++i) {
						Friend f = (Friend) FriendManager.getFriends().get(i);
						if (f.getName().equalsIgnoreCase(name)) {
							Client.instance.getNotificationManager().addNotification("Remove MCF" + name, Type.WARNING);
							FriendManager.getFriends().remove(i);
						}
					}
				} else {
					Client.instance.getNotificationManager().addNotification("Add MCF" + name, Type.INFO);
					FriendManager.getFriends().add(new Friend(name, name));
				}
				Client.instance.fileMgr.saveFriends();

			}
		}

	}

}
