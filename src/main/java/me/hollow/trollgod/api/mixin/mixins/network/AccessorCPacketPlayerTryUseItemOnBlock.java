/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.network;

import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CPacketPlayerTryUseItemOnBlock.class})
public interface AccessorCPacketPlayerTryUseItemOnBlock {
    @Accessor(value="placedBlockDirection")
    public void setPlacedBlockDirection(EnumFacing var1);
}

