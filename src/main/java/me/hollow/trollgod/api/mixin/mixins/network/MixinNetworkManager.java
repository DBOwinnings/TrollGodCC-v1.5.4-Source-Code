package me.hollow.trollgod.api.mixin.mixins.network;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.network.play.client.*;
import me.hollow.trollgod.client.events.*;
import me.hollow.trollgod.*;
import org.spongepowered.asm.mixin.injection.*;
import io.netty.channel.*;
import net.minecraft.network.play.server.*;
import me.hollow.trollgod.client.modules.*;

@Mixin({ NetworkManager.class })
public class MixinNetworkManager
{
    @Inject(method = { "sendPacket(Lnet/minecraft/network/Packet;)V" }, at = { @At("HEAD") }, cancellable = true)
    public void onSendPacket(final Packet<?> packetIn, final CallbackInfo ci) {
        if (packetIn instanceof CPacketKeepAlive) {
            return;
        }
        final PacketEvent.Send event = new PacketEvent.Send(packetIn);
        TrollGod.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    
    @Inject(method = { "channelRead0" }, at = { @At("HEAD") }, cancellable = true)
    public void onReceivePacket(final ChannelHandlerContext p_channelRead0_1_, final Packet<?> packet, final CallbackInfo ci) {
        if (packet instanceof SPacketKeepAlive) {
            return;
        }
        final PacketEvent.Receive event = new PacketEvent.Receive(packet);
        TrollGod.INSTANCE.getBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    
    @Inject(method = { "handleDisconnection" }, at = { @At("HEAD") })
    public void onDisconnect(final CallbackInfo ci) {
        TrollGod.INSTANCE.getModuleManager().getModules().forEach(Module::onDisconnect);
    }
}
