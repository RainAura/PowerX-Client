package net.minecraft.client.renderer;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.world.WorldType;
import net.minecraft.util.Vec3i;
import net.minecraft.util.MathHelper;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.CrashReport;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.block.Block;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.world.IBlockAccess;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class BlockRendererDispatcher implements IResourceManagerReloadListener
{
    private BlockModelShapes blockModelShapes;
    private final GameSettings gameSettings;
    private final BlockModelRenderer blockModelRenderer;
    private final ChestRenderer chestRenderer;
    private final BlockFluidRenderer fluidRenderer;
    
    public BlockRendererDispatcher(final BlockModelShapes blockModelShapesIn, final GameSettings gameSettingsIn) {
        this.blockModelRenderer = new BlockModelRenderer();
        this.chestRenderer = new ChestRenderer();
        this.fluidRenderer = new BlockFluidRenderer();
        this.blockModelShapes = blockModelShapesIn;
        this.gameSettings = gameSettingsIn;
    }
    
    public BlockModelShapes getBlockModelShapes() {
        return this.blockModelShapes;
    }
    
    public void renderBlockDamage(IBlockState state, final BlockPos pos, final TextureAtlasSprite texture, final IBlockAccess blockAccess) {
        final Block lvt_5_1_ = state.getBlock();
        final int lvt_6_1_ = lvt_5_1_.getRenderType();
        if (lvt_6_1_ != 3) {
            return;
        }
        state = lvt_5_1_.getActualState(state, blockAccess, pos);
        final IBakedModel lvt_7_1_ = this.blockModelShapes.getModelForState(state);
        final IBakedModel lvt_8_1_ = new SimpleBakedModel.Builder(lvt_7_1_, texture).makeBakedModel();
        this.blockModelRenderer.renderModel(blockAccess, lvt_8_1_, state, pos, Tessellator.getInstance().getWorldRenderer());
    }
    
    public boolean renderBlock(final IBlockState state, final BlockPos pos, final IBlockAccess blockAccess, final WorldRenderer worldRendererIn) {
        try {
            final int lvt_5_1_ = state.getBlock().getRenderType();
            if (lvt_5_1_ == -1) {
                return false;
            }
            switch (lvt_5_1_) {
                case 3: {
                    final IBakedModel lvt_6_1_ = this.getModelFromBlockState(state, blockAccess, pos);
                    return this.blockModelRenderer.renderModel(blockAccess, lvt_6_1_, state, pos, worldRendererIn);
                }
                case 2: {
                    return false;
                }
                case 1: {
                    return this.fluidRenderer.renderFluid(blockAccess, state, pos, worldRendererIn);
                }
            }
        }
        catch (Throwable lvt_5_2_) {
            final CrashReport lvt_6_2_ = CrashReport.makeCrashReport(lvt_5_2_, "Tesselating block in world");
            final CrashReportCategory lvt_7_1_ = lvt_6_2_.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(lvt_7_1_, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(lvt_6_2_);
        }
        return false;
    }
    
    public BlockModelRenderer getBlockModelRenderer() {
        return this.blockModelRenderer;
    }
    
    private IBakedModel getBakedModel(final IBlockState state, final BlockPos pos) {
        IBakedModel lvt_3_1_ = this.blockModelShapes.getModelForState(state);
        if (pos != null && this.gameSettings.allowBlockAlternatives && lvt_3_1_ instanceof WeightedBakedModel) {
            lvt_3_1_ = ((WeightedBakedModel)lvt_3_1_).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }
        return lvt_3_1_;
    }
    
    public IBakedModel getModelFromBlockState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        final Block lvt_4_1_ = state.getBlock();
        if (worldIn.getWorldType() != WorldType.DEBUG_WORLD) {
            try {
                state = lvt_4_1_.getActualState(state, worldIn, pos);
            }
            catch (Exception ex) {}
        }
        IBakedModel lvt_5_1_ = this.blockModelShapes.getModelForState(state);
        if (pos != null && this.gameSettings.allowBlockAlternatives && lvt_5_1_ instanceof WeightedBakedModel) {
            lvt_5_1_ = ((WeightedBakedModel)lvt_5_1_).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }
        return lvt_5_1_;
    }
    
    public void renderBlockBrightness(final IBlockState state, final float brightness) {
        final int lvt_3_1_ = state.getBlock().getRenderType();
        if (lvt_3_1_ == -1) {
            return;
        }
        switch (lvt_3_1_) {
            case 3: {
                final IBakedModel lvt_4_1_ = this.getBakedModel(state, null);
                this.blockModelRenderer.renderModelBrightness(lvt_4_1_, state, brightness, true);
                break;
            }
            case 2: {
                this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
                break;
            }
        }
    }
    
    public boolean isRenderTypeChest(final Block p_175021_1_, final int p_175021_2_) {
        if (p_175021_1_ == null) {
            return false;
        }
        final int lvt_3_1_ = p_175021_1_.getRenderType();
        return lvt_3_1_ != 3 && lvt_3_1_ == 2;
    }
    
    @Override
    public void onResourceManagerReload(final IResourceManager resourceManager) {
        this.fluidRenderer.initAtlasSprites();
    }
}
