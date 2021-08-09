/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import java.awt.Color;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="EnchantColor", listen=false, category=Module.Category.VISUAL, color=0xFF9933)
public class EnchantColor
extends Module {
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    public static EnchantColor INSTANCE;

    public EnchantColor() {
        INSTANCE = this;
    }

    @Override
    public int getColor() {
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
    }
}

