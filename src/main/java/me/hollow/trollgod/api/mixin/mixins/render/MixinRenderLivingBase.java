/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.render;

import javax.annotation.Nullable;
import me.hollow.trollgod.api.interfaces.Minecraftable;
import me.hollow.trollgod.client.modules.visual.Skeleton;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderLivingBase.class})
public class MixinRenderLivingBase<T extends EntityLivingBase>
extends Render
implements Minecraftable {
    @Shadow
    protected ModelBase mainModel;

    @Inject(method={"renderLayers"}, at={@At(value="RETURN")})
    public void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {
        if (Skeleton.INSTANCE.isEnabled()) {
            Skeleton.INSTANCE.onRenderModel(this.mainModel, (Entity)entitylivingbaseIn);
        }
    }

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}

