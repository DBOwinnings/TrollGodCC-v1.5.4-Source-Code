/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="NoRender", listen=false, category=Module.Category.VISUAL, color=26112)
public class NoRender
extends Module {
    public final Setting<Boolean> noBossOverlay = this.register(new Setting<Boolean>("NoBoss", true));
    public static NoRender INSTANCE;

    public NoRender() {
        INSTANCE = this;
    }
}

