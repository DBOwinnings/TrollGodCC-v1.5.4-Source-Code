package me.hollow.trollgod.client.events;

import tcb.bces.event.*;
import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;

public class ClientEvent extends Event
{
    private Module module;
    private Setting setting;
    private int stage;
    
    public ClientEvent(final Setting setting) {
        this.setting = setting;
    }
    
    public ClientEvent(final int stage) {
        this.stage = stage;
    }
    
    public Module getModule() {
        return this.module;
    }
    
    public Setting getSetting() {
        return this.setting;
    }
    
    public int getStage() {
        return this.stage;
    }
}
