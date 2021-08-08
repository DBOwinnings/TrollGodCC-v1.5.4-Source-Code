package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;

@ModuleManifest(label = "Step", listen = false, category = Category.MOVEMENT, color = -256)
public class Step extends Module
{
    private final Setting<Boolean> placeHolder;
    
    public Step() {
        this.placeHolder = (Setting<Boolean>)this.register(new Setting("PlaceHolder", (T)true));
    }
    
    @Override
    public void onEnable() {
        if (!this.placeHolder.getValue()) {
            this.mc.player.stepHeight = 2.0f;
        }
    }
    
    @Override
    public void onDisable() {
        if (!this.placeHolder.getValue()) {
            this.mc.player.stepHeight = 0.6f;
        }
    }
}
