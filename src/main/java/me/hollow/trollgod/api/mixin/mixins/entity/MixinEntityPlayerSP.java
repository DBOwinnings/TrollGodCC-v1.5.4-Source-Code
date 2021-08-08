package me.hollow.trollgod.api.mixin.mixins.entity;

import net.minecraft.client.entity.*;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.world.*;
import com.mojang.authlib.*;
import net.minecraft.entity.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.events.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.util.math.*;
import me.hollow.trollgod.client.modules.combat.*;
import me.hollow.trollgod.client.modules.misc.*;

@Mixin({ EntityPlayerSP.class })
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer
{
    @Shadow
    protected Minecraft mc;
    
    public MixinEntityPlayerSP(final World worldIn, final GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }
    
    public void move(final MoverType type, final double x, final double y, final double z) {
        final MoveEvent event = new MoveEvent(x, y, z);
        TrollGod.INSTANCE.getBus().post(event);
        super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
    }
    
    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("HEAD") })
    public void onUpdatePre(final CallbackInfo ci) {
        TrollGod.INSTANCE.getRotationManager().updateRotations();
        TrollGod.INSTANCE.getBus().post(new UpdateEvent(0));
    }
    
    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("RETURN") })
    public void onUpdatePost(final CallbackInfo ci) {
        TrollGod.INSTANCE.getBus().post(new UpdateEvent(1));
        TrollGod.INSTANCE.getRotationManager().restoreRotations();
    }
    
    @Inject(method = { "onUpdate" }, at = { @At("HEAD") })
    public void update(final CallbackInfo ci) {
        if (AutoFeetPlace.INSTANCE.isEnabled()) {
            AutoFeetPlace.INSTANCE.doPlace();
        }
    }
    
    @Inject(method = { "onUpdate" }, at = { @At("RETURN") })
    public void update2(final CallbackInfo ci) {
        if (AutoFeetPlace.INSTANCE.isEnabled()) {
            AutoFeetPlace.INSTANCE.doPlace();
        }
    }
    
    @Inject(method = { "pushOutOfBlocks" }, at = { @At("HEAD") }, cancellable = true)
    public void push(final double x, final double y, final double z, final CallbackInfoReturnable<Boolean> cir) {
        if (new BlockPos(this.mc.player.getPositionVector()).equals((Object)Burrow.getInstance().startPos) || AntiPush.INSTANCE.isEnabled()) {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }
}
