package me.hollow.trollgod.client.gui.components.items.buttons;

import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.gui.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import net.minecraft.util.*;
import me.hollow.trollgod.client.events.*;

public class StringButton extends Button
{
    private final Setting setting;
    public boolean isListening;
    private CurrentString currentString;
    
    public StringButton(final Setting setting) {
        super(setting.getName());
        this.currentString = new CurrentString("");
        this.setting = setting;
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.0f, this.y + this.height - 0.5f, this.getColor(this.isHovering(mouseX, mouseY)));
        if (this.isListening) {
            TrollGod.fontManager.drawString(this.currentString.getString() + "_", this.x + 2.0f, this.y - 1.0f - TrollGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
        else {
            TrollGod.fontManager.drawString((this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? ("Prefix  " + ChatFormatting.GRAY) : "")) + this.setting.getValue(), this.x + 2.0f, this.y - 1.0f - TrollGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            StringButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (this.isListening) {
            switch (keyCode) {
                case 1: {
                    break;
                }
                case 28: {
                    this.enterString();
                    break;
                }
                case 14: {
                    this.setString(removeLastChar(this.currentString.getString()));
                    break;
                }
                default: {
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.setString(this.currentString.getString() + typedChar);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        }
        else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        super.onMouseClick();
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
    
    public void setString(final String newString) {
        TrollGod.INSTANCE.getBus().post(new ClientEvent(null));
        this.currentString = new CurrentString(newString);
    }
    
    public static String removeLastChar(final String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }
    
    public static class CurrentString
    {
        private final String string;
        
        public CurrentString(final String string) {
            this.string = string;
        }
        
        public String getString() {
            return this.string;
        }
    }
}
