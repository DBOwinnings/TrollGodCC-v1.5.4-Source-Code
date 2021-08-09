/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="AntiVoid", listen=false, category=Module.Category.PLAYER)
public class AntiVoid
extends Module {
    public final Setting<Integer> height = this.register(new Setting<Integer>("height", 255, 0, 255));
}

