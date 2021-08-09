/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.events.UpdateEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="BehindSpoof", category=Module.Category.PLAYER)
public class BehindSpoof
extends Module {
    private final Setting<Boolean> onlyOnBackwards = this.register(new Setting<Boolean>("Require Movement", true));

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        if (this.onlyOnBackwards.getValue().booleanValue() && !this.mc.gameSettings.keyBindBack.isKeyDown()) {
            return;
        }
        TrollGod.INSTANCE.getRotationManager().setPlayerRotations((this.mc.player.rotationYaw + 180.0f) % 360.0f, this.mc.player.rotationPitch);
    }
}

