package me.hollow.trollgod.api.mixin.mixins.render;

import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.model.*;
import net.minecraft.inventory.*;
import me.hollow.trollgod.client.modules.visual.*;
import org.spongepowered.asm.mixin.*;

@Mixin({ LayerBipedArmor.class })
public abstract class MixinLayerBipedArmor
{
    @Shadow
    protected abstract void setModelVisible(final ModelBiped p0);
    
    @Overwrite
    protected void setModelSlotVisible(final ModelBiped p_188359_1_, final EntityEquipmentSlot slotIn) {
        this.setModelVisible(p_188359_1_);
        switch (slotIn) {
            case HEAD: {
                p_188359_1_.bipedHead.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.helmet.getValue());
                p_188359_1_.bipedHeadwear.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.helmet.getValue());
                break;
            }
            case CHEST: {
                p_188359_1_.bipedBody.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.chestplate.getValue());
                p_188359_1_.bipedRightArm.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.chestplate.getValue());
                p_188359_1_.bipedLeftArm.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.chestplate.getValue());
                break;
            }
            case LEGS: {
                p_188359_1_.bipedBody.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.thighHighs.getValue());
                p_188359_1_.bipedRightLeg.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.thighHighs.getValue());
                p_188359_1_.bipedLeftLeg.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.thighHighs.getValue());
                break;
            }
            case FEET: {
                p_188359_1_.bipedRightLeg.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.boots.getValue());
                p_188359_1_.bipedLeftLeg.showModel = (!NoArmorRender.INSTANCE.isEnabled() || NoArmorRender.INSTANCE.boots.getValue());
                break;
            }
        }
    }
}
