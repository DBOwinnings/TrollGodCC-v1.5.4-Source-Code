package me.hollow.trollgod.api.mixin.mixins.network;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.play.server.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ SPacketPlayerPosLook.class })
public interface AccessorSPacketPlayerPosLook
{
    @Accessor("yaw")
    void setYaw(final float p0);
    
    @Accessor("pitch")
    void setPitch(final float p0);
}
