package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.*;

@ModuleManifest(label = "NoFall", listen = false, category = Category.PLAYER)
public class NoFall extends Module
{
    public static NoFall INSTANCE;
    
    public NoFall() {
        (NoFall.INSTANCE = this).setSuffix("Packet");
    }
}
