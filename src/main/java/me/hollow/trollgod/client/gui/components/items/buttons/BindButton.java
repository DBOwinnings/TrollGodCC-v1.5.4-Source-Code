package me.hollow.trollgod.client.gui.components.items.buttons;

import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.gui.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import me.hollow.trollgod.api.property.*;

public class BindButton extends Button
{
    private final Setting setting;
    public boolean isListening;
    
    public BindButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.0f, this.y + this.height - 0.5f, this.getColor(this.isHovering(mouseX, mouseY)));
        if (this.isListening) {
            TrollGod.fontManager.drawString("Listening..", this.x + 2.0f, this.y - 1.0f - TrollGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
        else {
            TrollGod.fontManager.drawString(this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue().toString(), this.x + 2.3f, this.y - 1.7f - TrollGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }
    
    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            BindButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.getKey() == 211) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            super.onMouseClick();
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }
    
    @Override
    public boolean getState() {
        return !this.isListening;
    }
}
