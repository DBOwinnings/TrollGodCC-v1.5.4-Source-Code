package me.hollow.trollgod.api.mixin.mixins.client;

import me.hollow.trollgod.api.mixin.accessors.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.multiplayer.*;
import org.spongepowered.asm.mixin.gen.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.hollow.trollgod.client.events.*;
import me.hollow.trollgod.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ PlayerControllerMP.class })
public abstract class MixinPlayerControllerMP implements IPlayerControllerMP
{
    @Accessor("isHittingBlock")
    @Override
    public abstract void setIsHittingBlock(final boolean p0);
    
    @Accessor("blockHitDelay")
    @Override
    public abstract void setBlockHitDelay(final int p0);
    
    @Accessor("curBlockDamageMP")
    @Override
    public abstract float getCurBlockDamageMP();
    
    @Inject(method = { "clickBlock" }, at = { @At("HEAD") }, cancellable = true)
    public void clickBlock(final BlockPos loc, final EnumFacing face, final CallbackInfoReturnable<Boolean> cir) {
        final ClickBlockEvent event = new ClickBlockEvent(0, loc, face);
        TrollGod.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            cir.cancel();
        }
    }
    
    @Inject(method = { "onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z" }, at = { @At("HEAD") }, cancellable = true)
    public void onPlayerDamageBlock(final BlockPos posBlock, final EnumFacing directionFacing, final CallbackInfoReturnable<Boolean> cir) {
        final ClickBlockEvent event = new ClickBlockEvent(1, posBlock, directionFacing);
        TrollGod.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            cir.cancel();
        }
    }
}
