/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="ShulkerPreview", listen=false, category=Module.Category.VISUAL, color=0xCC9900)
public class ShulkerPreview
extends Module {
    private static ShulkerPreview INSTANCE;

    public ShulkerPreview() {
        INSTANCE = this;
    }

    public static ShulkerPreview getInstance() {
        return INSTANCE;
    }
}

