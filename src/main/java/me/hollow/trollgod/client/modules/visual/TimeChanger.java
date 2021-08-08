package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;

@ModuleManifest(label = "TimeChanger", category = Category.VISUAL, listen = false, color = 10053375)
public final class TimeChanger extends Module
{
    public final Setting<Integer> timeSetting;
    public static TimeChanger INSTANCE;
    
    public TimeChanger() {
        this.timeSetting = (Setting<Integer>)this.register(new Setting("Time", (T)12000, (T)0, (T)23000));
        TimeChanger.INSTANCE = this;
    }
}
