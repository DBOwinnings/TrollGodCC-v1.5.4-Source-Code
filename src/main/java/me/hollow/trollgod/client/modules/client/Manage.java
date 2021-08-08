package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.server.*;
import java.util.*;
import me.hollow.trollgod.api.mixin.mixins.network.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.util.text.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "Manage", category = Category.CLIENT, persistent = true, listen = false, drawn = false)
public final class Manage extends Module
{
    public final Setting<Boolean> placeSwing;
    public final Setting<Boolean> unfocusedLimit;
    public final Setting<Integer> unfocusedFPS;
    public final Setting<Boolean> tabTweaks;
    public final Setting<Boolean> highlightFriends;
    public final Setting<Boolean> chatTweaks;
    private final Setting<Boolean> timestamps;
    public final Setting<Boolean> giantBeetleSoundsLikeAJackhammer;
    public static Manage INSTANCE;
    
    public Manage() {
        this.placeSwing = (Setting<Boolean>)this.register(new Setting("Place Swing", (T)true));
        this.unfocusedLimit = (Setting<Boolean>)this.register(new Setting("Limit Unfocused", (T)true));
        this.unfocusedFPS = (Setting<Integer>)this.register(new Setting("Unfocused FPS", (T)60, (T)1, (T)240, v -> this.unfocusedLimit.getValue()));
        this.tabTweaks = (Setting<Boolean>)this.register(new Setting("Tab Tweaks", (T)true));
        this.highlightFriends = (Setting<Boolean>)this.register(new Setting("Highlight Friends", (T)true, v -> this.tabTweaks.getValue()));
        this.chatTweaks = (Setting<Boolean>)this.register(new Setting("Chat Tweaks", (T)true));
        this.timestamps = (Setting<Boolean>)this.register(new Setting("Timestamps", (T)true, v -> this.chatTweaks.getValue()));
        this.giantBeetleSoundsLikeAJackhammer = (Setting<Boolean>)this.register(new Setting("No Rect", (T)true, v -> this.chatTweaks.getValue()));
        Manage.INSTANCE = this;
    }
    
    @Override
    public void onLoad() {
        TrollGod.INSTANCE.getBus().register(this);
    }
    
    @Subscribe
    public final void onPacketReceive(final PacketEvent.Receive event) {
        if (this.timestamps.getValue() && event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat)event.getPacket();
            final Date date = new Date();
            final AccessorSPacketChat chatPacket = (AccessorSPacketChat)event.getPacket();
            boolean add = false;
            if (date.getMinutes() <= 9) {
                add = true;
            }
            final String time = "<" + ChatFormatting.LIGHT_PURPLE + date.getHours() + ":" + (add ? "0" : "") + date.getMinutes() + ChatFormatting.RESET + "> ";
            chatPacket.setChatComponent((ITextComponent)new TextComponentString(time + packet.getChatComponent().getFormattedText()));
        }
    }
}
