package me.hollow.trollgod.api.mixin.mixins.gui;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.hollow.trollgod.client.modules.visual.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ GuiBossOverlay.class })
public class MixinGuiBossOverlay
{
    @Inject(method = { "renderBossHealth" }, at = { @At("HEAD") }, cancellable = true)
    public void render(final CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.noBossOverlay.getValue()) {
            ci.cancel();
        }
    }
}
