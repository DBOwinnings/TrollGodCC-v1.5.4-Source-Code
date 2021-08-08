package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.*;
import net.minecraft.client.entity.*;
import java.util.*;
import com.mojang.authlib.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;

@ModuleManifest(label = "FakePlayer", listen = false, category = Category.MISC, color = -16711936)
public class FakePlayer extends Module
{
    private EntityOtherPlayerMP entityOtherPlayerMP;
    
    public FakePlayer() {
        this.entityOtherPlayerMP = null;
    }
    
    @Override
    public void onEnable() {
        if (this.mc.player == null) {
            return;
        }
        (this.entityOtherPlayerMP = new EntityOtherPlayerMP((World)this.mc.world, new GameProfile(UUID.fromString("cc72ff00-a113-48f4-be18-2dda8db52355"), "is pig"))).copyLocationAndAnglesFrom((Entity)this.mc.player);
        this.entityOtherPlayerMP.inventory = this.mc.player.inventory;
        this.mc.world.spawnEntity((Entity)this.entityOtherPlayerMP);
    }
    
    @Override
    public void onDisable() {
        if (this.entityOtherPlayerMP != null) {
            this.mc.world.removeEntity((Entity)this.entityOtherPlayerMP);
        }
    }
}
