/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.gui;

import me.hollow.trollgod.client.modules.visual.NoRender;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiBossOverlay.class})
public class MixinGuiBossOverlay {
    @Inject(method={"renderBossHealth"}, at={@At(value="HEAD")}, cancellable=true)
    public void render(CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.noBossOverlay.getValue().booleanValue()) {
            ci.cancel();
        }
    }
}

