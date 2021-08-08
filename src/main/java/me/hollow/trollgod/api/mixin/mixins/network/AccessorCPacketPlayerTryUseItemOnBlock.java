package me.hollow.trollgod.api.mixin.mixins.network;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ CPacketPlayerTryUseItemOnBlock.class })
public interface AccessorCPacketPlayerTryUseItemOnBlock
{
    @Accessor("placedBlockDirection")
    void setPlacedBlockDirection(final EnumFacing p0);
}
