/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import java.awt.Color;
import me.hollow.trollgod.api.mixin.mixins.render.AccessorEntityRenderer;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import me.hollow.trollgod.client.modules.client.Colours;
import net.minecraft.util.math.RayTraceResult;

@ModuleManifest(label="BlockHighlight", listen=false, category=Module.Category.VISUAL, color=-1070353)
public final class BlockHighlight
extends Module {
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(4.0f)));
    private final Setting<Boolean> sync = this.register(new Setting<Boolean>("Sync", true));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255, v -> this.sync.getValue() == false));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255, v -> this.sync.getValue() == false));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255, v -> this.sync.getValue() == false));

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
            RenderUtil.renderProperOutline(this.mc.objectMouseOver.getBlockPos(), this.sync.getValue() != false ? new Color(Colours.INSTANCE.getColor()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()), this.lineWidth.getValue().floatValue());
        }
    }
}

