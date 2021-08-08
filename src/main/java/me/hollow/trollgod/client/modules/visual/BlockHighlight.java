package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.mixin.mixins.render.*;
import net.minecraft.util.math.*;
import me.hollow.trollgod.client.modules.client.*;
import java.awt.*;
import me.hollow.trollgod.api.util.render.*;

@ModuleManifest(label = "BlockHighlight", listen = false, category = Category.VISUAL, color = -1070353)
public final class BlockHighlight extends Module
{
    private final Setting<Float> lineWidth;
    private final Setting<Boolean> sync;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    
    public BlockHighlight() {
        this.lineWidth = (Setting<Float>)this.register(new Setting("Width", (T)1.0f, (T)0.1f, (T)4.0f));
        this.sync = (Setting<Boolean>)this.register(new Setting("Sync", (T)true));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255, v -> !this.sync.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> !this.sync.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255, v -> !this.sync.getValue()));
    }
    
    @Override
    public void onEnable() {
        ((AccessorEntityRenderer)this.mc.entityRenderer).setDrawBlockOutline(false);
    }
    
    @Override
    public void onDisable() {
        ((AccessorEntityRenderer)this.mc.entityRenderer).setDrawBlockOutline(true);
    }
    
    @Override
    public void onRender3D() {
        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            RenderUtil.renderProperOutline(this.mc.objectMouseOver.getBlockPos(), ((boolean)this.sync.getValue()) ? new Color(Colours.INSTANCE.getColor()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()), this.lineWidth.getValue());
        }
    }
}
