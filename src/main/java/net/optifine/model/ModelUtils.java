package net.optifine.model;

import net.minecraft.client.resources.model.SimpleBakedModel;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import java.util.List;
import net.minecraft.util.EnumFacing;
import net.minecraft.src.Config;
import net.minecraft.client.resources.model.IBakedModel;

public class ModelUtils
{
    public static void dbgModel(final IBakedModel model) {
        if (model == null) {
            return;
        }
        Config.dbg("Model: " + model + ", ao: " + model.isAmbientOcclusion() + ", gui3d: " + model.isGui3d() + ", builtIn: " + model.isBuiltInRenderer() + ", particle: " + model.getParticleTexture());
        final EnumFacing[] faces = EnumFacing.VALUES;
        for (int i = 0; i < faces.length; ++i) {
            final EnumFacing face = faces[i];
            final List faceQuads = model.getFaceQuads(face);
            dbgQuads(face.getName(), faceQuads, "  ");
        }
        final List generalQuads = model.getGeneralQuads();
        dbgQuads("General", generalQuads, "  ");
    }
    
    private static void dbgQuads(final String name, final List<BakedQuad> quads, final String prefix) {
        for (final BakedQuad quad : quads) {
            dbgQuad(name, quad, prefix);
        }
    }
    
    public static void dbgQuad(final String name, final BakedQuad quad, final String prefix) {
        Config.dbg(prefix + "Quad: " + quad.getClass().getName() + ", type: " + name + ", face: " + quad.getFace() + ", tint: " + quad.getTintIndex() + ", sprite: " + quad.getSprite());
        dbgVertexData(quad.getVertexData(), "  " + prefix);
    }
    
    public static void dbgVertexData(final int[] vd, final String prefix) {
        final int step = vd.length / 4;
        Config.dbg(prefix + "Length: " + vd.length + ", step: " + step);
        for (int i = 0; i < 4; ++i) {
            final int pos = i * step;
            final float x = Float.intBitsToFloat(vd[pos + 0]);
            final float y = Float.intBitsToFloat(vd[pos + 1]);
            final float z = Float.intBitsToFloat(vd[pos + 2]);
            final int col = vd[pos + 3];
            final float u = Float.intBitsToFloat(vd[pos + 4]);
            final float v = Float.intBitsToFloat(vd[pos + 5]);
            Config.dbg(prefix + i + " xyz: " + x + "," + y + "," + z + " col: " + col + " u,v: " + u + "," + v);
        }
    }
    
    public static IBakedModel duplicateModel(final IBakedModel model) {
        final List generalQuads2 = duplicateQuadList(model.getGeneralQuads());
        final EnumFacing[] faces = EnumFacing.VALUES;
        final List faceQuads2 = new ArrayList();
        for (int i = 0; i < faces.length; ++i) {
            final EnumFacing face = faces[i];
            final List quads = model.getFaceQuads(face);
            final List quads2 = duplicateQuadList(quads);
            faceQuads2.add(quads2);
        }
        final SimpleBakedModel model2 = new SimpleBakedModel(generalQuads2, faceQuads2, model.isAmbientOcclusion(), model.isGui3d(), model.getParticleTexture(), model.getItemCameraTransforms());
        return model2;
    }
    
    public static List duplicateQuadList(final List<BakedQuad> list) {
        final List list2 = new ArrayList();
        for (final BakedQuad quad : list) {
            final BakedQuad quad2 = duplicateQuad(quad);
            list2.add(quad2);
        }
        return list2;
    }
    
    public static BakedQuad duplicateQuad(final BakedQuad quad) {
        final BakedQuad quad2 = new BakedQuad(quad.getVertexData().clone(), quad.getTintIndex(), quad.getFace(), quad.getSprite());
        return quad2;
    }
}
