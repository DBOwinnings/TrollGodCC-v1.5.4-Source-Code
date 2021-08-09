/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.player;

import java.awt.Color;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.mixin.accessors.IPlayerControllerMP;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.Timer;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.events.ClickBlockEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="SpeedMine", category=Module.Category.PLAYER, color=10858157)
public class SpeedMine
extends Module {
    public final Setting<Boolean> reset = this.register(new Setting<Boolean>("Reset", true));
    private static SpeedMine INSTANCE;
    private final Timer renderTimer = new Timer();
    private BlockPos currentPos = null;

    public SpeedMine() {
        INSTANCE = this;
    }

    public static SpeedMine getInstance() {
        return INSTANCE;
    }

    @Override
    public void onRender3D() {
        if (this.currentPos != null && this.mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
            this.currentPos = null;
        }
        if (this.currentPos != null) {
            Color color = new Color(this.renderTimer.hasReached((int)(2000.0f * TrollGod.INSTANCE.getTpsManager().getTpsFactor())) ? 0 : 255, this.renderTimer.hasReached((int)(2000.0f * TrollGod.INSTANCE.getTpsManager().getTpsFactor())) ? 255 : 0, 0, 255);
            RenderUtil.drawProperBoxESP(this.currentPos, color, 1.0f, true, true, 40, 1.0f);
        }
    }

    @Subscribe
    public void onClickBlock(ClickBlockEvent event) {
        if (event.getStage() == 0) {
            if (((IPlayerControllerMP)this.mc.playerController).getCurBlockDamageMP() > 0.1f) {
                ((IPlayerControllerMP)this.mc.playerController).setIsHittingBlock(true);
            }
        } else if (this.canBreak(event.getPos())) {
            ((IPlayerControllerMP)this.mc.playerController).setIsHittingBlock(false);
            if (this.currentPos == null) {
                this.currentPos = event.getPos();
                this.renderTimer.reset();
            }
            this.mc.player.swingArm(EnumHand.MAIN_HAND);
            this.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            this.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            this.currentPos = event.getPos();
            event.setCancelled();
        }
    }

    public boolean canBreak(BlockPos pos) {
        Block block = this.mc.world.getBlockState(pos).getBlock();
        return block.getBlockHardness(this.mc.world.getBlockState(pos), (World)this.mc.world, pos) != -1.0f;
    }
}

