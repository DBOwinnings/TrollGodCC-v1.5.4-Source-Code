package me.hollow.trollgod.client.managers;

import me.hollow.trollgod.api.interfaces.*;
import java.util.*;
import net.minecraft.entity.player.*;
import me.hollow.trollgod.client.modules.client.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.api.util.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.server.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import tcb.bces.listener.*;

public class PopManager implements Minecraftable, IListener
{
    private final Map<String, Integer> popMap;
    
    public PopManager() {
        this.popMap = new HashMap<String, Integer>();
    }
    
    public void update() {
        for (int size = PopManager.mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = PopManager.mc.world.playerEntities.get(i);
            if (player.getHealth() <= 0.0f && this.popMap.containsKey(player.getName())) {
                if (PopCounter.getInstance().isEnabled()) {
                    if (PopCounter.notify.getValue()) {
                        TrollGod.INSTANCE.getNotificationManager().addNotification(player.getName() + " died after popping their " + this.popMap.get(player.getName()) + this.getNumberStringThing(this.popMap.get(player.getName())) + " totem.");
                    }
                    MessageUtil.sendClientMessage(player.getName() + " died after popping their " + this.popMap.get(player.getName()) + this.getNumberStringThing(this.popMap.get(player.getName())) + " totem.", player.getEntityId());
                }
                this.popMap.remove(player.getName(), this.popMap.get(player.getName()));
            }
        }
    }
    
    public String getNumberStringThing(final int number) {
        if (number > 3) {
            return "th";
        }
        switch (number) {
            case 2: {
                return "nd";
            }
            case 3: {
                return "rd";
            }
            default: {
                return "";
            }
        }
    }
    
    @Subscribe
    public void onPacket(final PacketEvent.Receive event) {
        if (PopManager.mc.player == null || PopManager.mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            if (packet.getOpCode() == 35) {
                final Entity entity = packet.getEntity((World)PopManager.mc.world);
                if (this.popMap.get(entity.getName()) == null) {
                    this.popMap.put(entity.getName(), 1);
                    if (PopCounter.getInstance().isEnabled()) {
                        if (PopCounter.notify.getValue()) {
                            TrollGod.INSTANCE.getNotificationManager().addNotification(entity.getName() + " popped a totem.");
                        }
                        MessageUtil.sendClientMessage(entity.getName() + " popped a totem.", entity.getEntityId());
                    }
                }
                else if (this.popMap.get(entity.getName()) != null) {
                    final int popCounter = this.popMap.get(entity.getName());
                    final int newPopCounter = popCounter + 1;
                    this.popMap.put(entity.getName(), newPopCounter);
                    if (PopCounter.getInstance().isEnabled()) {
                        if (PopCounter.notify.getValue()) {
                            TrollGod.INSTANCE.getNotificationManager().addNotification(entity.getName() + " popped their " + newPopCounter + this.getNumberStringThing(newPopCounter) + " totem.");
                        }
                        MessageUtil.sendClientMessage(entity.getName() + " popped their " + newPopCounter + this.getNumberStringThing(newPopCounter) + " totem.", entity.getEntityId());
                    }
                }
            }
        }
    }
    
    public final Map<String, Integer> getPopMap() {
        return this.popMap;
    }
    
    public void init() {
        TrollGod.INSTANCE.getBus().register(this);
    }
    
    @Override
    public boolean isListening() {
        return true;
    }
}
