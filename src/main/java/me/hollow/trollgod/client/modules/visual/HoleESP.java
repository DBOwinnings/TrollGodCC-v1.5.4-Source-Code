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
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="HoleESP", category=Module.Category.VISUAL, color=52224)
public class HoleESP
extends Module {
    private final Setting<Page> page = this.register(new Setting<Page>("Page", Page.MISC));
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(16.0f), v -> this.page.getValue() == Page.MISC));
    private final Setting<Boolean> box = this.register(new Setting<Boolean>("Box", false, v -> this.page.getValue() == Page.MISC));
    private final Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", false, v -> this.page.getValue() == Page.MISC));
    private final Setting<Boolean> flat = this.register(new Setting<Boolean>("Flat", true, v -> this.page.getValue() == Page.MISC));
    private final Setting<Boolean> wireframe = this.register(new Setting<Boolean>("Wireframe", true, v -> this.page.getValue() == Page.MISC && this.flat.getValue() != false));
    private final Setting<Integer> obsidianRed = this.register(new Setting<Integer>("O-Red", 255, 0, 255, v -> this.page.getValue() == Page.COLOR));
    private final Setting<Integer> obsidianGreen = this.register(new Setting<Integer>("O-Green", 0, 0, 255, v -> this.page.getValue() == Page.COLOR));
    private final Setting<Integer> obsidianBlue = this.register(new Setting<Integer>("O-Blue", 0, 0, 255, v -> this.page.getValue() == Page.COLOR));
    private final Setting<Integer> obsidianAlpha = this.register(new Setting<Integer>("O-Alpha", 40, 0, 255, v -> this.page.getValue() == Page.COLOR));
    private final Setting<Integer> bedRockRed = this.register(new Setting<Integer>("B-Red", 0, 0, 255, v -> this.page.getValue() == Page.COLOR));
    private final Setting<Integer> bedRockGreen = this.register(new Setting<Integer>("B-Green", 255, 0, 255, v -> this.page.getValue() == Page.COLOR));
    private final Setting<Integer> bedRockBlue = this.register(new Setting<Integer>("B-Blue", 0, 0, 255, v -> this.page.getValue() == Page.COLOR));
    private final Setting<Integer> bedRockAlpha = this.register(new Setting<Integer>("B-Alpha", 40, 0, 255, v -> this.page.getValue() == Page.COLOR));
    private List<BlockPos> holes = new ArrayList<BlockPos>();
    private final BlockPos[] surroundOffset = BlockUtil.toBlockPos(BlockUtil.holeOffsets);

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (this.isNull() || this.mc.player.ticksExisted % 2 == 0) {
            return;
        }
        this.holes = this.calcHoles();
    }

    @Override
    public void onRender3D() {
        int size = this.holes.size();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = this.holes.get(i);
            Color color = this.isSafe(pos) ? new Color(this.bedRockRed.getValue(), this.bedRockGreen.getValue(), this.bedRockBlue.getValue()) : new Color(this.obsidianRed.getValue(), this.obsidianGreen.getValue(), this.obsidianBlue.getValue());
            RenderUtil.drawBoxESP(pos, color, 1.0f, this.outline.getValue(), this.box.getValue(), this.isSafe(pos) ? this.bedRockAlpha.getValue().intValue() : this.obsidianAlpha.getValue().intValue(), this.flat.getValue() != false ? 0.0f : 1.0f);
            if (!this.wireframe.getValue().booleanValue()) continue;
            RenderUtil.renderCrosses(pos, color, 1.0f);
        }
    }

    public List<BlockPos> calcHoles() {
        ArrayList<BlockPos> safeSpots = new ArrayList<BlockPos>();
        List<BlockPos> positions = BlockUtil.getSphere(this.range.getValue().floatValue(), false);
        int size = positions.size();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = positions.get(i);
            if (!this.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !this.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !this.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) continue;
            boolean isSafe = true;
            for (BlockPos offset : this.surroundOffset) {
                Block block = this.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
                if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) continue;
                isSafe = false;
            }
            if (!isSafe) continue;
            safeSpots.add(pos);
        }
        return safeSpots;
    }

    private boolean isSafe(BlockPos pos) {
        boolean isSafe = true;
        for (BlockPos offset : this.surroundOffset) {
            if (this.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock() == Blocks.BEDROCK) continue;
            isSafe = false;
            break;
        }
        return isSafe;
    }

    public static enum Page {
        COLOR,
        MISC;

    }
}

