/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.events.ClientEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="Font", category=Module.Category.CLIENT)
public class FontModule
extends Module {
    public final Setting<String> font = this.register(new Setting<String>("Font", "Verdana"));
    public final Setting<Integer> size = this.register(new Setting<Integer>("Size", 18, 12, 24));
    public final Setting<Boolean> antiAlias = this.register(new Setting<Boolean>("Anti Alias", true));
    public static FontModule INSTANCE = new FontModule();

    public FontModule() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        TrollGod.fontManager.updateFont();
    }

    @Subscribe
    public void onSetting(ClientEvent event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (event.getSetting() == this.size || event.getSetting() == this.font || event.getSetting() == null || event.getSetting() == this.antiAlias) {
            TrollGod.fontManager.updateFont();
        }
    }

    @Override
    public void onEnable() {
        TrollGod.fontManager.customFont = true;
    }

    @Override
    public void onDisable() {
        TrollGod.fontManager.customFont = false;
    }
}

