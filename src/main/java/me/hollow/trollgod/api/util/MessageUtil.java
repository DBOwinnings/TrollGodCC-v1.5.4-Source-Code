package me.hollow.trollgod.api.util;

import me.hollow.trollgod.api.interfaces.*;
import net.minecraft.util.text.*;

public class MessageUtil implements Minecraftable
{
    private static final String prefix = "[§5TrollGod§r] §d";
    
    public static void sendClientMessage(final String string, final boolean deleteOld) {
        if (MessageUtil.mc.player == null) {
            return;
        }
        final ITextComponent component = (ITextComponent)new TextComponentString("[§5TrollGod§r] §d" + string);
        if (deleteOld) {
            MessageUtil.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, -727);
        }
        else {
            MessageUtil.mc.ingameGUI.getChatGUI().printChatMessage(component);
        }
    }
    
    public static void sendClientMessage(final String string, final int id) {
        if (MessageUtil.mc.player == null) {
            return;
        }
        final ITextComponent component = (ITextComponent)new TextComponentString("[§5TrollGod§r] §d" + string);
        MessageUtil.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, id);
    }
}
