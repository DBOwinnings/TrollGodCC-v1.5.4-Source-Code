/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.events.ClientEvent;
import me.hollow.trollgod.client.gui.TrollGui;
import me.hollow.trollgod.client.gui.components.items.buttons.Button;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.SoundEvent;

public class StringButton
extends Button {
    private final Setting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.0f, this.y + (float)this.height - 0.5f, this.getColor(this.isHovering(mouseX, mouseY)));
        if (this.isListening) {
            TrollGod.fontManager.drawString(this.currentString.getString() + "_", this.x + 2.0f, this.y - 1.0f - (float)TrollGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        } else {
            TrollGod.fontManager.drawString((this.setting.getName().equals("Buttons") ? "Buttons " : (this.setting.getName().equals("Prefix") ? "Prefix  " + ChatFormatting.GRAY : "")) + this.setting.getValue(), this.x + 2.0f, this.y - 1.0f - (float)TrollGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
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
                    this.setString(StringButton.removeLastChar(this.currentString.getString()));
                    break;
                }
                default: {
                    if (!ChatAllowedCharacters.isAllowedCharacter((char)typedChar)) break;
                    this.setString(this.currentString.getString() + typedChar);
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
        } else {
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

    public void setString(String newString) {
        TrollGod.INSTANCE.getBus().post(new ClientEvent(null));
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}

