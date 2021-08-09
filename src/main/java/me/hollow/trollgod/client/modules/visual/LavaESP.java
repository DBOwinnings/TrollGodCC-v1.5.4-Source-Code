/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.hollow.trollgod.api.util.BlockUtil;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;

@ModuleManifest(label="LavaESP", listen=false, category=Module.Category.VISUAL)
public class LavaESP
extends Module {
    List<BlockPos> airBlockList = new ArrayList<BlockPos>();

    @Override
    public void onUpdate() {
        int lavaCount = 0;
        int renderBlocks = 0;
        this.airBlockList.clear();
        List<BlockPos> sortedSphere = BlockUtil.getSphere(8.0f, false);
        sortedSphere.sort(Comparator.comparingDouble(pos -> -this.mc.player.getDistanceSq(pos)));
        for (BlockPos pos2 : BlockUtil.getSphere(8.0f, false)) {
            if (this.mc.world.getBlockState(pos2).getMaterial() == Material.LAVA) {
                ++lavaCount;
            }
            if (lavaCount < 10 || !BlockUtil.canPlaceCrystal(pos2, true)) continue;
            if (++renderBlocks > 8) break;
            this.airBlockList.add(pos2);
        }
    }

    @Override
    public void onRender3D() {
        for (BlockPos pos : this.airBlockList) {
            RenderUtil.drawBoxESP(pos, new Color(255, 0, 0, 255), 1.0f, true, true, 70, 1.0f);
        }
    }
}

