/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Date;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.mixin.mixins.network.AccessorSPacketChat;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="Manage", category=Module.Category.CLIENT, persistent=true, listen=false, drawn=false)
public final class Manage
extends Module {
    public final Setting<Boolean> placeSwing = this.register(new Setting<Boolean>("Place Swing", true));
    public final Setting<Boolean> unfocusedLimit = this.register(new Setting<Boolean>("Limit Unfocused", true));
    public final Setting<Integer> unfocusedFPS = this.register(new Setting<Integer>("Unfocused FPS", 60, 1, 240, v -> this.unfocusedLimit.getValue()));
    public final Setting<Boolean> tabTweaks = this.register(new Setting<Boolean>("Tab Tweaks", true));
    public final Setting<Boolean> highlightFriends = this.register(new Setting<Boolean>("Highlight Friends", true, v -> this.tabTweaks.getValue()));
    public final Setting<Boolean> chatTweaks = this.register(new Setting<Boolean>("Chat Tweaks", true));
    private final Setting<Boolean> timestamps = this.register(new Setting<Boolean>("Timestamps", true, v -> this.chatTweaks.getValue()));
    public final Setting<Boolean> giantBeetleSoundsLikeAJackhammer = this.register(new Setting<Boolean>("No Rect", true, v -> this.chatTweaks.getValue()));
    public static Manage INSTANCE;

    public Manage() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        TrollGod.INSTANCE.getBus().register(this);
    }

    @Subscribe
    public final void onPacketReceive(PacketEvent.Receive event) {
        if (this.timestamps.getValue().booleanValue() && event.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat)event.getPacket();
            Date date = new Date();
            AccessorSPacketChat chatPacket = (AccessorSPacketChat)event.getPacket();
            boolean add = false;
            if (date.getMinutes() <= 9) {
                add = true;
            }
            String time = "<" + ChatFormatting.LIGHT_PURPLE + date.getHours() + ":" + (add ? "0" : "") + date.getMinutes() + ChatFormatting.RESET + "> ";
            chatPacket.setChatComponent((ITextComponent)new TextComponentString(time + packet.getChatComponent().getFormattedText()));
        }
    }
}

