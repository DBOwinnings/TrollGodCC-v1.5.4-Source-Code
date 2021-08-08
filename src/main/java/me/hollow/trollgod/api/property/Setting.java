package me.hollow.trollgod.api.property;

import java.util.function.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.events.*;

public class Setting<T>
{
    private final String name;
    private final T defaultValue;
    private T value;
    private T min;
    private T max;
    private boolean hasRestriction;
    private Predicate<T> visibility;
    
    public Setting(final String name, final T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }
    
    public Setting(final String name, final T defaultValue, final T min, final T max) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.hasRestriction = true;
    }
    
    public Setting(final String name, final T defaultValue, final T min, final T max, final Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.visibility = visibility;
        this.hasRestriction = true;
    }
    
    public Setting(final String name, final T defaultValue, final Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.visibility = visibility;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final T getValue() {
        return this.value;
    }
    
    public final T getMin() {
        return this.min;
    }
    
    public final T getMax() {
        return this.max;
    }
    
    public final void setValue(final T value) {
        TrollGod.INSTANCE.getBus().post(new ClientEvent(this));
        if (this.hasRestriction) {
            T plannedValue = value;
            if (((Number)this.min).floatValue() > ((Number)value).floatValue()) {
                plannedValue = this.min;
            }
            if (((Number)this.max).floatValue() < ((Number)value).floatValue()) {
                plannedValue = this.max;
            }
            this.value = plannedValue;
            return;
        }
        this.value = value;
    }
    
    public int getEnum(final String input) {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; ++i) {
            final Enum e = (Enum)this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return i;
            }
        }
        return -1;
    }
    
    public void setEnumValue(final String value) {
        for (final Enum e : (Enum[])((Enum)this.value).getClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                this.value = (T)e;
            }
        }
    }
    
    public final String currentEnumName() {
        return EnumConverter.getProperName((Enum)this.value);
    }
    
    public void increaseEnum() {
        this.value = (T)EnumConverter.increaseEnum((Enum)this.value);
    }
    
    public String getType() {
        if (this.isEnumSetting()) {
            return "Enum";
        }
        return this.getClassName(this.defaultValue);
    }
    
    public <T> String getClassName(final T value) {
        return value.getClass().getSimpleName();
    }
    
    public boolean isNumberSetting() {
        return this.value instanceof Double || this.value instanceof Integer || this.value instanceof Short || this.value instanceof Long || this.value instanceof Float;
    }
    
    public boolean isEnumSetting() {
        return !this.isNumberSetting() && !(this.value instanceof Bind) && !(this.value instanceof String) && !(this.value instanceof Character) && !(this.value instanceof Boolean);
    }
    
    public boolean isStringSetting() {
        return this.value instanceof String;
    }
    
    public T getDefaultValue() {
        return this.defaultValue;
    }
    
    public String getValueAsString() {
        return this.value.toString();
    }
    
    public boolean hasRestriction() {
        return this.hasRestriction;
    }
    
    public void setVisibility(final Predicate<T> visibility) {
        this.visibility = visibility;
    }
    
    public boolean isVisible() {
        return this.visibility == null || this.visibility.test(this.getValue());
    }
}
