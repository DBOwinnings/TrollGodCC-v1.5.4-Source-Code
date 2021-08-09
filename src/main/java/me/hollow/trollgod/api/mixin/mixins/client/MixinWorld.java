/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.client;

import me.hollow.trollgod.client.modules.visual.TimeChanger;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={World.class})
public class MixinWorld {
    @Inject(method={"getWorldTime"}, at={@At(value="HEAD")}, cancellable=true)
    public void getWorldTime(CallbackInfoReturnable<Long> cir) {
        if (TimeChanger.INSTANCE.isEnabled()) {
            cir.cancel();
            cir.setReturnValue((long)TimeChanger.INSTANCE.timeSetting.getValue());
        }
    }
}

