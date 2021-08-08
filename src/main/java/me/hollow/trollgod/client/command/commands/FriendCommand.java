package me.hollow.trollgod.client.command.commands;

import me.hollow.trollgod.client.command.*;
import me.hollow.trollgod.*;

@CommandManifest(label = "Friend", aliases = { "friends", "friend, f" })
public class FriendCommand extends Command
{
    @Override
    public void execute(final String[] args) {
        if (args.length < 2) {
            return;
        }
        try {
            final String name = args[2];
            final String upperCase = args[1].toUpperCase();
            switch (upperCase) {
                case "ADD": {
                    TrollGod.INSTANCE.getFriendManager().addFriend(name);
                    break;
                }
                case "DEL": {
                    TrollGod.INSTANCE.getFriendManager().removeFriend(name);
                    break;
                }
                case "DELETE": {
                    TrollGod.INSTANCE.getFriendManager().removeFriend(name);
                    break;
                }
                case "CLEAR": {
                    TrollGod.INSTANCE.getFriendManager().clearFriends();
                    break;
                }
                case "INSIDE": {
                    TrollGod.INSTANCE.getFriendManager().clearFriends();
                    break;
                }
            }
        }
        catch (Exception ex) {}
    }
}
