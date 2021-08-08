package me.hollow.trollgod.api.mixin.mixins.render;

import me.hollow.trollgod.api.interfaces.*;
import net.minecraft.client.model.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.hollow.trollgod.client.modules.visual.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;
import javax.annotation.*;

@Mixin({ RenderLivingBase.class })
public class MixinRenderLivingBase<T extends EntityLivingBase> extends Render implements Minecraftable
{
    @Shadow
    protected ModelBase mainModel;
    
    @Inject(method = { "renderLayers" }, at = { @At("RETURN") })
    public void renderLayers(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleIn, final CallbackInfo ci) {
        if (Skeleton.INSTANCE.isEnabled()) {
            Skeleton.INSTANCE.onRenderModel(this.mainModel, (Entity)entitylivingbaseIn);
        }
    }
    
    protected MixinRenderLivingBase(final RenderManager renderManager) {
        super(renderManager);
    }
    
    @Nullable
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return null;
    }
}
