/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="PopCounter", category=Module.Category.CLIENT, listen=false)
public class PopCounter
extends Module {
    public static Setting<Boolean> notify = new Setting<Boolean>("Notify", true);
    private static PopCounter INSTANCE;

    public PopCounter() {
        INSTANCE = this;
        this.addSetting(notify);
    }

    public static PopCounter getInstance() {
        return INSTANCE;
    }
}

