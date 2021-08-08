package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import net.minecraft.util.math.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.block.material.*;
import java.util.*;
import java.awt.*;
import me.hollow.trollgod.api.util.render.*;

@ModuleManifest(label = "LavaESP", listen = false, category = Category.VISUAL)
public class LavaESP extends Module
{
    List<BlockPos> airBlockList;
    
    public LavaESP() {
        this.airBlockList = new ArrayList<BlockPos>();
    }
    
    @Override
    public void onUpdate() {
        int lavaCount = 0;
        int renderBlocks = 0;
        this.airBlockList.clear();
        final List<BlockPos> sortedSphere = BlockUtil.getSphere(8.0f, false);
        sortedSphere.sort(Comparator.comparingDouble(pos -> -this.mc.player.getDistanceSq(pos)));
        for (final BlockPos pos2 : BlockUtil.getSphere(8.0f, false)) {
            if (this.mc.world.getBlockState(pos2).getMaterial() == Material.LAVA) {
                ++lavaCount;
            }
            if (lavaCount < 10) {
                continue;
            }
            if (!BlockUtil.canPlaceCrystal(pos2, true)) {
                continue;
            }
            if (++renderBlocks > 8) {
                break;
            }
            this.airBlockList.add(pos2);
        }
    }
    
    @Override
    public void onRender3D() {
        for (final BlockPos pos : this.airBlockList) {
            RenderUtil.drawBoxESP(pos, new Color(255, 0, 0, 255), 1.0f, true, true, 70, 1.0f);
        }
    }
}
