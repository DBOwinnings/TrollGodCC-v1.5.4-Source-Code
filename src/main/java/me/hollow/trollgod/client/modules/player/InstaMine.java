/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.api.mixin.accessors.IPlayerControllerMP;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.Timer;
import me.hollow.trollgod.client.events.ClickBlockEvent;
import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="InstaMine", category=Module.Category.PLAYER, color=0xFAEEAF)
public class InstaMine
extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 20, 0, 500));
    private final Timer breakTimer = new Timer();
    private BlockPos renderBlock;
    private BlockPos lastBlock;
    private boolean packetCancel = false;
    private EnumFacing direction;

    @Override
    public void onUpdate() {
        if (this.renderBlock != null && this.breakTimer.hasReached(this.delay.getValue().intValue())) {
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.renderBlock, this.direction));
            this.breakTimer.reset();
        }
        ((IPlayerControllerMP)this.mc.playerController).setBlockHitDelay(0);
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        CPacketPlayerDigging digPacket;
        if (event.getPacket() instanceof CPacketPlayerDigging && (digPacket = (CPacketPlayerDigging)event.getPacket()).getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK && this.packetCancel) {
            event.setCancelled();
        }
    }

    @Subscribe
    public void onDamageBlock(ClickBlockEvent event) {
        if (event.getStage() != 1) {
            return;
        }
        if (this.canBreak(event.getPos())) {
            if (this.lastBlock == null || event.getPos() != this.lastBlock) {
                this.packetCancel = false;
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                this.packetCancel = true;
            } else {
                this.packetCancel = true;
            }
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            this.renderBlock = event.getPos();
            this.lastBlock = event.getPos();
            this.direction = event.getFacing();
            event.setCancelled();
        }
    }

    private boolean canBreak(BlockPos pos) {
        IBlockState blockState = this.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)this.mc.world, pos) != -1.0f;
    }

    public BlockPos getTarget() {
        return this.renderBlock;
    }

    public void setTarget(BlockPos pos) {
        this.renderBlock = pos;
        this.packetCancel = false;
        this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        this.packetCancel = true;
        this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        this.direction = EnumFacing.DOWN;
        this.lastBlock = pos;
    }
}

