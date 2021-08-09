/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.entity;

import com.mojang.authlib.GameProfile;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.client.events.MoveEvent;
import me.hollow.trollgod.client.events.UpdateEvent;
import me.hollow.trollgod.client.modules.combat.AutoFeetPlace;
import me.hollow.trollgod.client.modules.combat.Burrow;
import me.hollow.trollgod.client.modules.misc.AntiPush;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityPlayerSP.class})
public abstract class MixinEntityPlayerSP
extends AbstractClientPlayer {
    @Shadow
    protected Minecraft mc;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    public void move(MoverType type, double x, double y, double z) {
        MoveEvent event = new MoveEvent(x, y, z);
        TrollGod.INSTANCE.getBus().post(event);
        super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
    }

    @Inject(method={"onUpdateWalkingPlayer"}, at={@At(value="HEAD")})
    public void onUpdatePre(CallbackInfo ci) {
        TrollGod.INSTANCE.getRotationManager().updateRotations();
        TrollGod.INSTANCE.getBus().post(new UpdateEvent(0));
    }

    @Inject(method={"onUpdateWalkingPlayer"}, at={@At(value="RETURN")})
    public void onUpdatePost(CallbackInfo ci) {
        TrollGod.INSTANCE.getBus().post(new UpdateEvent(1));
        TrollGod.INSTANCE.getRotationManager().restoreRotations();
    }

    @Inject(method={"onUpdate"}, at={@At(value="HEAD")})
    public void update(CallbackInfo ci) {
        if (AutoFeetPlace.INSTANCE.isEnabled()) {
            AutoFeetPlace.INSTANCE.doPlace();
        }
    }

    @Inject(method={"onUpdate"}, at={@At(value="RETURN")})
    public void update2(CallbackInfo ci) {
        if (AutoFeetPlace.INSTANCE.isEnabled()) {
            AutoFeetPlace.INSTANCE.doPlace();
        }
    }

    @Inject(method={"pushOutOfBlocks"}, at={@At(value="HEAD")}, cancellable=true)
    public void push(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (new BlockPos(this.mc.player.getPositionVector()).equals((Object)Burrow.getInstance().startPos) || AntiPush.INSTANCE.isEnabled()) {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }
}

