package me.hollow.trollgod.api.mixin.mixins.network;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.play.client.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ CPacketChatMessage.class })
public interface AccessorCPacketChatMessage
{
    @Accessor("message")
    void setMessage(final String p0);
    
    @Accessor("message")
    String getMessage();
}
