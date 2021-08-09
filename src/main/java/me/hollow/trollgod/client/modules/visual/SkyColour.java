/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label="SkyColour", listen=false, category=Module.Category.VISUAL, color=26214)
public class SkyColour
extends Module {
    private final Setting<Float> red = this.register(new Setting<Float>("Red", Float.valueOf(255.0f), Float.valueOf(0.0f), Float.valueOf(255.0f)));
    private final Setting<Float> green = this.register(new Setting<Float>("Green", Float.valueOf(255.0f), Float.valueOf(0.0f), Float.valueOf(255.0f)));
    private final Setting<Float> blue = this.register(new Setting<Float>("Blue", Float.valueOf(255.0f), Float.valueOf(0.0f), Float.valueOf(255.0f)));

    @SubscribeEvent
    public void setFogColors(EntityViewRenderEvent.FogColors event) {
        event.setRed(this.red.getValue().floatValue() / 255.0f);
        event.setGreen(this.green.getValue().floatValue() / 255.0f);
        event.setBlue(this.blue.getValue().floatValue() / 255.0f);
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

