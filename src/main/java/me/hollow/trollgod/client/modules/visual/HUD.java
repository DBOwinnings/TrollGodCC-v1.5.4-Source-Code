package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.*;
import java.util.*;
import java.util.function.*;
import tcb.bces.listener.*;
import me.hollow.trollgod.client.events.*;
import me.hollow.trollgod.api.util.font.*;
import net.minecraft.util.math.*;
import net.minecraft.client.gui.*;
import me.hollow.trollgod.client.modules.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.client.*;

@ModuleManifest(label = "HUD", category = Category.CLIENT, color = 39423)
public final class HUD extends Module
{
    public static Setting<Boolean> rainbowSetting;
    public static Setting<Integer> speed;
    public static Setting<Integer> saturation;
    public static Setting<Integer> brightness;
    public static Setting<Integer> factor;
    private final Setting<Boolean> arrayList;
    private final Setting<SortMode> sortMode;
    private final Setting<Boolean> renderWatermark;
    private final Setting<Boolean> offsetWatermark;
    private final Setting<Boolean> customWatermark;
    private final Setting<String> watermarkString;
    private final Setting<Boolean> armorHud;
    private final Setting<Boolean> renderArmor;
    private final Setting<Boolean> tps;
    private final Setting<Boolean> coords;
    private final Setting<Boolean> ping;
    private final Setting<Boolean> direction;
    private final Setting<Boolean> welcomer;
    private final Setting<Boolean> kmh;
    private final Setting<Boolean> fps;
    private List<Module> modules;
    public static HUD INSTANCE;
    private int size;
    
    public HUD() {
        this.arrayList = (Setting<Boolean>)this.register(new Setting("Arraylist", (T)true));
        this.sortMode = (Setting<SortMode>)this.register(new Setting("Sorting", (T)SortMode.LENGTH));
        this.renderWatermark = (Setting<Boolean>)this.register(new Setting("Watermark", (T)true));
        this.offsetWatermark = (Setting<Boolean>)this.register(new Setting("OffsetWatermark", (T)false, v -> this.renderWatermark.getValue()));
        this.customWatermark = (Setting<Boolean>)this.register(new Setting("Custom", (T)false, v -> this.renderWatermark.getValue()));
        this.watermarkString = (Setting<String>)this.register(new Setting("CustomMark", (T)"StresserLegend.CC v573.5", v -> this.customWatermark.getValue()));
        this.armorHud = (Setting<Boolean>)this.register(new Setting("Armor", (T)true));
        this.renderArmor = (Setting<Boolean>)this.register(new Setting("Render Armor", (T)true, v -> this.armorHud.getValue()));
        this.tps = (Setting<Boolean>)this.register(new Setting("TPS", (T)true));
        this.coords = (Setting<Boolean>)this.register(new Setting("Coordinates", (T)true));
        this.ping = (Setting<Boolean>)this.register(new Setting("Ping", (T)true));
        this.direction = (Setting<Boolean>)this.register(new Setting("Direction", (T)true));
        this.welcomer = (Setting<Boolean>)this.register(new Setting("Welcomer", (T)true));
        this.kmh = (Setting<Boolean>)this.register(new Setting("KMH", (T)true));
        this.fps = (Setting<Boolean>)this.register(new Setting("FPS", (T)true));
        this.size = 0;
        (HUD.INSTANCE = this).addSetting(HUD.rainbowSetting, HUD.saturation, HUD.brightness, HUD.speed, HUD.factor);
    }
    
    @Override
    public void onLoad() {
        this.modules = new ArrayList<Module>(TrollGod.INSTANCE.getModuleManager().getModules());
        this.size = this.modules.size();
    }
    
