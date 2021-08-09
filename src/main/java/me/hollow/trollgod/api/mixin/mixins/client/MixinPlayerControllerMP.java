/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.client;

import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.mixin.accessors.IPlayerControllerMP;
import me.hollow.trollgod.client.events.ClickBlockEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerControllerMP.class})
public abstract class MixinPlayerControllerMP
implements IPlayerControllerMP {
    @Override
    @Accessor(value="isHittingBlock")
    public abstract void setIsHittingBlock(boolean var1);

    @Override
    @Accessor(value="blockHitDelay")
    public abstract void setBlockHitDelay(int var1);

    @Override
    @Accessor(value="curBlockDamageMP")
    public abstract float getCurBlockDamageMP();

    @Inject(method={"clickBlock"}, at={@At(value="HEAD")}, cancellable=true)
    public void clickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
        ClickBlockEvent event = new ClickBlockEvent(0, loc, face);
        TrollGod.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            cir.cancel();
        }
    }

    @Inject(method={"onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"}, at={@At(value="HEAD")}, cancellable=true)
    public void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        ClickBlockEvent event = new ClickBlockEvent(1, posBlock, directionFacing);
        TrollGod.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            cir.cancel();
        }
    }
}

