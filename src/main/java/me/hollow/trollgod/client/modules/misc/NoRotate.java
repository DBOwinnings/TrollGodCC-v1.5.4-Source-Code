/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.api.mixin.mixins.network.AccessorSPacketPlayerPosLook;
import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="NoRotate", category=Module.Category.MISC, color=-11870034)
public class NoRotate
extends Module {
    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (this.isNull()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            AccessorSPacketPlayerPosLook packet = (AccessorSPacketPlayerPosLook)event.getPacket();
            packet.setPitch(this.mc.player.rotationPitch);
            packet.setYaw(this.mc.player.rotationYaw);
        }
    }
}

