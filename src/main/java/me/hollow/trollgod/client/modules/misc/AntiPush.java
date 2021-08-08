package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.*;

@ModuleManifest(label = "AntiPush", listen = false, category = Category.MISC, color = 3368448)
public class AntiPush extends Module
{
    public static AntiPush INSTANCE;
    
    public AntiPush() {
        AntiPush.INSTANCE = this;
    }
}
