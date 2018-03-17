package com.github.manolo8.simplecraft.core.commands.def.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Willian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandMapping
{

    public String permission() default "simplecraft";

    public String permissionMessage() default "§cVocê não tem permissão para isso!";

    public boolean defaultCommand() default false;

    public int[] args() default 1;

    public String command() default "default";
    
    public String subCommand() default "";
    
    public String usage() default "";
}
