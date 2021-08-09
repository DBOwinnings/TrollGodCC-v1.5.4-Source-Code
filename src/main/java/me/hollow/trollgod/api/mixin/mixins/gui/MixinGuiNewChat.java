/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.gui;

import me.hollow.trollgod.api.util.font.RainbowText;
import me.hollow.trollgod.client.modules.client.Manage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={GuiNewChat.class})
public class MixinGuiNewChat {
    @Shadow
    @Final
    private Minecraft mc;

    @Redirect(method={"drawChat"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void drawChatHook1(int left, int top, int right, int bottom, int color) {
        if (Manage.INSTANCE.chatTweaks.getValue().booleanValue() && Manage.INSTANCE.giantBeetleSoundsLikeAJackhammer.getValue().booleanValue()) {
            return;
        }
        Gui.drawRect((int)left, (int)top, (int)right, (int)bottom, (int)color);
    }

    @Redirect(method={"drawChat"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    public int drawString(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (StringUtils.contains((CharSequence)text, (CharSequence)"\u00a7.TrollGod")) {
            RainbowText.drawStringWithShadow(text, x, y, color, true);
            return 0;
        }
        return this.mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    private int rainbow(double x, double y, float speed) {
        double scale = 0.01568627450980392;
        double d = 0.7071067811865483;
        double pos = (x * d + y * d) * -scale + (double)((float)System.currentTimeMillis() % speed) * 2.0 * -Math.PI / (double)speed;
        return 0xFF000000 | MathHelper.clamp((int)MathHelper.floor((double)(255.0 * (0.5 + Math.sin(0.0 + pos)))), (int)0, (int)255) << 16 | MathHelper.clamp((int)MathHelper.floor((double)(255.0 * (0.5 + Math.sin(2.0943951023931953 + pos)))), (int)0, (int)255) << 8 | MathHelper.clamp((int)MathHelper.floor((double)(255.0 * (0.5 + Math.sin(4.1887902047863905 + pos)))), (int)0, (int)255);
    }
}

