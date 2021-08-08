package me.hollow.trollgod.client.modules;

import tcb.bces.listener.*;
import net.minecraft.client.*;
import me.hollow.trollgod.api.property.*;
import java.lang.annotation.*;
import me.hollow.trollgod.*;
import java.util.*;
import me.hollow.trollgod.client.gui.*;
import com.mojang.realmsclient.gui.*;
import me.hollow.trollgod.api.util.*;

public class Module implements IListener
{
    protected final Minecraft mc;
    private final List<Setting> settings;
    public final Setting<Boolean> drawn;
    public final Setting<Bind> bind;
    private String label;
    private String suffix;
    private Category category;
    private boolean persistent;
    private boolean enabled;
    private int color;
    
    public Module() {
        this.mc = Minecraft.getMinecraft();
        this.settings = new ArrayList<Setting>();
        this.drawn = (Setting<Boolean>)this.register(new Setting("Drawn", (T)true));
        this.bind = (Setting<Bind>)this.register(new Setting("Bind", (T)new Bind(-10000)));
        this.suffix = "";
        if (this.getClass().isAnnotationPresent(ModuleManifest.class)) {
            final ModuleManifest moduleManifest = this.getClass().getAnnotation(ModuleManifest.class);
            this.label = moduleManifest.label();
            this.category = moduleManifest.category();
            this.bind.setValue(new Bind(moduleManifest.key()));
            this.drawn.setValue(moduleManifest.drawn());
            this.persistent = moduleManifest.persistent();
            if (this.persistent) {
                this.enabled = true;
            }
            this.color = moduleManifest.color();
            if (moduleManifest.listen()) {
                TrollGod.INSTANCE.getBus().register(this);
            }
        }
    }
    
    public void addSetting(final Setting... setting) {
        this.settings.addAll(Arrays.asList((Setting[])setting));
    }
    
    public final Setting register(final Setting setting) {
        this.settings.add(setting);
        if (this.mc.currentScreen instanceof TrollGui) {
            TrollGui.getInstance().updateModule(this);
        }
        return setting;
    }
    
    public final List<Setting> getSettings() {
        return this.settings;
    }
    
    public final void setEnabled(final boolean enabled) {
        if (this.persistent) {
            this.enabled = true;
            return;
        }
        this.enabled = enabled;
        this.onToggle();
        if (enabled) {
            this.onEnable();
            MessageUtil.sendClientMessage(ChatFormatting.DARK_AQUA + this.getLabel() + "§d was §2enabled", -44444);
        }
        else {
            this.onDisable();
            MessageUtil.sendClientMessage(ChatFormatting.DARK_AQUA + this.getLabel() + "§d was §4disabled", -44444);
        }
    }
    
    public void setDrawn(final boolean drawn) {
        this.drawn.setValue(drawn);
    }
    
    public void toggle() {
        this.setEnabled(!this.enabled);
    }
    
    public void disable() {
        this.setEnabled(false);
    }
    
    public void onRender3D() {
    }
    
    public void onUpdate() {
    }
    
    public void onToggle() {
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public void onLoad() {
    }
    
    public void onDisconnect() {
    }
    
    public final boolean isNull() {
        return this.mc.player == null || this.mc.world == null;
    }
    
    public final int getKey() {
        return this.bind.getValue().getKey();
    }
    
    public final boolean isEnabled() {
        return this.enabled;
    }
    
    public final boolean isHidden() {
        return !this.drawn.getValue();
    }
    
    public final boolean isPersistent() {
        return this.persistent;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public final Category getCategory() {
        return this.category;
    }
    
    public final String getLabel() {
        return this.label;
    }
    
    public final void clearSuffix() {
        this.suffix = "";
    }
    
    public final void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
    
    public final String getSuffix() {
        if (this.suffix.length() == 0) {
            return "";
        }
        return " §8[§f" + this.suffix + "§8]";
    }
    
    @Override
    public final boolean isListening() {
        return this.enabled && this.mc.player != null;
    }
    
    public enum Category
    {
        COMBAT, 
        MOVEMENT, 
        PLAYER, 
        CLIENT, 
        VISUAL, 
        MISC;
    }
}
