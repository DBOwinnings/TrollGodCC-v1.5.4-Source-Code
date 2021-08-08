package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.client.*;
import me.hollow.trollgod.api.mixin.mixins.network.*;
import me.hollow.trollgod.client.modules.client.*;
import org.apache.commons.lang3.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "ChatSuffix", category = Category.MISC)
public class ChatSuffix extends Module
{
    @Subscribe
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            final AccessorCPacketChatMessage packet = (AccessorCPacketChatMessage)event.getPacket();
            if (StringUtils.startsWith((CharSequence)packet.getMessage(), (CharSequence)ClickGui.getInstance().prefix.getValue()) || StringUtils.startsWith((CharSequence)packet.getMessage(), (CharSequence)"/")) {
                return;
            }
            packet.setMessage(((CPacketChatMessage)event.getPacket()).getMessage() + " | Konas");
        }
    }
}
