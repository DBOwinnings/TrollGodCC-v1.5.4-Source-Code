/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="Speed", listen=false, category=Module.Category.MOVEMENT, color=11437534)
public class Speed
extends Module {
    @Override
    public void onEnable() {
        this.setSuffix("Strafe");
    }
}

