package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import java.awt.*;
import me.hollow.trollgod.client.events.*;
import me.hollow.trollgod.client.gui.*;
import net.minecraft.client.settings.*;
import tcb.bces.listener.*;
import net.minecraft.client.gui.*;

@ModuleManifest(label = "ClickGui", category = Category.CLIENT, key = 157)
public class ClickGui extends Module
{
    public final Setting<String> prefix;
    public final Setting<Boolean> customFov;
    public final Setting<Float> fov;
    public final Setting<Integer> red;
    public final Setting<Integer> green;
    public final Setting<Integer> blue;
    public final Setting<Integer> hoverAlpha;
    public final Setting<Integer> enabledAlpha;
    public final Setting<Integer> categoryRed;
    public final Setting<Integer> categoryGreen;
    public final Setting<Integer> categoryBlue;
    public final Setting<Integer> categoryAlpha;
    public final Setting<Integer> alpha;
    private static ClickGui INSTANCE;
    
    public ClickGui() {
        this.prefix = (Setting<String>)this.register(new Setting("Prefix", (T)"."));
        this.customFov = (Setting<Boolean>)this.register(new Setting("Custom Fov", (T)false));
        this.fov = (Setting<Float>)this.register(new Setting("Fov", (T)150.0f, (T)(-180.0f), (T)180.0f));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255));
        this.hoverAlpha = (Setting<Integer>)this.register(new Setting("Hover Alpha", (T)60, (T)0, (T)255));
        this.enabledAlpha = (Setting<Integer>)this.register(new Setting("Enabled Alpha", (T)60, (T)0, (T)255));
        this.categoryRed = (Setting<Integer>)this.register(new Setting("Category Red", (T)255, (T)0, (T)255));
        this.categoryGreen = (Setting<Integer>)this.register(new Setting("Category Green", (T)255, (T)0, (T)255));
        this.categoryBlue = (Setting<Integer>)this.register(new Setting("Category Blue", (T)255, (T)0, (T)255));
        this.categoryAlpha = (Setting<Integer>)this.register(new Setting("Category Alpha", (T)40, (T)0, (T)255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)40, (T)0, (T)255));
        ClickGui.INSTANCE = this;
    }
    
    public static ClickGui getInstance() {
        return ClickGui.INSTANCE;
    }
    
    public final int getColor(final boolean hover) {
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), hover ? this.hoverAlpha.getValue() : ((int)this.enabledAlpha.getValue())).getRGB();
    }
    
    @Subscribe
    public void onTick(final UpdateEvent event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (!(this.mc.currentScreen instanceof TrollGui)) {
            this.setEnabled(false);
        }
        if (this.customFov.getValue()) {
            this.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, (float)this.fov.getValue());
        }
    }
    
    @Override
    public void onEnable() {
        if (this.mc.player != null) {
            this.mc.displayGuiScreen((GuiScreen)new TrollGui());
        }
    }
    
    @Override
    public void onDisable() {
        if (this.mc.currentScreen instanceof TrollGui) {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }
}
