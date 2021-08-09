/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="Velocity", category=Module.Category.MISC, color=-16764673)
public class PacketCanceller
extends Module {
    @Override
    public void onEnable() {
        this.setSuffix("H0.0%:V0.0%");
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).getEntityID() == this.mc.player.getEntityId() || event.getPacket() instanceof SPacketExplosion) {
            event.setCancelled();
        }
    }
}

