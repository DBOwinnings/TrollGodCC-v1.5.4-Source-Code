/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="TimeChanger", category=Module.Category.VISUAL, listen=false, color=0x9966FF)
public final class TimeChanger
extends Module {
    public final Setting<Integer> timeSetting = this.register(new Setting<Integer>("Time", 12000, 0, 23000));
    public static TimeChanger INSTANCE;

    public TimeChanger() {
        INSTANCE = this;
    }
}

