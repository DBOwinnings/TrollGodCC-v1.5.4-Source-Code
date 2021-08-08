package me.hollow.trollgod.api.mixin.mixins.client;

import me.hollow.trollgod.api.mixin.accessors.*;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.gen.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.client.events.*;
import org.spongepowered.asm.mixin.injection.*;
import org.lwjgl.input.*;
import me.hollow.trollgod.client.modules.misc.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.hollow.trollgod.client.modules.client.*;
import org.lwjgl.opengl.*;

@Mixin(value = { Minecraft.class }, priority = 1001)
public abstract class MixinMinecraft implements IMinecraft
{
    @Accessor
    @Override
    public abstract void setRightClickDelayTimer(final int p0);
    
    @Inject(method = { "runTickKeyboard" }, at = { @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE) })
    private void onKeyboard(final CallbackInfo callbackInfo) {
        if (Keyboard.getEventKeyState()) {
            for (int i = 0; i < TrollGod.INSTANCE.getModuleManager().getSize(); ++i) {
                final Module m = TrollGod.INSTANCE.getModuleManager().getModules().get(i);
                if (m.getKey() == Keyboard.getEventKey()) {
                    m.toggle();
                }
            }
            TrollGod.INSTANCE.getBus().post(new KeyEvent(Keyboard.getEventKey()));
        }
    }
    
    @Inject(method = { "runTickMouse" }, at = { @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I", ordinal = 0, shift = At.Shift.BEFORE) })
    private void mouseClick(final CallbackInfo ci) {
        if (Mouse.getEventButtonState()) {
            MiddleClick.getInstance().run(Mouse.getEventButton());
            MultiTask.getInstance().onMouse(Mouse.getEventButton());
        }
    }
    
    @Inject(method = { "getLimitFramerate" }, at = { @At("HEAD") }, cancellable = true)
    public void limitFps(final CallbackInfoReturnable<Integer> cir) {
        if (Manage.INSTANCE.unfocusedLimit.getValue() && !Display.isActive()) {
            cir.setReturnValue(Manage.INSTANCE.unfocusedFPS.getValue());
        }
    }
    
    @Inject(method = { "init" }, at = { @At("RETURN") })
    public void init(final CallbackInfo ci) {
        TrollGod.INSTANCE.init();
    }
}
