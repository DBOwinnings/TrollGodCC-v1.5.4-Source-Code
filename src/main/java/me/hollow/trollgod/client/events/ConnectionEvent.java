package me.hollow.trollgod.client.events;

import tcb.bces.event.*;
import java.util.*;
import net.minecraft.entity.player.*;

public class ConnectionEvent extends Event
{
    private final int stage;
    private final UUID uuid;
    private final EntityPlayer entity;
    private final String name;
    
    public ConnectionEvent(final int stage, final UUID uuid, final String name) {
        this.stage = stage;
        this.uuid = uuid;
        this.name = name;
        this.entity = null;
    }
    
    public ConnectionEvent(final int stage, final EntityPlayer entity, final UUID uuid, final String name) {
        this.stage = stage;
        this.entity = entity;
        this.uuid = uuid;
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getStage() {
        return this.stage;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public EntityPlayer getEntity() {
        return this.entity;
    }
}
