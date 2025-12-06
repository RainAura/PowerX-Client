package net.optifine.model;

import net.minecraft.util.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import java.util.Iterator;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import org.lwjgl.util.vector.Vector3f;
import java.util.Collection;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.BakedQuad;
import java.util.List;
import net.minecraft.util.EnumFacing;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.client.resources.model.IBakedModel;

public class BlockModelUtils
{
    private static final float VERTEX_COORD_ACCURACY = 1.0E-6f;
    
    public static IBakedModel makeModelCube(final String spriteName, final int tintIndex) {
        final TextureAtlasSprite sprite = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(spriteName);
        return makeModelCube(sprite, tintIndex);
    }
    
    public static IBakedModel makeModelCube(final TextureAtlasSprite sprite, final int tintIndex) {
        final List generalQuads = new ArrayList();
        final EnumFacing[] facings = EnumFacing.VALUES;
        final List<List<BakedQuad>> faceQuads = new ArrayList<List<BakedQuad>>();
        for (int i = 0; i < facings.length; ++i) {
            final EnumFacing facing = facings[i];
            final List quads = new ArrayList();
            quads.add(makeBakedQuad(facing, sprite, tintIndex));
            faceQuads.add(quads);
        }
        final IBakedModel bakedModel = new SimpleBakedModel(generalQuads, faceQuads, true, true, sprite, ItemCameraTransforms.DEFAULT);
        return bakedModel;
    }
    
    public static IBakedModel joinModelsCube(final IBakedModel modelBase, final IBakedModel modelAdd) {
        final List<BakedQuad> generalQuads = new ArrayList<BakedQuad>();
        generalQuads.addAll(modelBase.getGeneralQuads());
        generalQuads.addAll(modelAdd.getGeneralQuads());
        final EnumFacing[] facings = EnumFacing.VALUES;
        final List faceQuads = new ArrayList();
        for (int i = 0; i < facings.length; ++i) {
            final EnumFacing facing = facings[i];
            final List quads = new ArrayList();
            quads.addAll(modelBase.getFaceQuads(facing));
            quads.addAll(modelAdd.getFaceQuads(facing));
            faceQuads.add(quads);
        }
        final boolean ao = modelBase.isAmbientOcclusion();
        final boolean builtIn = modelBase.isBuiltInRenderer();
        final TextureAtlasSprite sprite = modelBase.getParticleTexture();
        final ItemCameraTransforms transforms = modelBase.getItemCameraTransforms();
        final IBakedModel bakedModel = new SimpleBakedModel(generalQuads, faceQuads, ao, builtIn, sprite, transforms);
        return bakedModel;
    }
    
    public static BakedQuad makeBakedQuad(final EnumFacing facing, final TextureAtlasSprite sprite, final int tintIndex) {
        final Vector3f posFrom = new Vector3f(0.0f, 0.0f, 0.0f);
        final Vector3f posTo = new Vector3f(16.0f, 16.0f, 16.0f);
        final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace face = new BlockPartFace(facing, tintIndex, "#" + facing.getName(), uv);
        final ModelRotation modelRotation = ModelRotation.X0_Y0;
        final BlockPartRotation partRotation = null;
        final boolean uvLocked = false;
        final boolean shade = true;
        final FaceBakery faceBakery = new FaceBakery();
        final BakedQuad quad = faceBakery.makeBakedQuad(posFrom, posTo, face, sprite, facing, modelRotation, partRotation, uvLocked, shade);
        return quad;
    }
    
    public static IBakedModel makeModel(final String modelName, final String spriteOldName, final String spriteNewName) {
        final TextureMap textureMap = Config.getMinecraft().getTextureMapBlocks();
        final TextureAtlasSprite spriteOld = textureMap.getSpriteSafe(spriteOldName);
        final TextureAtlasSprite spriteNew = textureMap.getSpriteSafe(spriteNewName);
        return makeModel(modelName, spriteOld, spriteNew);
    }
    
    public static IBakedModel makeModel(final String modelName, final TextureAtlasSprite spriteOld, final TextureAtlasSprite spriteNew) {
        if (spriteOld == null || spriteNew == null) {
            return null;
        }
        final ModelManager modelManager = Config.getModelManager();
        if (modelManager == null) {
            return null;
        }
        final ModelResourceLocation mrl = new ModelResourceLocation(modelName, "normal");
        final IBakedModel model = modelManager.getModel(mrl);
        if (model == null || model == modelManager.getMissingModel()) {
            return null;
        }
        final IBakedModel modelNew = ModelUtils.duplicateModel(model);
        final EnumFacing[] faces = EnumFacing.VALUES;
        for (int i = 0; i < faces.length; ++i) {
            final EnumFacing face = faces[i];
            final List<BakedQuad> quads = modelNew.getFaceQuads(face);
            replaceTexture(quads, spriteOld, spriteNew);
        }
        final List<BakedQuad> quadsGeneral = modelNew.getGeneralQuads();
        replaceTexture(quadsGeneral, spriteOld, spriteNew);
        return modelNew;
    }
    
    private static void replaceTexture(final List<BakedQuad> quads, final TextureAtlasSprite spriteOld, final TextureAtlasSprite spriteNew) {
        final List<BakedQuad> quadsNew = new ArrayList<BakedQuad>();
        for (BakedQuad quad : quads) {
            if (quad.getSprite() == spriteOld) {
                quad = new BreakingFour(quad, spriteNew);
            }
            quadsNew.add(quad);
        }
        quads.clear();
        quads.addAll(quadsNew);
    }
    
    public static void snapVertexPosition(final Vector3f pos) {
        pos.setX(snapVertexCoord(pos.getX()));
        pos.setY(snapVertexCoord(pos.getY()));
        pos.setZ(snapVertexCoord(pos.getZ()));
    }
    
    private static float snapVertexCoord(final float x) {
        if (x > -1.0E-6f && x < 1.0E-6f) {
            return 0.0f;
        }
        if (x > 0.999999f && x < 1.000001f) {
            return 1.0f;
        }
        return x;
    }
    
    public static AxisAlignedBB getOffsetBoundingBox(final AxisAlignedBB aabb, final Block.EnumOffsetType offsetType, final BlockPos pos) {
        final int x = pos.getX();
        final int z = pos.getZ();
        long k = (long)(x * 3129871) ^ z * 116129781L;
        k = k * k * 42317861L + k * 11L;
        final double dx = ((k >> 16 & 0xFL) / 15.0f - 0.5) * 0.5;
        final double dz = ((k >> 24 & 0xFL) / 15.0f - 0.5) * 0.5;
        double dy = 0.0;
        if (offsetType == Block.EnumOffsetType.XYZ) {
            dy = ((k >> 20 & 0xFL) / 15.0f - 1.0) * 0.2;
        }
        return aabb.offset(dx, dy, dz);
    }
}
