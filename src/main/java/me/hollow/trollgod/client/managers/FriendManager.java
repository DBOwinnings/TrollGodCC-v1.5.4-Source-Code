package me.hollow.trollgod.client.managers;

import com.google.gson.*;
import java.util.*;
import com.google.common.reflect.*;
import java.io.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.entity.player.*;

public class FriendManager
{
    private Set<String> friends;
    private File directory;
    
    public FriendManager() {
        this.friends = new HashSet<String>();
    }
    
    public void init() {
        if (!this.directory.exists()) {
            try {
                this.directory.createNewFile();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.loadFriends();
    }
    
    public void unload() {
        this.saveFriends();
    }
    
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    public void saveFriends() {
        if (this.directory.exists()) {
            try (final Writer writer = new FileWriter(this.directory)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson((Object)this.friends));
            }
            catch (IOException e) {
                this.directory.delete();
            }
        }
    }
    
    public void loadFriends() {
        if (!this.directory.exists()) {
            return;
        }
        try (final FileReader inFile = new FileReader(this.directory)) {
            this.friends = new HashSet<String>((Collection<? extends String>)new GsonBuilder().setPrettyPrinting().create().fromJson((Reader)inFile, new TypeToken<HashSet<String>>() {}.getType()));
        }
        catch (Exception ex) {}
    }
    
    public void addFriend(final String name) {
        MessageUtil.sendClientMessage("Added " + name + " as a friend ", false);
        this.friends.add(name);
    }
    
    public final boolean isFriend(final String ign) {
        return this.friends.contains(ign);
    }
    
    public boolean isFriend(final EntityPlayer ign) {
        return this.friends.contains(ign.getName());
    }
    
    public void clearFriends() {
        this.friends.clear();
    }
    
    public void removeFriend(final String name) {
        this.friends.remove(name);
    }
}
