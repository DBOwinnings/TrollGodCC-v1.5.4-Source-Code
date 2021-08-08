package me.hollow.trollgod.api.mixin.mixins.network;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.play.client.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ CPacketPlayer.class })
public interface AccessorCPacketPlayer
{
    @Accessor("yaw")
    void setYaw(final float p0);
    
    @Accessor("pitch")
    void setPitch(final float p0);
    
    @Accessor("yaw")
    float getYaw();
    
    @Accessor("pitch")
    float getPitch();
}
