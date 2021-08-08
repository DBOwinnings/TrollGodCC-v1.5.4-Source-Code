package me.hollow.trollgod.client.notification;

import me.hollow.trollgod.api.util.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.api.util.render.*;

public class Notification
{
    private String message;
    private Timer timer;
    Minecraft mc;
    ScaledResolution scaledRes;
    
    public Notification(final String message) {
        this.timer = new Timer();
        this.mc = Minecraft.getMinecraft();
        this.scaledRes = new ScaledResolution(this.mc);
        this.message = message;
        this.timer.reset();
    }
    
    public void drawNotification(final int y) {
        if (this.timer.hasReached(2000L)) {
            TrollGod.INSTANCE.getNotificationManager().getNotifications().remove(this);
        }
        RenderUtil.drawBorderedRect((float)(this.scaledRes.getScaledWidth() - 10 - TrollGod.fontManager.getStringWidth(this.message)), (float)y, (float)(this.scaledRes.getScaledWidth() - 4), (float)(y + 14), 1965171234, -15658735);
        TrollGod.fontManager.drawString(this.message, (float)(this.scaledRes.getScaledWidth() - TrollGod.fontManager.getStringWidth(this.message) - 6), (float)(y + 2), 16777215);
    }
}
