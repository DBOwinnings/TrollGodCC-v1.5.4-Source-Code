package me.hollow.trollgod.client.managers;

import me.hollow.trollgod.api.interfaces.*;
import java.text.*;
import me.hollow.trollgod.*;
import net.minecraft.network.play.server.*;
import com.google.common.base.*;
import me.hollow.trollgod.client.events.*;
import java.util.*;
import net.minecraft.entity.player.*;
import tcb.bces.listener.*;

public class TPSManager implements Minecraftable, IListener
{
    public static float TPS;
    public static long lastUpdate;
    public static float[] tpsCounts;
    public static DecimalFormat format;
    
    public static void update() {
        final long currentTime = System.currentTimeMillis();
        if (TPSManager.lastUpdate == -1L) {
            TPSManager.lastUpdate = currentTime;
            return;
        }
        final long timeDiff = currentTime - TPSManager.lastUpdate;
        float tickTime = (float)(timeDiff / 20L);
        if (tickTime == 0.0f) {
            tickTime = 50.0f;
        }
        float tps = 1000.0f / tickTime;
        if (tps > 20.0f) {
            tps = 20.0f;
        }
        System.arraycopy(TPSManager.tpsCounts, 0, TPSManager.tpsCounts, 1, TPSManager.tpsCounts.length - 1);
        TPSManager.tpsCounts[0] = tps;
        double total = 0.0;
        for (final float f : TPSManager.tpsCounts) {
            total += f;
        }
        total /= TPSManager.tpsCounts.length;
        if (total > 20.0) {
            total = 20.0;
        }
        TPSManager.TPS = Float.parseFloat(TPSManager.format.format(total));
        TPSManager.lastUpdate = currentTime;
    }
    
    public void reset() {
        Arrays.fill(TPSManager.tpsCounts, 20.0f);
        TPSManager.TPS = 20.0f;
    }
    
    public void init() {
        TrollGod.INSTANCE.getBus().register(this);
    }
    
    @Subscribe
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            update();
        }
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            if (TPSManager.mc.world == null || TPSManager.mc.player == null) {
                return;
            }
            final SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals((Object)packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals((Object)packet.getAction())) {
                return;
            }
            for (final SPacketPlayerListItem.AddPlayerData data : packet.getEntries()) {
                if (data != null && (!Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null)) {
                    final UUID id = data.getProfile().getId();
                    switch (packet.getAction()) {
                        case ADD_PLAYER: {
                            final String name = data.getProfile().getName();
                            TrollGod.INSTANCE.getBus().post(new ConnectionEvent(0, id, name));
                            continue;
                        }
                        case REMOVE_PLAYER: {
                            final EntityPlayer entity = TPSManager.mc.world.getPlayerEntityByUUID(id);
                            if (entity != null) {
                                final String logoutName = entity.getName();
                                TrollGod.INSTANCE.getBus().post(new ConnectionEvent(1, entity, id, logoutName));
                                continue;
                            }
                            TrollGod.INSTANCE.getBus().post(new ConnectionEvent(2, id, null));
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    public final float getTpsFactor() {
        return 20.0f / TPSManager.TPS;
    }
    
    public final float getTPS() {
        return TPSManager.TPS;
    }
    
    public int getPing() {
        if (TPSManager.mc.player == null || TPSManager.mc.world == null || TPSManager.mc.getConnection() == null || TPSManager.mc.getConnection().getPlayerInfo(TPSManager.mc.getConnection().getGameProfile().getId()) == null) {
            return -1;
        }
        return TPSManager.mc.getConnection().getPlayerInfo(TPSManager.mc.getConnection().getGameProfile().getId()).getResponseTime();
    }
    
    @Override
    public boolean isListening() {
        return true;
    }
    
    static {
        TPSManager.TPS = 20.0f;
        TPSManager.lastUpdate = -1L;
        TPSManager.tpsCounts = new float[10];
        TPSManager.format = new DecimalFormat("##.0#");
    }
}
