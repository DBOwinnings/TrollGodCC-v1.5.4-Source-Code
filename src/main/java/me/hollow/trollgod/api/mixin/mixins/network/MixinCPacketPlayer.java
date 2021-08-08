package me.hollow.trollgod.api.mixin.mixins.network;

import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import me.hollow.trollgod.client.modules.misc.*;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.*;

@Mixin({ CPacketPlayer.class })
public class MixinCPacketPlayer
{
    @Shadow
    protected boolean onGround;
    
    @Overwrite
    public void writePacketData(final PacketBuffer buf) {
        if (NoFall.INSTANCE.isEnabled() && Minecraft.getMinecraft().player.fallDistance > 2.0f) {
            buf.writeByte(1);
            return;
        }
        buf.writeByte((int)(this.onGround ? 1 : 0));
    }
}
