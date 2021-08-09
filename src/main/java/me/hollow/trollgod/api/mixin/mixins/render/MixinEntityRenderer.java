/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.render;

import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.visual.HUD;
import me.hollow.trollgod.client.modules.visual.Nametags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public abstract class MixinEntityRenderer {
    @Final
    @Shadow
    private Minecraft mc;

    @Inject(method={"updateCameraAndRender"}, at={@At(value="INVOKE", target="net/minecraft/client/gui/GuiIngame.renderGameOverlay(F)V")})
    public void onRender2D(float partialTicks, long nanoTime, CallbackInfo ci) {
        if (this.mc.player != null || this.mc.world != null) {
            HUD.INSTANCE.onRender2D();
            TrollGod.INSTANCE.getNotificationManager().handleNotifications(new ScaledResolution(this.mc).getScaledHeight() - 60);
        }
    }

    @Inject(method={"renderWorldPass"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V")})
    public void onRender3D(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (this.mc.player != null || this.mc.world != null) {
            for (int i = 0; i < TrollGod.INSTANCE.getModuleManager().getSize(); ++i) {
                Module mod = TrollGod.INSTANCE.getModuleManager().getModules().get(i);
                if (!mod.isEnabled()) continue;
                mod.onRender3D();
            }
        }
    }

    @Inject(method={"drawNameplate"}, at={@At(value="HEAD")}, cancellable=true)
    private static void renderName(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, CallbackInfo ci) {
        if (Nametags.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }
}

