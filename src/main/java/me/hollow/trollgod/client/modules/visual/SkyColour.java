package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.common.*;

@ModuleManifest(label = "SkyColour", listen = false, category = Category.VISUAL, color = 26214)
public class SkyColour extends Module
{
    private final Setting<Float> red;
    private final Setting<Float> green;
    private final Setting<Float> blue;
    
    public SkyColour() {
        this.red = (Setting<Float>)this.register(new Setting("Red", (T)255.0f, (T)0.0f, (T)255.0f));
        this.green = (Setting<Float>)this.register(new Setting("Green", (T)255.0f, (T)0.0f, (T)255.0f));
        this.blue = (Setting<Float>)this.register(new Setting("Blue", (T)255.0f, (T)0.0f, (T)255.0f));
    }
    
    @SubscribeEvent
    public void setFogColors(final EntityViewRenderEvent.FogColors event) {
        event.setRed(this.red.getValue() / 255.0f);
        event.setGreen(this.green.getValue() / 255.0f);
        event.setBlue(this.blue.getValue() / 255.0f);
    }
    
    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
    
    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
}
