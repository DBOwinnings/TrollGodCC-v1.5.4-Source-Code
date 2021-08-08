package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.client.entity.*;

@ModuleManifest(label = "ReverseStep", listen = false, category = Category.MOVEMENT, color = 11437534)
public class ReverseStep extends Module
{
    private final Setting<Integer> speed;
    private final Setting<Boolean> test;
    
    public ReverseStep() {
        this.speed = (Setting<Integer>)this.register(new Setting("Speed", (T)10, (T)1, (T)20));
        this.test = (Setting<Boolean>)this.register(new Setting("Test", (T)false));
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.player.isInWater() || this.mc.player.isInLava() || this.mc.player.isOnLadder()) {
            return;
        }
        if (this.mc.player.onGround) {
            if (this.test.getValue()) {
                this.mc.player.motionY = -10.0;
            }
            else {
                final EntityPlayerSP player = this.mc.player;
                player.motionY -= this.speed.getValue() / 10.0f;
            }
        }
    }
}
