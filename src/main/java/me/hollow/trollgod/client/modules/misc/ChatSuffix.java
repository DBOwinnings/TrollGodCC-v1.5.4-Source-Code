/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.api.mixin.mixins.network.AccessorCPacketChatMessage;
import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import me.hollow.trollgod.client.modules.client.ClickGui;
import net.minecraft.network.play.client.CPacketChatMessage;
import org.apache.commons.lang3.StringUtils;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="ChatSuffix", category=Module.Category.MISC)
public class ChatSuffix
extends Module {
    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            AccessorCPacketChatMessage packet = (AccessorCPacketChatMessage)event.getPacket();
            if (StringUtils.startsWith((CharSequence)packet.getMessage(), (CharSequence)ClickGui.getInstance().prefix.getValue()) || StringUtils.startsWith((CharSequence)packet.getMessage(), (CharSequence)"/")) {
                return;
            }
            packet.setMessage(((CPacketChatMessage)event.getPacket()).getMessage() + " | Konas");
        }
    }
}

