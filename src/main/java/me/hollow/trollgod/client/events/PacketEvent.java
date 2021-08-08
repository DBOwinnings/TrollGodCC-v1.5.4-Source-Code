package me.hollow.trollgod.client.events;

import tcb.bces.event.*;
import net.minecraft.network.*;

public class PacketEvent extends EventCancellable
{
    private final Packet packet;
    
    public PacketEvent(final Packet packet) {
        this.packet = packet;
    }
    
    public final Packet getPacket() {
        return this.packet;
    }
    
    public static final class Receive extends PacketEvent
    {
        public Receive(final Packet packet) {
            super(packet);
        }
    }
    
    public static final class Send extends PacketEvent
    {
        public Send(final Packet packet) {
            super(packet);
        }
    }
}
