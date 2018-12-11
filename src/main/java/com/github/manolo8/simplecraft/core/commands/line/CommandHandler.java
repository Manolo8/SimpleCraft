package com.github.manolo8.simplecraft.core.commands.line;

import com.github.manolo8.simplecraft.SimpleCraft;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

public class CommandHandler {

    private final Object object;
    private final Method method;
    private CmdMapping mapping;
    private CmdDescription description;
    private CmdPermission permission;
    private CmdOptions options;
    private CmdParams basicParams;
    private CmdChecker checker;
    private boolean console;

    public CommandHandler(Object object, Method method) {
        this.object = object;
        this.method = method;

        mapping = method.getAnnotation(CmdMapping.class);
        description = method.getAnnotation(CmdDescription.class);
        permission = method.getAnnotation(CmdPermission.class);
        options = method.getAnnotation(CmdOptions.class);
        checker = method.getAnnotation(CmdChecker.class);
        basicParams = method.getAnnotation(CmdParams.class);
        console = getMethodParameter(0) == Sender.class;
    }

    public void handle(Object[] arguments) {
        if (isSync()) {
            handle0(arguments);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(SimpleCraft.instance, () -> handle0(arguments), 0);
        }
    }

    private void handle0(Object[] arguments) {
        try {
            method.invoke(object, arguments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsage() {
        return mapping.value();
    }

    public String getPermission() {
        return permission == null ? "simplecraft.admin" : permission.value();
    }

    public String getPermissionMessage() {
        return permission == null ? "Você não tem permissão para isso!" : permission.message();
    }

    public String getDescription() {
        return description == null ? "..." : description.value();

    }

    public boolean allowInPvp() {
        return (options == null) || options.pvp();
    }

    public boolean allowInConsole() {
        return console;
    }

    public CmdParams getBasicParams() {
        return basicParams;
    }

    public CmdChecker getChecker() {
        return checker;
    }

    public boolean isSync() {
        return options == null || options.sync();
    }

    public Class getMethodParameter(int i) {
        return method.getParameterTypes()[i];
    }
}
