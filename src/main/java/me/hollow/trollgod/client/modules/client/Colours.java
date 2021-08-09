/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.client;

import java.awt.Color;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.events.ClientEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="Colours", category=Module.Category.CLIENT, persistent=true)
public class Colours
extends Module {
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    public static Colours INSTANCE;
    private int color;

    public Colours() {
        INSTANCE = this;
    }

    @Subscribe
    public void onSetting(ClientEvent event) {
        this.color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
    }

    @Override
    public void onLoad() {
        this.color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
    }

    @Override
    public final int getColor() {
        return this.color;
    }
}

