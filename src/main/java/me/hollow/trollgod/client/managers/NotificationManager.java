package me.hollow.trollgod.client.managers;

import java.util.*;
import me.hollow.trollgod.client.notification.*;

public class NotificationManager
{
    private final ArrayList<Notification> notifications;
    
    public NotificationManager() {
        this.notifications = new ArrayList<Notification>();
    }
    
    public void handleNotifications(int posY) {
        for (int i = 0; i < this.getNotifications().size(); ++i) {
            this.getNotifications().get(i).drawNotification(posY);
            posY -= 22;
        }
    }
    
    public void addNotification(final String text) {
        this.getNotifications().add(new Notification(text));
    }
    
    public ArrayList<Notification> getNotifications() {
        return this.notifications;
    }
}
