package me.hollow.trollgod.api.mixin.mixins.client;

import org.spongepowered.asm.mixin.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.hollow.trollgod.client.modules.visual.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ World.class })
public class MixinWorld
{
    @Inject(method = { "getWorldTime" }, at = { @At("HEAD") }, cancellable = true)
    public void getWorldTime(final CallbackInfoReturnable<Long> cir) {
        if (TimeChanger.INSTANCE.isEnabled()) {
            cir.cancel();
            cir.setReturnValue((long)TimeChanger.INSTANCE.timeSetting.getValue());
        }
    }
}
