package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;

@ModuleManifest(label = "NoArmorRender", listen = false, category = Category.VISUAL, color = 39168)
public class NoArmorRender extends Module
{
    public final Setting<Boolean> helmet;
    public final Setting<Boolean> chestplate;
    public final Setting<Boolean> thighHighs;
    public final Setting<Boolean> boots;
    public static NoArmorRender INSTANCE;
    
    public NoArmorRender() {
        this.helmet = (Setting<Boolean>)this.register(new Setting("Helmet", (T)true));
        this.chestplate = (Setting<Boolean>)this.register(new Setting("Chestplate", (T)true));
        this.thighHighs = (Setting<Boolean>)this.register(new Setting("Leggings", (T)true));
        this.boots = (Setting<Boolean>)this.register(new Setting("Boots", (T)true));
        NoArmorRender.INSTANCE = this;
    }
}
