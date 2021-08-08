package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "Criticals", category = Category.COMBAT, color = -12582800)
public class Criticals extends Module
{
    private final Setting<Boolean> smallPacket;
    private final Setting<Integer> packets;
    private final double[] packetArray;
    
    public Criticals() {
        this.smallPacket = (Setting<Boolean>)this.register(new Setting("small packet", (T)false));
        this.packets = (Setting<Integer>)this.register(new Setting("Packets", (T)2, (T)1, (T)5, v -> !this.smallPacket.getValue()));
        this.packetArray = new double[] { 0.11, 0.11, 0.1100013579, 0.1100013579, 0.1100013579, 0.1100013579 };
    }
    
    @Subscribe
    public void onPacket(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            if (!this.mc.player.onGround) {
                return;
            }
            final CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)this.mc.world) instanceof EntityLivingBase) {
                if (this.smallPacket.getValue()) {
                    this.mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.01, this.mc.player.posZ, false));
                }
                else {
                    for (int i = 0; i < this.packets.getValue(); ++i) {
                        this.mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + this.packetArray[i], this.mc.player.posZ, false));
                    }
                }
                this.mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ, false));
            }
        }
    }
}
