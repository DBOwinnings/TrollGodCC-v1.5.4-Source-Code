package me.hollow.trollgod.client.events;

import tcb.bces.event.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;

public class DamageBlockEvent extends EventCancellable
{
    private final BlockPos blockPos;
    private final EnumFacing enumFacing;
    
    public DamageBlockEvent(final BlockPos blockPos, final EnumFacing enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }
    
    public BlockPos getPos() {
        return this.blockPos;
    }
    
    public EnumFacing getFacing() {
        return this.enumFacing;
    }
}