    @Subscribe
    public void onSettingChange(final ClientEvent event) {
        if (event.getSetting() == this.sortMode) {
            this.modules.sort(Comparator.comparing((Function<? super Module, ? extends Comparable>)Module::getLabel));
        }
    }
    
    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        if (this.sortMode.getValue() == SortMode.LENGTH) {
            if (this.mc.player.ticksExisted % 2 != 0) {
                return;
            }
            this.modules.sort(Comparator.comparingDouble(mod -> -this.getStringWidth(mod.getLabel() + mod.getSuffix())));
        }
    }
    
    public void drawString(final String text, final float x, final float y, final int color) {
        if (HUD.rainbowSetting.getValue()) {
            RainbowText.drawStringWithShadow(text, x, y, rainbow(x, y), false);
        }
        else {
            TrollGod.fontManager.drawString(text, x, y, color);
        }
    }
    
    private static int rainbow(final double x, final double y) {
        final double scale = 0.01568627450980392;
        final double d = 0.7071067811865483;
        final double pos = (x * d + y * d) * -scale + System.currentTimeMillis() % HUD.speed.getValue() * 2.0 * -3.141592653589793 / HUD.speed.getValue();
        return 0xFF000000 | MathHelper.clamp(MathHelper.floor(255.0 * (0.5 + Math.sin(0.0 + pos))), 0, 255) << 16 | MathHelper.clamp(MathHelper.floor(255.0 * (0.5 + Math.sin(2.0943951023931953 + pos))), 0, 255) << 8 | MathHelper.clamp(MathHelper.floor(255.0 * (0.5 + Math.sin(4.1887902047863905 + pos))), 0, 255);
    }
    
    public float getStringWidth(final String text) {
        if (HUD.rainbowSetting.getValue()) {
            return (float)RainbowText.rainbow.getStringWidth(text);
        }
        return (float)TrollGod.fontManager.getStringWidth(text);
    }
    
    public final void onRender2D() {
        if (!this.isEnabled()) {
            return;
        }
        final ScaledResolution resolution = new ScaledResolution(this.mc);
        if (this.renderWatermark.getValue()) {
            this.drawString(((boolean)this.customWatermark.getValue()) ? this.watermarkString.getValue() : "TrollGod.CC §7v1.5.2", 2.0f, ((boolean)this.offsetWatermark.getValue()) ? 10.0f : 2.0f, Colours.INSTANCE.getColor());
        }
        if (this.armorHud.getValue()) {
            GlStateManager.enableTexture2D();
            final int i = resolution.getScaledWidth() >> 1;
            final int y = resolution.getScaledHeight() - 55 - ((this.mc.player.isInWater() && this.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
            for (int j = 0; j < 4; ++j) {
                final ItemStack is = (ItemStack)this.mc.player.inventory.armorInventory.get(j);
                if (!is.isEmpty()) {
                    final int x = i - 90 + (9 - j - 1) * 20 + 2;
                    if (this.renderArmor.getValue()) {
                        GlStateManager.enableDepth();
                        this.mc.getRenderItem().zLevel = 200.0f;
                        this.mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
                        this.mc.getRenderItem().renderItemOverlayIntoGUI(this.mc.fontRenderer, is, x, y + (this.renderArmor.getValue() ? 0 : 10), "");
                        this.mc.getRenderItem().zLevel = 0.0f;
                        GlStateManager.enableTexture2D();
                        GlStateManager.disableLighting();
                        GlStateManager.disableDepth();
                    }
                    final int dmg = (int)ItemUtil.getDamageInPercent(is);
                    TrollGod.fontManager.drawString(dmg + "", (float)(x + 8 - (this.mc.fontRenderer.getStringWidth(dmg + "") >> 1)), (float)(y + (this.renderArmor.getValue() ? -8 : 6)), is.getItem().getRGBDurabilityForDisplay(is));
                }
            }
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
        if (this.coords.getValue()) {
            this.drawString("XYZ §f" + (int)this.mc.player.posX + ", " + (int)this.mc.player.posY + ", " + (int)this.mc.player.posZ, 2.0f, (float)(resolution.getScaledHeight() - (this.mc.ingameGUI.getChatGUI().getChatOpen() ? 22 : 10)), Colours.INSTANCE.getColor());
        }
        if (this.welcomer.getValue()) {
            final String welcomerString = "Welcome, " + this.mc.player.getName();
            this.drawString(welcomerString, resolution.getScaledWidth() / 2.0f - this.getStringWidth(welcomerString) / 2.0f, 2.0f, Colours.INSTANCE.getColor());
        }
        final boolean inChat = this.mc.ingameGUI.getChatGUI().getChatOpen();
        if (this.direction.getValue()) {
            String facing = "";
            switch (this.mc.getRenderViewEntity().getHorizontalFacing()) {
                case NORTH: {
                    facing = "North §8[§r-Z§8]";
                    break;
                }
                case SOUTH: {
                    facing = "South §8[§r+Z§8]";
                    break;
                }
                case WEST: {
                    facing = "West §8[§r-X§8]";
                    break;
                }
                case EAST: {
                    facing = "East §8[§r+X§8]";
                    break;
                }
            }
            this.drawString(facing, 2.0f, (float)(resolution.getScaledHeight() - (inChat ? 24 : 10) - (this.coords.getValue() ? 10 : 0)), Colours.INSTANCE.getColor());
        }
        int daFunnies = inChat ? 14 : 0;
        if (this.kmh.getValue()) {
            final String kmhString = "Speed §f" + TrollGod.INSTANCE.getSpeedManager().getKpH() + "km/h";
            this.drawString(kmhString, resolution.getScaledWidth() - this.getStringWidth(kmhString) - 2.0f, (float)(resolution.getScaledHeight() - (inChat ? 24 : 10)), Colours.INSTANCE.getColor());
            daFunnies += 10;
        }
        if (this.tps.getValue()) {
            final String tpsString = "TPS §f" + String.format("%.1f", TrollGod.INSTANCE.getTpsManager().getTPS());
            this.drawString(tpsString, resolution.getScaledWidth() - this.getStringWidth(tpsString) - 2.0f, (float)(resolution.getScaledHeight() - 10 - daFunnies), Colours.INSTANCE.getColor());
            daFunnies += 10;
        }
        if (this.fps.getValue()) {
            final String fpsString = "FPS §f" + Minecraft.getDebugFPS();
            this.drawString(fpsString, resolution.getScaledWidth() - this.getStringWidth(fpsString) - 2.0f, (float)(resolution.getScaledHeight() - 10 - daFunnies), Colours.INSTANCE.getColor());
            daFunnies += 10;
        }
        if (this.ping.getValue()) {
            final String pingString = "Ping §f" + TrollGod.INSTANCE.getTpsManager().getPing();
            this.drawString(pingString, resolution.getScaledWidth() - this.getStringWidth(pingString) - 2.0f, (float)(resolution.getScaledHeight() - 10 - daFunnies), Colours.INSTANCE.getColor());
        }
        if (this.arrayList.getValue()) {
            int offset = -6;
            for (int k = 0; k < this.size; ++k) {
                final Module module = this.modules.get(k);
                if (module.isEnabled() && !module.isHidden()) {
                    final String string;
                    final String fullLabel = string = module.getLabel() + module.getSuffix();
                    final float x2 = resolution.getScaledWidth() - this.getStringWidth(fullLabel) - 2.0f;
                    offset += 10;
                    this.drawString(string, x2, (float)offset, module.getColor());
                }
            }
        }
    }
    
    static {
        HUD.rainbowSetting = new Setting<Boolean>("Rainbow", true);
        HUD.speed = new Setting<Integer>("Speed", 5000, 1000, 15000);
        HUD.saturation = new Setting<Integer>("Saturation", 200, 0, 255);
        HUD.brightness = new Setting<Integer>("Brightness", 255, 0, 255);
        HUD.factor = new Setting<Integer>("Hue", 5, 0, 40);
    }
    
    public enum SortMode
    {
        ALPHABETICAL, 
        LENGTH;
    }
}
