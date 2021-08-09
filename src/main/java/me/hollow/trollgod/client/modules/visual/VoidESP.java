/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.BlockUtil;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.events.UpdateEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="VoidESP", category=Module.Category.VISUAL, color=-5602)
public class VoidESP
extends Module {
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(3.0f), Float.valueOf(16.0f)));
    private final Setting<Boolean> down = this.register(new Setting<Boolean>("Up", false));
    private List<BlockPos> holes = new ArrayList<BlockPos>();

    @Subscribe
    public void onTick(UpdateEvent event) {
        if (this.isNull()) {
            return;
        }
        this.holes = this.calcHoles();
    }

    @Override
    public void onRender3D() {
        int size = this.holes.size();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = this.holes.get(i);
            RenderUtil.renderCrosses(this.down.getValue() != false ? pos.up() : pos, new Color(255, 255, 255), 2.0f);
        }
    }

    public List<BlockPos> calcHoles() {
        ArrayList<BlockPos> voidHoles = new ArrayList<BlockPos>();
        List<BlockPos> positions = BlockUtil.getSphere(this.range.getValue().floatValue(), false);
        int size = positions.size();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = positions.get(i);
            if (pos.getY() != 0 || this.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) continue;
            voidHoles.add(pos);
        }
        return voidHoles;
    }
}

