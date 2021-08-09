/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.api.util.font.RainbowText;
import me.hollow.trollgod.client.events.ClientEvent;
import me.hollow.trollgod.client.events.UpdateEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import me.hollow.trollgod.client.modules.client.Colours;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="HUD", category=Module.Category.CLIENT, color=39423)
public final class HUD
extends Module {
    public static Setting<Boolean> rainbowSetting = new Setting<Boolean>("Rainbow", true);
    public static Setting<Integer> speed = new Setting<Integer>("Speed", 5000, 1000, 15000);
    public static Setting<Integer> saturation = new Setting<Integer>("Saturation", 200, 0, 255);
    public static Setting<Integer> brightness = new Setting<Integer>("Brightness", 255, 0, 255);
    public static Setting<Integer> factor = new Setting<Integer>("Hue", 5, 0, 40);
    private final Setting<Boolean> arrayList = this.register(new Setting<Boolean>("Arraylist", true));
    private final Setting<SortMode> sortMode = this.register(new Setting<SortMode>("Sorting", SortMode.LENGTH));
    private final Setting<Boolean> renderWatermark = this.register(new Setting<Boolean>("Watermark", true));
    private final Setting<Boolean> offsetWatermark = this.register(new Setting<Object>("OffsetWatermark", false, v -> this.renderWatermark.getValue()));
    private final Setting<Boolean> customWatermark = this.register(new Setting<Object>("Custom", false, v -> this.renderWatermark.getValue()));
    private final Setting<String> watermarkString = this.register(new Setting<Object>("CustomMark", "StresserLegend.CC v573.5", v -> this.customWatermark.getValue()));
    private final Setting<Boolean> armorHud = this.register(new Setting<Boolean>("Armor", true));
    private final Setting<Boolean> renderArmor = this.register(new Setting<Boolean>("Render Armor", true, v -> this.armorHud.getValue()));
    private final Setting<Boolean> tps = this.register(new Setting<Boolean>("TPS", true));
    private final Setting<Boolean> coords = this.register(new Setting<Boolean>("Coordinates", true));
    private final Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", true));
    private final Setting<Boolean> direction = this.register(new Setting<Boolean>("Direction", true));
    private final Setting<Boolean> welcomer = this.register(new Setting<Boolean>("Welcomer", true));
    private final Setting<Boolean> kmh = this.register(new Setting<Boolean>("KMH", true));
    private final Setting<Boolean> fps = this.register(new Setting<Boolean>("FPS", true));
    private List<Module> modules;
    public static HUD INSTANCE;
    private int size = 0;

    public HUD() {
        INSTANCE = this;
        this.addSetting(rainbowSetting, saturation, brightness, speed, factor);
    }

    @Override
    public void onLoad() {
        this.modules = new ArrayList<Module>(TrollGod.INSTANCE.getModuleManager().getModules());
        this.size = this.modules.size();
    }

    @Subscribe
    public void onSettingChange(ClientEvent event) {
        if (event.getSetting() == this.sortMode) {
            this.modules.sort(Comparator.comparing(Module::getLabel));
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (this.sortMode.getValue() == SortMode.LENGTH) {
            if (this.mc.player.ticksExisted % 2 != 0) {
                return;
            }
            this.modules.sort(Comparator.comparingDouble(mod -> -this.getStringWidth(mod.getLabel() + mod.getSuffix())));
        }
    }

    public void drawString(String text, float x, float y, int color) {
        if (rainbowSetting.getValue().booleanValue()) {
            RainbowText.drawStringWithShadow(text, x, y, HUD.rainbow(x, y), false);
        } else {
            TrollGod.fontManager.drawString(text, x, y, color);
        }
    }

    private static int rainbow(double x, double y) {
        double scale = 0.01568627450980392;
        double d = 0.7071067811865483;
        double pos = (x * d + y * d) * -scale + (double)(System.currentTimeMillis() % (long)speed.getValue().intValue()) * 2.0 * -Math.PI / (double)speed.getValue().intValue();
        return 0xFF000000 | MathHelper.clamp((int)MathHelper.floor((double)(255.0 * (0.5 + Math.sin(0.0 + pos)))), (int)0, (int)255) << 16 | MathHelper.clamp((int)MathHelper.floor((double)(255.0 * (0.5 + Math.sin(2.0943951023931953 + pos)))), (int)0, (int)255) << 8 | MathHelper.clamp((int)MathHelper.floor((double)(255.0 * (0.5 + Math.sin(4.1887902047863905 + pos)))), (int)0, (int)255);
    }

    public float getStringWidth(String text) {
        if (rainbowSetting.getValue().booleanValue()) {
            return RainbowText.rainbow.getStringWidth(text);
        }
        return TrollGod.fontManager.getStringWidth(text);
    }

    public final void onRender2D() {
        int daFunnies;
        if (!this.isEnabled()) {
            return;
        }
        ScaledResolution resolution = new ScaledResolution(this.mc);
        if (this.renderWatermark.getValue().booleanValue()) {
            this.drawString(this.customWatermark.getValue() != false ? this.watermarkString.getValue() : "TrollGod.CC \u00a77v1.5.2", 2.0f, this.offsetWatermark.getValue() != false ? 10.0f : 2.0f, Colours.INSTANCE.getColor());
        }
        if (this.armorHud.getValue().booleanValue()) {
            GlStateManager.enableTexture2D();
            int i = resolution.getScaledWidth() >> 1;
            int y = resolution.getScaledHeight() - 55 - (this.mc.player.isInWater() && this.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            for (int j = 0; j < 4; ++j) {
                ItemStack is = (ItemStack)this.mc.player.inventory.armorInventory.get(j);
                if (is.isEmpty()) continue;
                int x = i - 90 + (9 - j - 1) * 20 + 2;
                if (this.renderArmor.getValue().booleanValue()) {
                    GlStateManager.enableDepth();
                    this.mc.getRenderItem().zLevel = 200.0f;
                    this.mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
                    this.mc.getRenderItem().renderItemOverlayIntoGUI(this.mc.fontRenderer, is, x, y + (this.renderArmor.getValue() != false ? 0 : 10), "");
                    this.mc.getRenderItem().zLevel = 0.0f;
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                }
                int dmg = (int)ItemUtil.getDamageInPercent(is);
                TrollGod.fontManager.drawString(dmg + "", x + 8 - (this.mc.fontRenderer.getStringWidth(dmg + "") >> 1), y + (this.renderArmor.getValue() != false ? -8 : 6), is.getItem().getRGBDurabilityForDisplay(is));
            }
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
        if (this.coords.getValue().booleanValue()) {
            this.drawString("XYZ \u00a7f" + (int)this.mc.player.posX + ", " + (int)this.mc.player.posY + ", " + (int)this.mc.player.posZ, 2.0f, resolution.getScaledHeight() - (this.mc.ingameGUI.getChatGUI().getChatOpen() ? 22 : 10), Colours.INSTANCE.getColor());
        }
        if (this.welcomer.getValue().booleanValue()) {
            String welcomerString = "Welcome, " + this.mc.player.getName();
            this.drawString(welcomerString, (float)resolution.getScaledWidth() / 2.0f - this.getStringWidth(welcomerString) / 2.0f, 2.0f, Colours.INSTANCE.getColor());
        }
        boolean inChat = this.mc.ingameGUI.getChatGUI().getChatOpen();
        if (this.direction.getValue().booleanValue()) {
            String facing = "";
            switch (this.mc.getRenderViewEntity().getHorizontalFacing()) {
                case NORTH: {
                    facing = "North \u00a78[\u00a7r-Z\u00a78]";
                    break;
                }
                case SOUTH: {
                    facing = "South \u00a78[\u00a7r+Z\u00a78]";
                    break;
                }
                case WEST: {
                    facing = "West \u00a78[\u00a7r-X\u00a78]";
                    break;
                }
                case EAST: {
                    facing = "East \u00a78[\u00a7r+X\u00a78]";
                }
            }
            this.drawString(facing, 2.0f, resolution.getScaledHeight() - (inChat ? 24 : 10) - (this.coords.getValue() != false ? 10 : 0), Colours.INSTANCE.getColor());
        }
        int n = daFunnies = inChat ? 14 : 0;
        if (this.kmh.getValue().booleanValue()) {
            String kmhString = "Speed \u00a7f" + TrollGod.INSTANCE.getSpeedManager().getKpH() + "km/h";
            this.drawString(kmhString, (float)resolution.getScaledWidth() - this.getStringWidth(kmhString) - 2.0f, resolution.getScaledHeight() - (inChat ? 24 : 10), Colours.INSTANCE.getColor());
            daFunnies += 10;
        }
        if (this.tps.getValue().booleanValue()) {
            String tpsString = "TPS \u00a7f" + String.format("%.1f", Float.valueOf(TrollGod.INSTANCE.getTpsManager().getTPS()));
            this.drawString(tpsString, (float)resolution.getScaledWidth() - this.getStringWidth(tpsString) - 2.0f, resolution.getScaledHeight() - 10 - daFunnies, Colours.INSTANCE.getColor());
            daFunnies += 10;
        }
        if (this.fps.getValue().booleanValue()) {
            String fpsString = "FPS \u00a7f" + Minecraft.getDebugFPS();
            this.drawString(fpsString, (float)resolution.getScaledWidth() - this.getStringWidth(fpsString) - 2.0f, resolution.getScaledHeight() - 10 - daFunnies, Colours.INSTANCE.getColor());
            daFunnies += 10;
        }
        if (this.ping.getValue().booleanValue()) {
            String pingString = "Ping \u00a7f" + TrollGod.INSTANCE.getTpsManager().getPing();
            this.drawString(pingString, (float)resolution.getScaledWidth() - this.getStringWidth(pingString) - 2.0f, resolution.getScaledHeight() - 10 - daFunnies, Colours.INSTANCE.getColor());
        }
        if (this.arrayList.getValue().booleanValue()) {
            int offset = -6;
            for (int i = 0; i < this.size; ++i) {
                Module module = this.modules.get(i);
                if (!module.isEnabled() || module.isHidden()) continue;
                String fullLabel = module.getLabel() + module.getSuffix();
                this.drawString(fullLabel, (float)resolution.getScaledWidth() - this.getStringWidth(fullLabel) - 2.0f, offset += 10, module.getColor());
            }
        }
    }

    public static enum SortMode {
        ALPHABETICAL,
        LENGTH;

    }
}

