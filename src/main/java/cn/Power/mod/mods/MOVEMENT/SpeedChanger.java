package cn.Power.mod.mods.MOVEMENT;

import java.util.Iterator;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.util.PlayerUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import cn.Power.mod.mods.MOVEMENT.Speed;

public class SpeedChanger extends Mod {
	private Value<Double> id_ena = new Value<Double>("SpeedChanger_EnableMode", 8.0, 1.0, 11.0, 1);
	private Value<Double> id_dis = new Value<Double>("SpeedChanger_DisableMode", 2.0, 1.0, 11.0, 1);
	
	
	public SpeedChanger() {
		super("SpeedChanger", Category.MOVEMENT);
	}

}