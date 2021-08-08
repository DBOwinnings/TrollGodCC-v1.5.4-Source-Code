package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.mixin.mixins.render.*;
import net.minecraft.client.renderer.culling.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.renderer.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.modules.client.*;
import net.minecraft.enchantment.*;
import net.minecraft.util.text.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.nbt.*;
import net.minecraft.item.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.entity.*;
import net.minecraft.client.network.*;
import java.util.*;

@ModuleManifest(label = "Nametags", listen = false, category = Category.VISUAL, color = 16711782)
public final class Nametags extends Module
{
    private final Setting<Boolean> armor;
    private final Setting<Float> scaling;
    private final Setting<Boolean> ping;
    private final Setting<Boolean> totemPops;
    private final Setting<Boolean> rect;
    private final Setting<Boolean> rectBorder;
    private final Setting<Boolean> gradientBorder;
    private final Setting<Boolean> sneak;
    private final Setting<Boolean> scaleing;
    private final Setting<Float> factor;
    public static Nametags INSTANCE;
    private final ICamera camera;
    private final AccessorRenderManager renderManager;
    
    public Nametags() {
        this.armor = (Setting<Boolean>)this.register(new Setting("Armor", (T)true));
        this.scaling = (Setting<Float>)this.register(new Setting("Size", (T)0.3f, (T)0.1f, (T)20.0f));
        this.ping = (Setting<Boolean>)this.register(new Setting("Ping", (T)true));
        this.totemPops = (Setting<Boolean>)this.register(new Setting("TotemPops", (T)true));
        this.rect = (Setting<Boolean>)this.register(new Setting("Rectangle", (T)true));
        this.rectBorder = (Setting<Boolean>)this.register(new Setting("Border", (T)true, v -> this.rect.getValue()));
        this.gradientBorder = (Setting<Boolean>)this.register(new Setting("GradientBorder", (T)false, v -> this.rectBorder.getValue()));
        this.sneak = (Setting<Boolean>)this.register(new Setting("SneakColor", (T)false));
        this.scaleing = (Setting<Boolean>)this.register(new Setting("Scale", (T)false));
        this.factor = (Setting<Float>)this.register(new Setting("Factor", (T)0.3f, (T)0.1f, (T)1.0f, v -> this.scaleing.getValue()));
        this.camera = (ICamera)new Frustum();
        this.renderManager = (AccessorRenderManager)this.mc.getRenderManager();
        Nametags.INSTANCE = this;
    }
    
