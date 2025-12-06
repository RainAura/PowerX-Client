package net.optifine.util;

import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.client.renderer.chunk.RenderChunk;

public class RenderChunkUtils
{
    public static int getCountBlocks(final RenderChunk renderChunk) {
        final ExtendedBlockStorage[] ebss = renderChunk.getChunk().getBlockStorageArray();
        if (ebss == null) {
            return 0;
        }
        final int indexEbs = renderChunk.getPosition().getY() >> 4;
        final ExtendedBlockStorage ebs = ebss[indexEbs];
        if (ebs == null) {
            return 0;
        }
        return ebs.getBlockRefCount();
    }
    
    public static double getRelativeBufferSize(final RenderChunk renderChunk) {
        final int blockCount = getCountBlocks(renderChunk);
        final double vertexCountRel = getRelativeBufferSize(blockCount);
        return vertexCountRel;
    }
    
    public static double getRelativeBufferSize(final int blockCount) {
        double countRel = blockCount / 4096.0;
        countRel *= 0.995;
        double weight = countRel * 2.0 - 1.0;
        weight = MathHelper.clamp_double(weight, -1.0, 1.0);
        return MathHelper.sqrt_double(1.0 - weight * weight);
    }
}
