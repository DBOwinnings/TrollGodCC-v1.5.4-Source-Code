/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="Step", listen=false, category=Module.Category.MOVEMENT, color=-256)
public class Step
extends Module {
    private final Setting<Boolean> placeHolder = this.register(new Setting<Boolean>("PlaceHolder", true));

    @Override
    public void onEnable() {
        if (!this.placeHolder.getValue().booleanValue()) {
            this.mc.player.stepHeight = 2.0f;
        }
    }

    @Override
    public void onDisable() {
        if (!this.placeHolder.getValue().booleanValue()) {
            this.mc.player.stepHeight = 0.6f;
        }
    }
}

