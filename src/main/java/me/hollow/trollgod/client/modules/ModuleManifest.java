package me.hollow.trollgod.client.modules;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ModuleManifest {
    String label() default "";
    
    Module.Category category();
    
    int key() default 0;
    
    boolean persistent() default false;
    
    boolean drawn() default true;
    
    boolean listen() default true;
    
    int color() default -1;
}
