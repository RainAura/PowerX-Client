package net.optifine.model;

import com.google.common.collect.ImmutableList;
import net.optifine.NaturalTextures;
import net.optifine.ConnectedTextures;
import net.optifine.BetterGrass;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.optifine.SmartLeaves;
import net.optifine.render.RenderEnv;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import java.util.List;

public class BlockModelCustomizer
{
    private static final List<BakedQuad> NO_QUADS;
    
    public static IBakedModel getRenderModel(IBakedModel modelIn, final IBlockState stateIn, final RenderEnv renderEnv) {
        if (renderEnv.isSmartLeaves()) {
            modelIn = SmartLeaves.getLeavesModel(modelIn, stateIn);
        }
        return modelIn;
    }
    
    public static List<BakedQuad> getRenderQuads(List<BakedQuad> quads, final IBlockAccess worldIn, final IBlockState stateIn, final BlockPos posIn, final EnumFacing enumfacing, final EnumWorldBlockLayer layer, final long rand, final RenderEnv renderEnv) {
        if (enumfacing != null) {
            if (renderEnv.isSmartLeaves() && SmartLeaves.isSameLeaves(worldIn.getBlockState(posIn.offset(enumfacing)), stateIn)) {
                return BlockModelCustomizer.NO_QUADS;
            }
            if (!renderEnv.isBreakingAnimation(quads) && Config.isBetterGrass()) {
                quads = BetterGrass.getFaceQuads(worldIn, stateIn, posIn, enumfacing, quads);
            }
        }
        final List<BakedQuad> quadsNew = renderEnv.getListQuadsCustomizer();
        quadsNew.clear();
        for (int i = 0; i < quads.size(); ++i) {
            final BakedQuad quad = quads.get(i);
            final BakedQuad[] quadArr = getRenderQuads(quad, worldIn, stateIn, posIn, enumfacing, rand, renderEnv);
            if (i == 0 && quads.size() == 1 && quadArr.length == 1 && quadArr[0] == quad && quad.getQuadEmissive() == null) {
                return quads;
            }
            for (int q = 0; q < quadArr.length; ++q) {
                final BakedQuad quadSingle = quadArr[q];
                quadsNew.add(quadSingle);
                if (quadSingle.getQuadEmissive() != null) {
                    renderEnv.getListQuadsOverlay(getEmissiveLayer(layer)).addQuad(quadSingle.getQuadEmissive(), stateIn);
                    renderEnv.setOverlaysRendered(true);
                }
            }
        }
        return quadsNew;
    }
    
    private static EnumWorldBlockLayer getEmissiveLayer(final EnumWorldBlockLayer layer) {
        if (layer == null || layer == EnumWorldBlockLayer.SOLID) {
            return EnumWorldBlockLayer.CUTOUT_MIPPED;
        }
        return layer;
    }
    
    private static BakedQuad[] getRenderQuads(BakedQuad quad, final IBlockAccess worldIn, final IBlockState stateIn, final BlockPos posIn, final EnumFacing enumfacing, final long rand, final RenderEnv renderEnv) {
        if (renderEnv.isBreakingAnimation(quad)) {
            return renderEnv.getArrayQuadsCtm(quad);
        }
        final BakedQuad quadOriginal = quad;
        if (Config.isConnectedTextures()) {
            final BakedQuad[] quads = ConnectedTextures.getConnectedTexture(worldIn, stateIn, posIn, quad, renderEnv);
            if (quads.length != 1 || quads[0] != quad) {
                return quads;
            }
        }
        if (Config.isNaturalTextures()) {
            quad = NaturalTextures.getNaturalTexture(posIn, quad);
            if (quad != quadOriginal) {
                return renderEnv.getArrayQuadsCtm(quad);
            }
        }
        return renderEnv.getArrayQuadsCtm(quad);
    }
    
    static {
        NO_QUADS = (List)ImmutableList.of();
    }
}
