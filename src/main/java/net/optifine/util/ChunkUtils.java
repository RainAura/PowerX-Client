package net.optifine.util;

import java.util.Iterator;
import java.util.List;
import net.minecraft.src.Config;
import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.world.World;
import net.minecraft.util.BlockPos;
import net.optifine.reflect.Reflector;
import net.minecraft.world.chunk.Chunk;
import net.optifine.reflect.ReflectorField;
import net.optifine.reflect.ReflectorClass;

public class ChunkUtils
{
    
    public static int getPrecipitationHeight(final Chunk chunk, final BlockPos pos) {
        final int[] precipitationHeightMap = chunk.precipitationHeightMap;
        if (precipitationHeightMap == null || precipitationHeightMap.length != 256) {
            return -1;
        }
        final int cx = pos.getX() & 0xF;
        final int cz = pos.getZ() & 0xF;
        final int ix = cx | cz << 4;
        final int y = precipitationHeightMap[ix];
        if (y >= 0) {
            return y;
        }
        final BlockPos posPrep = chunk.getPrecipitationHeight(pos);
        return posPrep.getY();
    }

}
