/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="NoFall", listen=false, category=Module.Category.PLAYER)
public class NoFall
extends Module {
    public static NoFall INSTANCE;

    public NoFall() {
        INSTANCE = this;
        this.setSuffix("Packet");
    }
}

