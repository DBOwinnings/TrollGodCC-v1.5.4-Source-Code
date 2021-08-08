package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.server.*;
import me.hollow.trollgod.api.mixin.mixins.network.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "NoRotate", category = Category.MISC, color = -11870034)
public class NoRotate extends Module
{
    @Subscribe
    public void onPacket(final PacketEvent.Receive event) {
        if (this.isNull()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            final AccessorSPacketPlayerPosLook packet = (AccessorSPacketPlayerPosLook)event.getPacket();
            packet.setPitch(this.mc.player.rotationPitch);
            packet.setYaw(this.mc.player.rotationYaw);
        }
    }
}
