package me.hollow.trollgod.api.property;

import org.lwjgl.input.*;
import com.google.common.base.*;
import com.google.gson.*;

public class Bind
{
    private final int key;
    
    public Bind(final int key) {
        this.key = key;
    }
    
    public final int getKey() {
        return this.key;
    }
    
    public final boolean isEmpty() {
        return this.key < 0;
    }
    
    @Override
    public String toString() {
        return this.isEmpty() ? "None" : ((this.key < 0) ? "None" : this.capitalise(Keyboard.getKeyName(this.key)));
    }
    
    public boolean isDown() {
        return !this.isEmpty() && Keyboard.isKeyDown(this.getKey());
    }
    
    private String capitalise(final String str) {
        if (str.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + ((str.length() != 1) ? str.substring(1).toLowerCase() : "");
    }
    
    public static Bind none() {
        return new Bind(-1);
    }
    
    public static class BindConverter extends Converter<Bind, JsonElement>
    {
        public JsonElement doForward(final Bind bind) {
            return (JsonElement)new JsonPrimitive(bind.toString());
        }
        
        public Bind doBackward(final JsonElement jsonElement) {
            final String s = jsonElement.getAsString();
            if (s.equalsIgnoreCase("None")) {
                return Bind.none();
            }
            int key = -1;
            try {
                key = Keyboard.getKeyIndex(s.toUpperCase());
            }
            catch (Exception ex) {}
            if (key == 0) {
                return Bind.none();
            }
            return new Bind(key);
        }
    }
}
