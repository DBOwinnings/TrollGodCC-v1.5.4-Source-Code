package me.hollow.trollgod.client.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CommandManifest {
    String label();
    
    String[] aliases() default {};
}
