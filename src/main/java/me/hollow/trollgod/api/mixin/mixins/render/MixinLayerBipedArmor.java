/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.render;

import me.hollow.trollgod.client.modules.visual.NoArmorRender;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={LayerBipedArmor.class})
public abstract class MixinLayerBipedArmor {
    @Shadow
    protected abstract void setModelVisible(ModelBiped var1);

    @Overwrite
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn) {
        this.setModelVisible(p_188359_1_);
        switch (slotIn) {
            case HEAD: {
                p_188359_1_.bipedHead.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.helmet.getValue() != false;
                p_188359_1_.bipedHeadwear.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.helmet.getValue() != false;
                break;
            }
            case CHEST: {
                p_188359_1_.bipedBody.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.chestplate.getValue() != false;
                p_188359_1_.bipedRightArm.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.chestplate.getValue() != false;
                p_188359_1_.bipedLeftArm.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.chestplate.getValue() != false;
                break;
            }
            case LEGS: {
                p_188359_1_.bipedBody.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.thighHighs.getValue() != false;
                p_188359_1_.bipedRightLeg.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.thighHighs.getValue() != false;
                p_188359_1_.bipedLeftLeg.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.thighHighs.getValue() != false;
                break;
            }
            case FEET: {
                p_188359_1_.bipedRightLeg.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.boots.getValue() != false;
                p_188359_1_.bipedLeftLeg.showModel = !NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.boots.getValue() != false;
            }
        }
    }
}

