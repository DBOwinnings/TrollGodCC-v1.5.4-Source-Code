package me.hollow.trollgod.client.events;

import tcb.bces.event.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;

public class ClickBlockEvent extends EventCancellable
{
    final BlockPos pos;
    final EnumFacing facing;
    final int stage;
    
    public ClickBlockEvent(final int stage, final BlockPos pos, final EnumFacing facing) {
        this.stage = stage;
        this.pos = pos;
        this.facing = facing;
    }
    
    public final BlockPos getPos() {
        return this.pos;
    }
    
    public final EnumFacing getFacing() {
        return this.facing;
    }
    
    public final int getStage() {
        return this.stage;
    }
}
