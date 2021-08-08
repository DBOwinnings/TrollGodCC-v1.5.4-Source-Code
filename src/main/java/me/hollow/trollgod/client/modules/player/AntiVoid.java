package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;

@ModuleManifest(label = "AntiVoid", listen = false, category = Category.PLAYER)
public class AntiVoid extends Module
{
    public final Setting<Integer> height;
    
    public AntiVoid() {
        this.height = (Setting<Integer>)this.register(new Setting("height", (T)255, (T)0, (T)255));
    }
}
