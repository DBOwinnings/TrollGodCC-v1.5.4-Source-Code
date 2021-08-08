package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import me.hollow.trollgod.*;
import java.awt.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.client.events.*;
import me.hollow.trollgod.api.mixin.accessors.*;
import net.minecraft.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import tcb.bces.listener.*;
import net.minecraft.world.*;
import net.minecraft.block.*;

@ModuleManifest(label = "SpeedMine", category = Category.PLAYER, color = 10858157)
public class SpeedMine extends Module
{
    public final Setting<Boolean> reset;
    private static SpeedMine INSTANCE;
    private final Timer renderTimer;
    private BlockPos currentPos;
    
    public SpeedMine() {
        this.reset = (Setting<Boolean>)this.register(new Setting("Reset", (T)true));
        this.renderTimer = new Timer();
        this.currentPos = null;
        SpeedMine.INSTANCE = this;
    }
    
    public static SpeedMine getInstance() {
        return SpeedMine.INSTANCE;
    }
    
    @Override
    public void onRender3D() {
        if (this.currentPos != null && this.mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
            this.currentPos = null;
        }
        if (this.currentPos != null) {
            final Color color = new Color(this.renderTimer.hasReached((int)(2000.0f * TrollGod.INSTANCE.getTpsManager().getTpsFactor())) ? 0 : 255, this.renderTimer.hasReached((int)(2000.0f * TrollGod.INSTANCE.getTpsManager().getTpsFactor())) ? 255 : 0, 0, 255);
            RenderUtil.drawProperBoxESP(this.currentPos, color, 1.0f, true, true, 40, 1.0f);
        }
    }
    
    @Subscribe
    public void onClickBlock(final ClickBlockEvent event) {
        if (event.getStage() == 0) {
            if (((IPlayerControllerMP)this.mc.playerController).getCurBlockDamageMP() > 0.1f) {
                ((IPlayerControllerMP)this.mc.playerController).setIsHittingBlock(true);
            }
        }
        else if (this.canBreak(event.getPos())) {
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
    
    public boolean canBreak(final BlockPos pos) {
        final Block block = this.mc.world.getBlockState(pos).getBlock();
        return block.getBlockHardness(this.mc.world.getBlockState(pos), (World)this.mc.world, pos) != -1.0f;
    }
}
