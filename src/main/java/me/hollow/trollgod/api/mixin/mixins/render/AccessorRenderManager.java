/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.render;

import net.minecraft.client.renderer.entity.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RenderManager.class})
public interface AccessorRenderManager {
    @Accessor(value="renderPosX")
    public double getRenderPosX();

    @Accessor(value="renderPosY")
    public double getRenderPosY();

    @Accessor(value="renderPosZ")
    public double getRenderPosZ();
}

