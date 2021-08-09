/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.network;

import me.hollow.trollgod.client.modules.misc.NoFall;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={CPacketPlayer.class})
public class MixinCPacketPlayer {
    @Shadow
    protected boolean onGround;

    @Overwrite
    public void writePacketData(PacketBuffer buf) {
        if (NoFall.INSTANCE.isEnabled() && Minecraft.getMinecraft().player.fallDistance > 2.0f) {
            buf.writeByte(1);
            return;
        }
        buf.writeByte(this.onGround ? 1 : 0);
    }
}

