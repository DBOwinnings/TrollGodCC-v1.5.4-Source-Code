//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.util.math.*;
import java.util.*;

@ModuleManifest(label = "SelfTrap", category = Category.COMBAT, listen = false)
public class SelfTrap extends Module
{
    private final Setting<Integer> blocksPerTick;
    private final Setting<Integer> delay;
    private final Setting<Integer> disableTime;
    private final Setting<Boolean> disable;
    private final Timer offTimer;
    private final Timer timer;
    private final Map<BlockPos, Integer> retries;
    private int blocksThisTick;
    
    public SelfTrap() {
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("BlocksPerTick", (T)8, (T)1, (T)20));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)50, (T)0, (T)250));
        this.disableTime = (Setting<Integer>)this.register(new Setting("DisableTime", (T)200, (T)50, (T)300));
        this.disable = (Setting<Boolean>)this.register(new Setting("AutoDisable", (T)true));
        this.offTimer = new Timer();
        this.timer = new Timer();
        this.retries = new HashMap<BlockPos, Integer>();
        this.blocksThisTick = 0;
    }
    
    @Override
    public void onEnable() {
        if (this.isNull()) {
            this.disable();
        }
        this.offTimer.reset();
    }
    
    @Override
    public void onDisable() {
        this.retries.clear();
    }
    
    @Override
    public void onUpdate() {
        if (this.check()) {
            return;
        }
        for (final BlockPos position : this.getPositions()) {
            if (BlockUtil.isPositionPlaceable(position, false) != 3) {
                continue;
            }
            this.placeBlock(position);
        }
    }
    
    private List<BlockPos> getPositions() {
        final List<BlockPos> positions = new ArrayList<BlockPos>();
        positions.add(new BlockPos(this.mc.player.posX, this.mc.player.posY + 2.0, this.mc.player.posZ));
        final int placeability = BlockUtil.isPositionPlaceable(positions.get(0), false);
        switch (placeability) {
            case 0: {
                return new ArrayList<BlockPos>();
            }
            case 3: {
                return positions;
            }
            case 1: {
                if (BlockUtil.isPositionPlaceable(positions.get(0), false) == 3) {
                    return positions;
                }
            }
            case 2: {
                positions.add(new BlockPos(this.mc.player.posX + 1.0, this.mc.player.posY + 1.0, this.mc.player.posZ));
                positions.add(new BlockPos(this.mc.player.posX + 1.0, this.mc.player.posY + 2.0, this.mc.player.posZ));
                break;
            }
        }
        positions.sort(Comparator.comparingDouble(Vec3i::getY));
        return positions;
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.blocksThisTick < this.blocksPerTick.getValue()) {
            BlockUtil.placeBlock(pos);
            this.timer.reset();
            ++this.blocksThisTick;
        }
    }
    
    private boolean check() {
        if (this.isNull()) {
            this.disable();
            return true;
        }
        this.blocksThisTick = 0;
        if (this.disable.getValue() && this.offTimer.hasReached(this.disableTime.getValue())) {
            this.disable();
            return true;
        }
        return !this.timer.hasReached(this.delay.getValue());
    }
}
