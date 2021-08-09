/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.network;

import io.netty.channel.ChannelHandlerContext;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.modules.Module;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketKeepAlive;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={NetworkManager.class})
public class MixinNetworkManager {
    @Inject(method={"sendPacket(Lnet/minecraft/network/Packet;)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void onSendPacket(Packet<?> packetIn, CallbackInfo ci) {
        if (packetIn instanceof CPacketKeepAlive) {
            return;
        }
        PacketEvent.Send event = new PacketEvent.Send(packetIn);
        TrollGod.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"channelRead0"}, at={@At(value="HEAD")}, cancellable=true)
    public void onReceivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof SPacketKeepAlive) {
            return;
        }
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        TrollGod.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"handleDisconnection"}, at={@At(value="HEAD")})
    public void onDisconnect(CallbackInfo ci) {
        TrollGod.INSTANCE.getModuleManager().getModules().forEach(Module::onDisconnect);
    }
}

