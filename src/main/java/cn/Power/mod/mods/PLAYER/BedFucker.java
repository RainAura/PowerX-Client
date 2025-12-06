package cn.Power.mod.mods.PLAYER;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRespawn;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Scaffold;
import cn.Power.notification.Notification.Type;
import cn.Power.util.Colors;
import cn.Power.util.CombatUtil;
import cn.Power.util.Helper;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

public class BedFucker extends Mod {
	ArrayList<BlockPos> whiteList = new ArrayList<BlockPos>();
	public BlockPos currentPos = null;
	private TimeHelper timer = new TimeHelper();
	public static Value<String> mode = new Value("BedFucker", "Mode", 0);
	private Value<Double> reach = new Value<Double>("BedFucker_Reach", 6.0, 1.0, 6.0, 0.1);
	private Value<Double> delay = new Value<Double>("BedFucker_Delay", 120.0, 0.0, 1000.0, 10.0);
	private Value<Boolean> teleport = new Value<Boolean>("BedFucker_Teleport", false);

	public BedFucker() {
		super("BedFucker", Category.PLAYER);
		BedFucker.mode.mode.add("Bed");
		BedFucker.mode.mode.add("Egg");
		BedFucker.mode.mode.add("Cake");
	}

	@EventTarget
	public void onPre(EventPreMotion event) {
		this.standartDestroyer(event);
	}

	@EventTarget
	public void onPre(EventRespawn event) {
		whiteList = new ArrayList<BlockPos>();
		currentPos = null;
	}

	@EventTarget
	public void onChat(EventPacket e) {
		if (e.getEventType() == EventType.RECEIVE) {
			if (e.getPacket() instanceof S02PacketChat) {
				S02PacketChat pk = (S02PacketChat) e.getPacket();

				if (pk.getChatComponent().toString()
						.contains("TextComponent{text='You can't destroy your own bed!', sib")) {

					if (currentPos != null) {

						System.out.println(pk.getChatComponent().toString());
						this.whiteList.add(currentPos);
					}

				}
			}
		}
	}

	private void standartDestroyer(EventPreMotion event) {

		if (SkyBlockUtils.isMWgame() || ModManager.getModByClass(Scaffold.class).isEnabled())
			return;

		Iterator<BlockPos> positions = BlockPos.getAllInBox(
				this.mc.thePlayer.getPosition().subtract(
						new Vec3i(this.reach.getValueState(), this.reach.getValueState(), this.reach.getValueState())),
				this.mc.thePlayer.getPosition().add(
						new Vec3i(this.reach.getValueState(), this.reach.getValueState(), this.reach.getValueState())))
				.iterator();
		BlockPos bedPos = null;

		try {
			while ((bedPos = positions.next()) != null) {
				if ((this.mc.theWorld.getBlockState(bedPos).getBlock() instanceof BlockBed && mode.isCurrentMode("Bed")
						|| this.mc.theWorld.getBlockState(bedPos).getBlock() instanceof BlockDragonEgg
								&& mode.isCurrentMode("Egg")
						|| this.mc.theWorld.getBlockState(bedPos).getBlock() instanceof BlockCake
								&& mode.isCurrentMode("Cake"))
						&& !this.whiteList.contains(bedPos))
					break;
			}

		} catch (Throwable c) {

			currentPos = null;

			return;
		}
		if (!(bedPos instanceof BlockPos)) {

			currentPos = null;

			return;
		}

        this.mc.thePlayer.swingItem();
        
        this.mc.playerController.onPlayerDamageBlock(bedPos, getFacing(bedPos));
    	
    	
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
 //           if(Helper.mc().thePlayer.canEntityBeSeen(temp)) {
                return facing;
 //           }
        }

        return null;
    }
}
