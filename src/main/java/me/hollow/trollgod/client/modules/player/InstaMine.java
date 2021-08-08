package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.util.math.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import me.hollow.trollgod.api.mixin.accessors.*;
import tcb.bces.listener.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;

@ModuleManifest(label = "InstaMine", category = Category.PLAYER, color = 16445103)
public class InstaMine extends Module
{
    private final Setting<Integer> delay;
    private final Timer breakTimer;
    private BlockPos renderBlock;
    private BlockPos lastBlock;
    private boolean packetCancel;
    private EnumFacing direction;
    
    public InstaMine() {
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)20, (T)0, (T)500));
        this.breakTimer = new Timer();
        this.packetCancel = false;
    }
    
    @Override
    public void onUpdate() {
        if (this.renderBlock != null && this.breakTimer.hasReached(this.delay.getValue())) {
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.renderBlock, this.direction));
            this.breakTimer.reset();
        }
        ((IPlayerControllerMP)this.mc.playerController).setBlockHitDelay(0);
    }
    
    @Subscribe
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging digPacket = (CPacketPlayerDigging)event.getPacket();
            if (digPacket.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK && this.packetCancel) {
                event.setCancelled();
            }
        }
    }
    
    @Subscribe
    public void onDamageBlock(final ClickBlockEvent event) {
        if (event.getStage() != 1) {
            return;
        }
        if (this.canBreak(event.getPos())) {
            if (this.lastBlock == null || event.getPos() != this.lastBlock) {
                this.packetCancel = false;
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
                this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                this.packetCancel = true;
            }
            else {
                this.packetCancel = true;
            }
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            this.renderBlock = event.getPos();
            this.lastBlock = event.getPos();
            this.direction = event.getFacing();
            event.setCancelled();
        }
    }
    
    private boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = this.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)this.mc.world, pos) != -1.0f;
    }
    
    public BlockPos getTarget() {
        return this.renderBlock;
    }
    
    public void setTarget(final BlockPos pos) {
        this.renderBlock = pos;
        this.packetCancel = false;
        this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        this.packetCancel = true;
        this.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        this.direction = EnumFacing.DOWN;
        this.lastBlock = pos;
    }
}
