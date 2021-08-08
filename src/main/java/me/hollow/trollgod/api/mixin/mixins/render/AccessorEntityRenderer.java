package me.hollow.trollgod.api.mixin.mixins.render;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ EntityRenderer.class })
public interface AccessorEntityRenderer
{
    @Accessor("drawBlockOutline")
    void setDrawBlockOutline(final boolean p0);
    
    @Invoker("orientCamera")
    void invokeOrientCamera(final float p0);
}
