package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;

@ModuleManifest(label = "PopCounter", category = Category.CLIENT, listen = false)
public class PopCounter extends Module
{
    public static Setting<Boolean> notify;
    private static PopCounter INSTANCE;
    
    public PopCounter() {
        (PopCounter.INSTANCE = this).addSetting(PopCounter.notify);
    }
    
    public static PopCounter getInstance() {
        return PopCounter.INSTANCE;
    }
    
    static {
        PopCounter.notify = new Setting<Boolean>("Notify", true);
    }
}
