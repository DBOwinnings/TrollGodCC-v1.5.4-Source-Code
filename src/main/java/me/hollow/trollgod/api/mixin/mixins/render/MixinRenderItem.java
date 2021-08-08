package me.hollow.trollgod.api.mixin.mixins.render;

import me.hollow.trollgod.api.interfaces.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import net.minecraft.client.renderer.block.model.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.hollow.trollgod.client.modules.visual.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ RenderItem.class })
public class MixinRenderItem implements Minecraftable
{
    @ModifyArg(method = { "renderEffect" }, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"))
    private int renderEffect(final int glintVal) {
        return EnchantColor.INSTANCE.isEnabled() ? EnchantColor.INSTANCE.getColor() : glintVal;
    }
    
    @Inject(method = { "renderItemModel" }, at = { @At("INVOKE") })
    public void renderItem(final ItemStack stack, final IBakedModel bakedmodel, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        if (ViewmodelChanger.INSTANCE.isEnabled() && (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)) {
            final float scale = ViewmodelChanger.INSTANCE.scale.getValue();
            GL11.glScalef(scale / 10.0f, scale / 10.0f, scale / 10.0f);
            final float translateX = ViewmodelChanger.INSTANCE.translateX.getValue();
            final float translateY = ViewmodelChanger.INSTANCE.translateY.getValue();
            final float translateZ = ViewmodelChanger.INSTANCE.translateZ.getValue();
            final float rotateX = ViewmodelChanger.INSTANCE.rotateX.getValue();
            final float rotateY = ViewmodelChanger.INSTANCE.rotateY.getValue();
            final float rotateZ = ViewmodelChanger.INSTANCE.rotateZ.getValue();
            if (transform.equals((Object)ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND)) {
                if (MixinRenderItem.mc.player.getActiveHand() == EnumHand.OFF_HAND && MixinRenderItem.mc.player.isHandActive() && ViewmodelChanger.INSTANCE.pauseOnEat.getValue()) {
                    return;
                }
                GL11.glTranslated((double)(translateX / 15.0f), (double)(translateY / 15.0f), (double)(translateZ / 15.0f));
                GL11.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(rotateZ, 0.0f, 0.0f, 1.0f);
            }
            else {
                if (MixinRenderItem.mc.player.getActiveHand() == EnumHand.MAIN_HAND && MixinRenderItem.mc.player.isHandActive() && ViewmodelChanger.INSTANCE.pauseOnEat.getValue()) {
                    return;
                }
                GL11.glTranslated((double)(-translateX / 15.0f), (double)(translateY / 15.0f), (double)(translateZ / 15.0f));
                GL11.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(rotateY * -1.0f, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(rotateZ * -1.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }
}
