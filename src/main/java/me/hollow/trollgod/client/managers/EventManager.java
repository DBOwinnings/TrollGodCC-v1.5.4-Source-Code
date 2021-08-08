package me.hollow.trollgod.client.managers;

import net.minecraftforge.common.*;
import me.hollow.trollgod.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraft.client.*;
import me.hollow.trollgod.client.modules.*;
import net.minecraftforge.fml.common.gameevent.*;
import me.hollow.trollgod.client.modules.combat.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.client.*;
import tcb.bces.listener.*;

public class EventManager implements IListener
{
    private int ticksPassed;
    private int size;
    
    public EventManager() {
        this.size = -1;
    }
    
    public void init() {
        MinecraftForge.EVENT_BUS.register((Object)this);
        TrollGod.INSTANCE.getBus().register(this);
        this.size = TrollGod.INSTANCE.getModuleManager().getModules().size();
    }
    
    @SubscribeEvent
    public void onUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() == Minecraft.getMinecraft().player) {
            for (int i = 0; i < this.size; ++i) {
                final Module module = TrollGod.INSTANCE.getModuleManager().getModules().get(i);
                if (module.isEnabled()) {
                    module.onUpdate();
                }
            }
            TrollGod.INSTANCE.getSafeManager().update();
            TrollGod.INSTANCE.getPopManager().update();
            TrollGod.INSTANCE.getSpeedManager().update();
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTickHighest(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ++this.ticksPassed;
        }
        if (AutoCrystal.INSTANCE.isEnabled()) {
            AutoCrystal.INSTANCE.onTick();
        }
    }
    
    @Subscribe
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            this.ticksPassed = 0;
        }
    }
    
    public int ticksPassedSinceSwitch() {
        return this.ticksPassed;
    }
    
    @Override
    public boolean isListening() {
        return true;
    }
}
