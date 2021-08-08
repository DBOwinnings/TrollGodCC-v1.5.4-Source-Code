package me.hollow.trollgod.api.mixin.mixins.gui;

import net.minecraft.client.*;
import org.spongepowered.asm.mixin.*;
import me.hollow.trollgod.client.modules.client.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.gui.*;
import org.apache.commons.lang3.*;
import me.hollow.trollgod.api.util.font.*;
import net.minecraft.util.math.*;

@Mixin({ GuiNewChat.class })
public class MixinGuiNewChat
{
    @Shadow
    @Final
    private Minecraft mc;
    
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void drawChatHook1(final int left, final int top, final int right, final int bottom, final int color) {
        if (Manage.INSTANCE.chatTweaks.getValue() && Manage.INSTANCE.giantBeetleSoundsLikeAJackhammer.getValue()) {
            return;
        }
        Gui.drawRect(left, top, right, bottom, color);
    }
    
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    public int drawString(final FontRenderer fontRenderer, final String text, final float x, final float y, final int color) {
        if (StringUtils.contains((CharSequence)text, (CharSequence)"§.TrollGod")) {
            RainbowText.drawStringWithShadow(text, x, y, color, true);
            return 0;
        }
        return this.mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }
    
    private int rainbow(final double x, final double y, final float speed) {
        final double scale = 0.01568627450980392;
        final double d = 0.7071067811865483;
        final double pos = (x * d + y * d) * -scale + System.currentTimeMillis() % speed * 2.0 * -3.141592653589793 / speed;
        return 0xFF000000 | MathHelper.clamp(MathHelper.floor(255.0 * (0.5 + Math.sin(0.0 + pos))), 0, 255) << 16 | MathHelper.clamp(MathHelper.floor(255.0 * (0.5 + Math.sin(2.0943951023931953 + pos))), 0, 255) << 8 | MathHelper.clamp(MathHelper.floor(255.0 * (0.5 + Math.sin(4.1887902047863905 + pos))), 0, 255);
    }
}
