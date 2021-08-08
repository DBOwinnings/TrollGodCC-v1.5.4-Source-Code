package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.server.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "Velocity", category = Category.MISC, color = -16764673)
public class PacketCanceller extends Module
{
    @Override
    public void onEnable() {
        this.setSuffix("H0.0%:V0.0%");
    }
    
    @Subscribe
    public void onPacketReceive(final PacketEvent.Receive event) {
        if ((event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).getEntityID() == this.mc.player.getEntityId()) || event.getPacket() instanceof SPacketExplosion) {
            event.setCancelled();
        }
    }
}
