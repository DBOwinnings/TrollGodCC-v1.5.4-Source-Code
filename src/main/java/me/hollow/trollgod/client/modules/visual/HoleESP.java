package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import java.util.*;
import me.hollow.trollgod.api.util.*;
import me.hollow.trollgod.client.events.*;
import tcb.bces.listener.*;
import java.awt.*;
import me.hollow.trollgod.api.util.render.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;

@ModuleManifest(label = "HoleESP", category = Category.VISUAL, color = 52224)
public class HoleESP extends Module
{
    private final Setting<Page> page;
    private final Setting<Float> range;
    private final Setting<Boolean> box;
    private final Setting<Boolean> outline;
    private final Setting<Boolean> flat;
    private final Setting<Boolean> wireframe;
    private final Setting<Integer> obsidianRed;
    private final Setting<Integer> obsidianGreen;
    private final Setting<Integer> obsidianBlue;
    private final Setting<Integer> obsidianAlpha;
    private final Setting<Integer> bedRockRed;
    private final Setting<Integer> bedRockGreen;
    private final Setting<Integer> bedRockBlue;
    private final Setting<Integer> bedRockAlpha;
    private List<BlockPos> holes;
    private final BlockPos[] surroundOffset;
    
    public HoleESP() {
        this.page = (Setting<Page>)this.register(new Setting("Page", (T)Page.MISC));
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)5.0f, (T)1.0f, (T)16.0f, v -> this.page.getValue() == Page.MISC));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (T)false, v -> this.page.getValue() == Page.MISC));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (T)false, v -> this.page.getValue() == Page.MISC));
        this.flat = (Setting<Boolean>)this.register(new Setting("Flat", (T)true, v -> this.page.getValue() == Page.MISC));
        this.wireframe = (Setting<Boolean>)this.register(new Setting("Wireframe", (T)true, v -> this.page.getValue() == Page.MISC && this.flat.getValue()));
        this.obsidianRed = (Setting<Integer>)this.register(new Setting("O-Red", (T)255, (T)0, (T)255, v -> this.page.getValue() == Page.COLOR));
        this.obsidianGreen = (Setting<Integer>)this.register(new Setting("O-Green", (T)0, (T)0, (T)255, v -> this.page.getValue() == Page.COLOR));
        this.obsidianBlue = (Setting<Integer>)this.register(new Setting("O-Blue", (T)0, (T)0, (T)255, v -> this.page.getValue() == Page.COLOR));
        this.obsidianAlpha = (Setting<Integer>)this.register(new Setting("O-Alpha", (T)40, (T)0, (T)255, v -> this.page.getValue() == Page.COLOR));
        this.bedRockRed = (Setting<Integer>)this.register(new Setting("B-Red", (T)0, (T)0, (T)255, v -> this.page.getValue() == Page.COLOR));
        this.bedRockGreen = (Setting<Integer>)this.register(new Setting("B-Green", (T)255, (T)0, (T)255, v -> this.page.getValue() == Page.COLOR));
        this.bedRockBlue = (Setting<Integer>)this.register(new Setting("B-Blue", (T)0, (T)0, (T)255, v -> this.page.getValue() == Page.COLOR));
        this.bedRockAlpha = (Setting<Integer>)this.register(new Setting("B-Alpha", (T)40, (T)0, (T)255, v -> this.page.getValue() == Page.COLOR));
        this.holes = new ArrayList<BlockPos>();
        this.surroundOffset = BlockUtil.toBlockPos(BlockUtil.holeOffsets);
    }
    
    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        if (this.isNull() || this.mc.player.ticksExisted % 2 == 0) {
            return;
        }
        this.holes = this.calcHoles();
    }
    
    @Override
    public void onRender3D() {
        for (int size = this.holes.size(), i = 0; i < size; ++i) {
            final BlockPos pos = this.holes.get(i);
            final Color color = this.isSafe(pos) ? new Color(this.bedRockRed.getValue(), this.bedRockGreen.getValue(), this.bedRockBlue.getValue()) : new Color(this.obsidianRed.getValue(), this.obsidianGreen.getValue(), this.obsidianBlue.getValue());
            RenderUtil.drawBoxESP(pos, color, 1.0f, this.outline.getValue(), this.box.getValue(), this.isSafe(pos) ? ((int)this.bedRockAlpha.getValue()) : ((int)this.obsidianAlpha.getValue()), ((boolean)this.flat.getValue()) ? 0.0f : 1.0f);
            if (this.wireframe.getValue()) {
                RenderUtil.renderCrosses(pos, color, 1.0f);
            }
        }
    }
    
    public List<BlockPos> calcHoles() {
        final List<BlockPos> safeSpots = new ArrayList<BlockPos>();
        final List<BlockPos> positions = BlockUtil.getSphere(this.range.getValue(), false);
        for (int size = positions.size(), i = 0; i < size; ++i) {
            final BlockPos pos = positions.get(i);
            if (this.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && this.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                if (this.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                    boolean isSafe = true;
                    for (final BlockPos offset : this.surroundOffset) {
                        final Block block = this.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
                        if (block != Blocks.BEDROCK) {
                            if (block != Blocks.OBSIDIAN) {
                                isSafe = false;
                            }
                        }
                    }
                    if (isSafe) {
                        safeSpots.add(pos);
                    }
                }
            }
        }
        return safeSpots;
    }
    
    private boolean isSafe(final BlockPos pos) {
        boolean isSafe = true;
        for (final BlockPos offset : this.surroundOffset) {
            if (this.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock() != Blocks.BEDROCK) {
                isSafe = false;
                break;
            }
        }
        return isSafe;
    }
    
    public enum Page
    {
        COLOR, 
        MISC;
    }
}