    @Override
    public final void onRender3D() {
        if (this.mc.getRenderViewEntity() == null) {
            return;
        }
        this.camera.setPosition(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY, this.mc.getRenderViewEntity().posZ);
        for (int size = this.mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = this.mc.world.playerEntities.get(i);
            if (this.camera.isBoundingBoxInFrustum(player.getEntityBoundingBox()) && player != this.mc.player && !player.isDead && player.getHealth() > 0.0f) {
                this.renderNameTag(player, this.interpolate(player.lastTickPosX, player.posX, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosX(), this.interpolate(player.lastTickPosY, player.posY, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosY(), this.interpolate(player.lastTickPosZ, player.posZ, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosZ(), this.mc.getRenderPartialTicks());
            }
        }
    }
    
    private void renderNameTag(final EntityPlayer player, final double x, final double y, final double z, final float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5 : 0.7);
        final Entity camera = this.mc.getRenderViewEntity();
        final double originalPositionX = camera.posX;
        final double originalPositionY = camera.posY;
        final double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        final String displayTag = this.getDisplayTag(player);
        final double distance = camera.getDistance(x + this.mc.getRenderManager().viewerPosX, y + this.mc.getRenderManager().viewerPosY, z + this.mc.getRenderManager().viewerPosZ);
        final int width = this.mc.fontRenderer.getStringWidth(displayTag) >> 1;
        double scale = (0.0018 + this.scaling.getValue() * (distance * this.factor.getValue())) / 1000.0;
        if (distance <= 8.0) {
            scale = 0.0245;
        }
        if (!this.scaleing.getValue()) {
            scale = this.scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x, (float)tempY + 1.4f, (float)z);
        GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(this.mc.getRenderManager().playerViewX, (this.mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        if (this.rect.getValue()) {
            if (this.gradientBorder.getValue()) {
                RenderUtil.drawGradientBorderedRect((float)(-width - 2), (float)(-(this.mc.fontRenderer.FONT_HEIGHT + 1)), width + 2.0f, 1.5f, 1426063360);
            }
            else {
                RenderUtil.drawBorderedRect((float)(-width - 2), (float)(-(this.mc.fontRenderer.FONT_HEIGHT + 1)), width + 2.0f, 1.5f, 1426063360, ((boolean)this.rectBorder.getValue()) ? (TrollGod.INSTANCE.getFriendManager().isFriend(player) ? -11157267 : Colours.INSTANCE.getColor()) : 0);
            }
        }
        if (this.armor.getValue()) {
            GlStateManager.pushMatrix();
            int xOffset = -8;
            for (int i = 0; i < 4; ++i) {
                xOffset -= 8;
            }
            xOffset -= 8;
            final ItemStack renderOffhand = player.getHeldItemOffhand().copy();
            this.renderItemStack(renderOffhand, xOffset);
            xOffset += 16;
            for (int j = 0; j < 4; ++j) {
                this.renderItemStack(((ItemStack)player.inventory.armorInventory.get(j)).copy(), xOffset);
                xOffset += 16;
            }
            this.renderItemStack(player.getHeldItemMainhand().copy(), xOffset);
            GlStateManager.popMatrix();
        }
        this.mc.fontRenderer.drawStringWithShadow(displayTag, (float)(-width), -8.0f, this.getDisplayColour(player));
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }
    
    private void renderItemStack(final ItemStack stack, final int x) {
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        this.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -26);
        this.mc.getRenderItem().renderItemOverlays(this.mc.fontRenderer, stack, x, -26);
        this.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        this.renderEnchantmentText(stack, x, -26);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
    }
    
    private void renderEnchantmentText(final ItemStack stack, final int x, final int y) {
        int enchantmentY = y - 8;
        final NBTTagList enchants = stack.getEnchantmentTagList();
        for (int index = 0; index < enchants.tagCount(); ++index) {
            final short id = enchants.getCompoundTagAt(index).getShort("id");
            final short level = enchants.getCompoundTagAt(index).getShort("lvl");
            final Enchantment enc = Enchantment.getEnchantmentByID((int)id);
            if (enc != null && !enc.getName().contains("fall")) {
                if (enc.getName().contains("all") || enc.getName().contains("explosion")) {
                    this.mc.fontRenderer.drawStringWithShadow(enc.isCurse() ? (TextFormatting.RED + enc.getTranslatedName((int)level).substring(11).substring(0, 1).toLowerCase()) : (enc.getTranslatedName((int)level).substring(0, 1).toLowerCase() + level), (float)(x * 2), (float)enchantmentY, -1);
                    enchantmentY -= 8;
                }
            }
        }
        if (hasDurability(stack)) {
            final int percent = getRoundedDamage(stack);
            String color;
            if (percent >= 60) {
                color = ChatFormatting.GREEN.toString();
            }
            else if (percent >= 25) {
                color = ChatFormatting.YELLOW.toString();
            }
            else {
                color = ChatFormatting.RED.toString();
            }
            this.mc.fontRenderer.drawStringWithShadow(color + percent + "%", (float)(x << 1), (float)enchantmentY, -1);
        }
    }
    
    public static int getItemDamage(final ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }
    
    public static float getDamageInPercent(final ItemStack stack) {
        return getItemDamage(stack) / (float)stack.getMaxDamage() * 100.0f;
    }
    
    public static int getRoundedDamage(final ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }
    
    public static boolean hasDurability(final ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }
    
    private String getDisplayTag(final EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        final float health = EntityUtil.getHealth((EntityLivingBase)player);
        String color;
        if (health > 18.0f) {
            color = ChatFormatting.GREEN.toString();
        }
        else if (health > 16.0f) {
            color = ChatFormatting.DARK_GREEN.toString();
        }
        else if (health > 12.0f) {
            color = ChatFormatting.YELLOW.toString();
        }
        else if (health > 8.0f) {
            color = ChatFormatting.GOLD.toString();
        }
        else if (health > 5.0f) {
            color = ChatFormatting.RED.toString();
        }
        else {
            color = ChatFormatting.DARK_RED.toString();
        }
        String pingStr = "";
        if (this.ping.getValue()) {
            try {
                final int responseTime = Objects.requireNonNull(this.mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr = pingStr + responseTime + "ms";
            }
            catch (Exception ignored) {
                pingStr += "-1ms";
            }
        }
        String popStr = "";
        if (this.totemPops.getValue()) {
            final Map<String, Integer> registry = TrollGod.INSTANCE.getPopManager().getPopMap();
            popStr += (registry.containsKey(player.getName()) ? (" -" + registry.get(player.getName())) : "");
        }
        name = name + color + " " + (int)health;
        return name + " " + ChatFormatting.RESET + pingStr + popStr;
    }
    
    private int getDisplayColour(final EntityPlayer player) {
        if (TrollGod.INSTANCE.getFriendManager().isFriend(player)) {
            return -11157267;
        }
        if (player.isSneaking() && this.sneak.getValue()) {
            return 16757248;
        }
        return -1;
    }
    
    private double interpolate(final double previous, final double current, final float delta) {
        return previous + (current - previous) * delta;
    }
}
