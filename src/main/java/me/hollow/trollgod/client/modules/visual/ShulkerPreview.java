package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;

@ModuleManifest(label = "ShulkerPreview", listen = false, category = Category.VISUAL, color = 13408512)
public class ShulkerPreview extends Module
{
    private static ShulkerPreview INSTANCE;
    
    public ShulkerPreview() {
        ShulkerPreview.INSTANCE = this;
    }
    
    public static ShulkerPreview getInstance() {
        return ShulkerPreview.INSTANCE;
    }
}
