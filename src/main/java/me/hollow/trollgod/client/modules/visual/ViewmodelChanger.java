/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;

@ModuleManifest(label="ModelChanger", listen=false, category=Module.Category.VISUAL, color=-16558593)
public class ViewmodelChanger
extends Module {
    public final Setting<Boolean> pauseOnEat = this.register(new Setting<Boolean>("Pause", true));
    public final Setting<Float> translateX = this.register(new Setting<Float>("X", Float.valueOf(0.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    public final Setting<Float> translateY = this.register(new Setting<Float>("Y", Float.valueOf(0.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    public final Setting<Float> translateZ = this.register(new Setting<Float>("Z", Float.valueOf(0.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    public final Setting<Float> rotateX = this.register(new Setting<Float>("Rotate X", Float.valueOf(0.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    public final Setting<Float> rotateY = this.register(new Setting<Float>("Rotate Y", Float.valueOf(0.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    public final Setting<Float> rotateZ = this.register(new Setting<Float>("Rotate Z", Float.valueOf(0.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    public final Setting<Float> scale = this.register(new Setting<Float>("Scale", Float.valueOf(10.0f), Float.valueOf(9.0f), Float.valueOf(10.0f)));
    public static ViewmodelChanger INSTANCE;

    public ViewmodelChanger() {
        INSTANCE = this;
    }
}

