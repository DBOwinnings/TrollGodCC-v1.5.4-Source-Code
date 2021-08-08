package me.hollow.trollgod.client.gui.components.items.buttons;

import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import com.mojang.realmsclient.gui.*;
import me.hollow.trollgod.client.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;

public class UnlimitedSlider extends Button
{
    public final Setting setting;
    
    public UnlimitedSlider(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.0f, this.y + this.height - 0.5f, this.getColor(this.isHovering(mouseX, mouseY)));
        TrollGod.fontManager.drawString(" - " + this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue() + ChatFormatting.RESET + " +", this.x + 2.0f, this.y - 1.0f - TrollGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            UnlimitedSlider.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.isRight(mouseX)) {
                if (this.setting.getValue() instanceof Double) {
                    this.setting.setValue(this.setting.getValue() + 1.0);
                }
                else if (this.setting.getValue() instanceof Float) {
                    this.setting.setValue(this.setting.getValue() + 1.0f);
                }
                else if (this.setting.getValue() instanceof Integer) {
                    this.setting.setValue(this.setting.getValue() + 1);
                }
            }
            else if (this.setting.getValue() instanceof Double) {
                this.setting.setValue(this.setting.getValue() - 1.0);
            }
            else if (this.setting.getValue() instanceof Float) {
                this.setting.setValue(this.setting.getValue() - 1.0f);
            }
            else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue(this.setting.getValue() - 1);
            }
        }
    }
    
    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
    }
    
    @Override
    public boolean getState() {
        return true;
    }
    
    public boolean isRight(final int x) {
        return x > this.x + (this.width + 7.4f) / 2.0f;
    }
}
