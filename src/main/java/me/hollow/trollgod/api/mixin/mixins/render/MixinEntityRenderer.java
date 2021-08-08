package me.hollow.trollgod.api.mixin.mixins.render;

import net.minecraft.client.renderer.*;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.hollow.trollgod.*;
import org.spongepowered.asm.mixin.injection.*;
import me.hollow.trollgod.client.modules.*;
import net.minecraft.client.gui.*;
import me.hollow.trollgod.client.modules.visual.*;

@Mixin({ EntityRenderer.class })
public abstract class MixinEntityRenderer
{
    @Final
    @Shadow
    private Minecraft mc;
    
    @Inject(method = { "updateCameraAndRender" }, at = { @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiIngame.renderGameOverlay(F)V") })
    public void onRender2D(final float partialTicks, final long nanoTime, final CallbackInfo ci) {
        if (this.mc.player != null || this.mc.world != null) {
            HUD.INSTANCE.onRender2D();
            TrollGod.INSTANCE.getNotificationManager().handleNotifications(new ScaledResolution(this.mc).getScaledHeight() - 60);
        }
    }
    
    @Inject(method = { "renderWorldPass" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V") })
    public void onRender3D(final int pass, final float partialTicks, final long finishTimeNano, final CallbackInfo ci) {
        if (this.mc.player != null || this.mc.world != null) {
            for (int i = 0; i < TrollGod.INSTANCE.getModuleManager().getSize(); ++i) {
                final Module mod = TrollGod.INSTANCE.getModuleManager().getModules().get(i);
                if (mod.isEnabled()) {
                    mod.onRender3D();
                }
            }
        }
    }
    
    @Inject(method = { "drawNameplate" }, at = { @At("HEAD") }, cancellable = true)
    private static void renderName(final FontRenderer fontRendererIn, final String str, final float x, final float y, final float z, final int verticalShift, final float viewerYaw, final float viewerPitch, final boolean isThirdPersonFrontal, final boolean isSneaking, final CallbackInfo ci) {
        if (Nametags.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }
}
