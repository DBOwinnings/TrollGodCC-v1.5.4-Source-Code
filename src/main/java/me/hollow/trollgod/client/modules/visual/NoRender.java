package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;

@ModuleManifest(label = "NoRender", listen = false, category = Category.VISUAL, color = 26112)
public class NoRender extends Module
{
    public final Setting<Boolean> noBossOverlay;
    public static NoRender INSTANCE;
    
    public NoRender() {
        this.noBossOverlay = (Setting<Boolean>)this.register(new Setting("NoBoss", (T)true));
        NoRender.INSTANCE = this;
    }
}
