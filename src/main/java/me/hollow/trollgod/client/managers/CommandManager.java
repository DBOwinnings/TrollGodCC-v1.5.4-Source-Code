package me.hollow.trollgod.client.managers;

import me.hollow.trollgod.client.command.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.client.*;
import me.hollow.trollgod.client.modules.client.*;
import org.apache.commons.lang3.*;
import me.hollow.trollgod.api.util.*;
import tcb.bces.listener.*;
import me.hollow.trollgod.client.command.commands.*;
import me.hollow.trollgod.*;
import java.util.*;

public class CommandManager implements IListener
{
    private final List<Command> commands;
    
    public CommandManager() {
        this.commands = new ArrayList<Command>();
    }
    
    @Subscribe
    public void onSendPacket(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            final String message = ((CPacketChatMessage)event.getPacket()).getMessage();
            if (StringUtils.startsWith((CharSequence)message, (CharSequence)ClickGui.getInstance().prefix.getValue())) {
                final String[] args = message.split(" ");
                final String input = message.split(" ")[0].substring(1);
                for (final Command command : this.commands) {
                    if (input.equalsIgnoreCase(command.getLabel()) || this.checkAliases(input, command)) {
                        event.setCancelled();
                        command.execute(args);
                    }
                }
                if (!event.isCancelled()) {
                    MessageUtil.sendClientMessage("Command " + message + " was not found!", true);
                    event.setCancelled();
                }
                event.setCancelled();
            }
        }
    }
    
    private boolean checkAliases(final String input, final Command command) {
        for (final String str : command.getAliases()) {
            if (input.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
    
    public void init() {
        this.register(new ToggleCommand(), new BindCommand(), new DrawnCommand(), new FriendCommand(), new SaveCommand(), new TutorialCommand());
        TrollGod.INSTANCE.getBus().register(this);
    }
    
    public void register(final Command... command) {
        Collections.addAll(this.commands, command);
    }
    
    @Override
    public boolean isListening() {
        return true;
    }
}
