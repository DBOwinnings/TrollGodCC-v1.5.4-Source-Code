package me.hollow.trollgod.client.gui.components.items.buttons;

import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;

public class BooleanButton extends Button
{
    private final Setting setting;
    
    public BooleanButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.0f, this.y + this.height - 0.5f, this.getColor(this.isHovering(mouseX, mouseY)));
        TrollGod.fontManager.drawString(this.getName(), this.x + 2.0f, this.y - 1.0f - TrollGui.getClickGui().getTextOffset(), this.getState() ? this.getColor() : -1);
    }
    
    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            BooleanButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        this.setting.setValue(!this.setting.getValue());
    }
    
    @Override
    public boolean getState() {
        return this.setting.getValue();
    }
}
