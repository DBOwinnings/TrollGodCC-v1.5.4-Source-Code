package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.client.events.*;
import java.awt.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "Colours", category = Category.CLIENT, persistent = true)
public class Colours extends Module
{
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    public static Colours INSTANCE;
    private int color;
    
    public Colours() {
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255));
        Colours.INSTANCE = this;
    }
    
    @Subscribe
    public void onSetting(final ClientEvent event) {
        this.color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
    }
    
    @Override
    public void onLoad() {
        this.color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
    }
    
    @Override
    public final int getColor() {
        return this.color;
    }
}
