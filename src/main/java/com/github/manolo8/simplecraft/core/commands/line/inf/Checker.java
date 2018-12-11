package com.github.manolo8.simplecraft.core.commands.line.inf;

import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CheckerOptions;
import com.github.manolo8.simplecraft.module.user.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Checker {

    private CheckerOptions checker;
    private Object object;
    private Method handler;

    public Checker(Object object, Method handler, CheckerOptions checker) {
        this.object = object;
        this.checker = checker;
        this.handler = handler;
        handler.setAccessible(true);
    }

    public String getValue() {
        return checker.value();
    }

    public Result check(User user, boolean reverse) {
        boolean result;

        try {
            result = (boolean) handler.invoke(object, user);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new Error(checker.value() + " not work!");
        }

        if (reverse) result ^= true;

        if (result) return new Result(result);
        else return new Result.Error(reverse ? checker.reverseMessage() : checker.message());
    }

    public boolean checkSilent(User user, boolean reverse) {
        boolean result;

        try {
            result = (boolean) handler.invoke(object, user);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new Error(checker.value() + " not work...!" + (user == null));
        }

        if (reverse) result ^= true;

        return result;
    }
}
