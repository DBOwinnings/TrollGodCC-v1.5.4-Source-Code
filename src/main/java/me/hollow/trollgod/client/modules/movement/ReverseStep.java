/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="ReverseStep", listen=false, category=Module.Category.MOVEMENT, color=11437534)
public class ReverseStep
extends Module {
    private final Setting<Integer> speed = this.register(new Setting<Integer>("Speed", 10, 1, 20));
    private final Setting<Boolean> test = this.register(new Setting<Boolean>("Test", false));

    @Override
    public void onUpdate() {
        if (this.mc.player.isInWater() || this.mc.player.isInLava() || this.mc.player.isOnLadder()) {
            return;
        }
        if (this.mc.player.onGround) {
            this.mc.player.motionY = this.test.getValue().booleanValue() ? -10.0 : (this.mc.player.motionY -= (double)((float)this.speed.getValue().intValue() / 10.0f));
        }
    }
}

