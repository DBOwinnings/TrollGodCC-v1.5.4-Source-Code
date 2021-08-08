package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.util.math.*;
import java.util.*;
import me.hollow.trollgod.client.events.*;
import tcb.bces.listener.*;
import java.awt.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.init.*;

@ModuleManifest(label = "VoidESP", category = Category.VISUAL, color = -5602)
public class VoidESP extends Module
{
    private final Setting<Float> range;
    private final Setting<Boolean> down;
    private List<BlockPos> holes;
    
    public VoidESP() {
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)6.0f, (T)3.0f, (T)16.0f));
        this.down = (Setting<Boolean>)this.register(new Setting("Up", (T)false));
        this.holes = new ArrayList<BlockPos>();
    }
    
    @Subscribe
    public void onTick(final UpdateEvent event) {
        if (this.isNull()) {
            return;
        }
        this.holes = this.calcHoles();
    }
    
    @Override
    public void onRender3D() {
        for (int size = this.holes.size(), i = 0; i < size; ++i) {
            final BlockPos pos = this.holes.get(i);
            RenderUtil.renderCrosses(((boolean)this.down.getValue()) ? pos.up() : pos, new Color(255, 255, 255), 2.0f);
        }
    }
    
    public List<BlockPos> calcHoles() {
        final List<BlockPos> voidHoles = new ArrayList<BlockPos>();
        final List<BlockPos> positions = BlockUtil.getSphere(this.range.getValue(), false);
        for (int size = positions.size(), i = 0; i < size; ++i) {
            final BlockPos pos = positions.get(i);
            if (pos.getY() == 0 && this.mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                voidHoles.add(pos);
            }
        }
        return voidHoles;
    }
}
