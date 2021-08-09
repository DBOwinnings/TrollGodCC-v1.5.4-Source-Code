/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.render;

import me.hollow.trollgod.api.interfaces.Minecraftable;
import me.hollow.trollgod.client.modules.visual.EnchantColor;
import me.hollow.trollgod.client.modules.visual.ViewmodelChanger;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderItem.class})
public class MixinRenderItem
implements Minecraftable {
    @ModifyArg(method={"renderEffect"}, at=@At(value="INVOKE", target="net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"))
    private int renderEffect(int glintVal) {
        return EnchantColor.INSTANCE.isEnabled() ? EnchantColor.INSTANCE.getColor() : glintVal;
    }

    @Inject(method={"renderItemModel"}, at={@At(value="INVOKE")})
    public void renderItem(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        if (ViewmodelChanger.INSTANCE.isEnabled() && (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)) {
            float scale = ViewmodelChanger.INSTANCE.scale.getValue().floatValue();
            GL11.glScalef((float)(scale / 10.0f), (float)(scale / 10.0f), (float)(scale / 10.0f));
            float translateX = ViewmodelChanger.INSTANCE.translateX.getValue().floatValue();
            float translateY = ViewmodelChanger.INSTANCE.translateY.getValue().floatValue();
            float translateZ = ViewmodelChanger.INSTANCE.translateZ.getValue().floatValue();
            float rotateX = ViewmodelChanger.INSTANCE.rotateX.getValue().floatValue();
            float rotateY = ViewmodelChanger.INSTANCE.rotateY.getValue().floatValue();
            float rotateZ = ViewmodelChanger.INSTANCE.rotateZ.getValue().floatValue();
            if (transform.equals((Object)ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND)) {
                if (MixinRenderItem.mc.player.getActiveHand() == EnumHand.OFF_HAND && MixinRenderItem.mc.player.isHandActive() && ViewmodelChanger.INSTANCE.pauseOnEat.getValue().booleanValue()) {
                    return;
                }
                GL11.glTranslated((double)(translateX / 15.0f), (double)(translateY / 15.0f), (double)(translateZ / 15.0f));
                GL11.glRotatef((float)rotateX, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)rotateY, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)rotateZ, (float)0.0f, (float)0.0f, (float)1.0f);
            } else {
                if (MixinRenderItem.mc.player.getActiveHand() == EnumHand.MAIN_HAND && MixinRenderItem.mc.player.isHandActive() && ViewmodelChanger.INSTANCE.pauseOnEat.getValue().booleanValue()) {
                    return;
                }
                GL11.glTranslated((double)(-translateX / 15.0f), (double)(translateY / 15.0f), (double)(translateZ / 15.0f));
                GL11.glRotatef((float)rotateX, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)(rotateY * -1.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(rotateZ * -1.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
        }
    }
}

