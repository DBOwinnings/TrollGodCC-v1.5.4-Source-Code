package me.hollow.trollgod.api.mixin.mixins.network;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.play.client.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ CPacketUseEntity.class })
public interface AccessorCPacketUseEntity
{
    @Accessor("entityId")
    void setEntityId(final int p0);
    
    @Accessor("action")
    void setAction(final CPacketUseEntity.Action p0);
}
