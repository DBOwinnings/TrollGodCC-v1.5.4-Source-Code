/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="AntiPush", listen=false, category=Module.Category.MISC, color=0x336600)
public class AntiPush
extends Module {
    public static AntiPush INSTANCE;

    public AntiPush() {
        INSTANCE = this;
    }
}

