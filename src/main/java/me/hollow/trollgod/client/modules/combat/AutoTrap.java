//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import com.mojang.realmsclient.gui.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.entity.*;

@ModuleManifest(label = "AutoTrap", listen = false, category = Category.COMBAT, color = 14204927)
public final class AutoTrap extends Module
{
    private Setting<Type> type;
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerPlace;
    private final Setting<Double> targetRange;
    private final Setting<Double> range;
    private final Setting<Boolean> antiSelf;
    private final Setting<Boolean> retry;
    private final Setting<Boolean> feet;
    private final Setting<Integer> retryer;
    private final Map<BlockPos, Integer> retries;
    private final Timer retryTimer;
    private final Timer timer;
    private boolean didPlace;
    private int lastHotbarSlot;
    private int placements;
    public EntityPlayer target;
    public static boolean placing;
    
    public AutoTrap() {
        this.type = (Setting<Type>)this.register(new Setting("Type", (T)Type.PRESERVE));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", (T)50, (T)0, (T)250));
        this.blocksPerPlace = (Setting<Integer>)this.register(new Setting("Block/Place", (T)8, (T)1, (T)30));
        this.targetRange = (Setting<Double>)this.register(new Setting("Target Range", (T)10.0, (T)0.0, (T)20.0));
        this.range = (Setting<Double>)this.register(new Setting("Place Range", (T)6.0, (T)0.0, (T)10.0));
        this.antiSelf = (Setting<Boolean>)this.register(new Setting("Anti Self", (T)false));
        this.retry = (Setting<Boolean>)this.register(new Setting("Retry", (T)false));
        this.feet = (Setting<Boolean>)this.register(new Setting("Feet", (T)true));
        this.retryer = (Setting<Integer>)this.register(new Setting("Retries", (T)4, (T)1, (T)15, v -> this.retry.getValue()));
        this.retries = new HashMap<BlockPos, Integer>();
        this.retryTimer = new Timer();
        this.timer = new Timer();
        this.didPlace = false;
        this.placements = 0;
    }
    
    @Override
    public void onEnable() {
        if (this.isNull()) {
            this.disable();
            return;
        }
        this.retries.clear();
    }
    
    @Override
    public void onDisable() {
        AutoTrap.placing = false;
    }
    
    @Override
    public void onUpdate() {
        if (this.check()) {
            return;
        }
        if (this.type.getValue() == Type.PRESERVE) {
            this.doPreserve(this.target);
        }
        else {
            this.doNormal(BlockUtil.targets(this.target.getPositionVector(), this.feet.getValue()));
        }
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private List<BlockPos> getPreserve(final EntityPlayer player) {
        final List<BlockPos> positions = new ArrayList<BlockPos>();
        positions.add(new BlockPos(player.posX, player.posY + 2.0, player.posZ));
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
                positions.add(new BlockPos(player.posX + 1.0, player.posY + 1.0, player.posZ));
                positions.add(new BlockPos(player.posX + 1.0, player.posY + 2.0, player.posZ));
                break;
            }
        }
        positions.sort(Comparator.comparingDouble(Vec3i::getY));
        return positions;
    }
    
    private void doPreserve(final EntityPlayer player) {
        for (final BlockPos position : this.getPreserve(player)) {
            if (BlockUtil.isPositionPlaceable(position, false) != 3) {
                continue;
            }
            this.placeBlock(position);
        }
    }
    
    private void doNormal(final List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(this.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), this.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        this.lastHotbarSlot = this.mc.player.inventory.currentItem;
        final int obbySlot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(obbySlot));
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Vec3d vec3d3 = list.get(i);
            final BlockPos position = new BlockPos(vec3d3);
            final int placeability = BlockUtil.isPositionPlaceable(position, true);
            if (this.retry.getValue() && placeability == 1 && (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue())) {
                this.placeBlock(position);
                this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                this.retryTimer.reset();
            }
            else if (placeability == 3) {
                this.placeBlock(position);
            }
        }
        this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.lastHotbarSlot));
    }
    
    private boolean check() {
        if (this.isNull()) {
            return true;
        }
        this.didPlace = false;
        this.placements = 0;
        AutoTrap.placing = false;
        final int obbySlot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (!this.isEnabled()) {
            return true;
        }
        if (this.retryTimer.hasReached(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (obbySlot == -1) {
            MessageUtil.sendClientMessage(ChatFormatting.RED + "<AutoTrap> No obsidian, toggling.", -3232);
            this.disable();
            return true;
        }
        this.lastHotbarSlot = this.mc.player.inventory.currentItem;
        this.target = this.getTarget(this.targetRange.getValue());
        return this.target == null || !this.timer.hasReached(this.delay.getValue());
    }
    
    private EntityPlayer getTarget(final double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (int size = this.mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = this.mc.world.playerEntities.get(i);
            if (!EntityUtil.isntValid(player, range)) {
                if (!BlockUtil.getRoundedBlockPos((Entity)this.mc.player).equals((Object)BlockUtil.getRoundedBlockPos((Entity)player)) || !this.antiSelf.getValue()) {
                    if (target == null) {
                        target = player;
                        distance = this.mc.player.getDistanceSq((Entity)player);
                    }
                    else if (this.mc.player.getDistanceSq((Entity)player) < distance) {
                        target = player;
                        distance = this.mc.player.getDistanceSq((Entity)player);
                    }
                }
            }
        }
        return target;
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getValue() && this.mc.player.getDistanceSq(pos) <= this.range.getValue() * this.range.getValue()) {
            AutoTrap.placing = true;
            BlockUtil.placeBlock(pos);
            this.didPlace = true;
            ++this.placements;
        }
    }
    
    public enum Type
    {
        PRESERVE, 
        NORMAL;
    }
}
