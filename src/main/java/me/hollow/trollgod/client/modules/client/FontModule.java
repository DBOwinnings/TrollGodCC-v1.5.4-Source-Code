package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.events.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "Font", category = Category.CLIENT)
public class FontModule extends Module
{
    public final Setting<String> font;
    public final Setting<Integer> size;
    public final Setting<Boolean> antiAlias;
    public static FontModule INSTANCE;
    
    public FontModule() {
        this.font = (Setting<String>)this.register(new Setting("Font", (T)"Verdana"));
        this.size = (Setting<Integer>)this.register(new Setting("Size", (T)18, (T)12, (T)24));
        this.antiAlias = (Setting<Boolean>)this.register(new Setting("Anti Alias", (T)true));
        FontModule.INSTANCE = this;
    }
    
    @Override
    public void onLoad() {
        TrollGod.fontManager.updateFont();
    }
    
    @Subscribe
    public void onSetting(final ClientEvent event) {
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
    
    static {
        FontModule.INSTANCE = new FontModule();
    }
}
