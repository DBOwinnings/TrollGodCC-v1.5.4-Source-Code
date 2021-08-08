package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;

@ModuleManifest(label = "ModelChanger", listen = false, category = Category.VISUAL, color = -16558593)
public class ViewmodelChanger extends Module
{
    public final Setting<Boolean> pauseOnEat;
    public final Setting<Float> translateX;
    public final Setting<Float> translateY;
    public final Setting<Float> translateZ;
    public final Setting<Float> rotateX;
    public final Setting<Float> rotateY;
    public final Setting<Float> rotateZ;
    public final Setting<Float> scale;
    public static ViewmodelChanger INSTANCE;
    
    public ViewmodelChanger() {
        this.pauseOnEat = (Setting<Boolean>)this.register(new Setting("Pause", (T)true));
        this.translateX = (Setting<Float>)this.register(new Setting("X", (T)0.0f, (T)(-5.0f), (T)5.0f));
        this.translateY = (Setting<Float>)this.register(new Setting("Y", (T)0.0f, (T)(-5.0f), (T)5.0f));
        this.translateZ = (Setting<Float>)this.register(new Setting("Z", (T)0.0f, (T)(-5.0f), (T)5.0f));
        this.rotateX = (Setting<Float>)this.register(new Setting("Rotate X", (T)0.0f, (T)(-5.0f), (T)5.0f));
        this.rotateY = (Setting<Float>)this.register(new Setting("Rotate Y", (T)0.0f, (T)(-5.0f), (T)5.0f));
        this.rotateZ = (Setting<Float>)this.register(new Setting("Rotate Z", (T)0.0f, (T)(-5.0f), (T)5.0f));
        this.scale = (Setting<Float>)this.register(new Setting("Scale", (T)10.0f, (T)9.0f, (T)10.0f));
        ViewmodelChanger.INSTANCE = this;
    }
}
