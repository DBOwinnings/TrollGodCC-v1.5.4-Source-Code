package me.hollow.trollgod.api.mixin.mixins.network;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ SPacketChat.class })
public interface AccessorSPacketChat
{
    @Accessor("chatComponent")
    void setChatComponent(final ITextComponent p0);
}
