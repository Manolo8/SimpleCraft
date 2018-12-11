package com.github.manolo8.simplecraft.core.commands.line.inf;

import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.module.user.User;

public class CheckerHelper {

    private Checker checker;
    private boolean reverse;

    public CheckerHelper(Checker checker, boolean reverse) {
        this.checker = checker;
        this.reverse = reverse;
    }

    public Result check(User user) {
        return checker.check(user, reverse);
    }

    public boolean checkSilent(User user) {
        return checker.checkSilent(user, reverse);
    }

    public String getValue() {
        return checker.getValue();
    }

    public boolean match(String key, boolean reverse) {
        return checker.getValue().equals(key) && this.reverse == reverse;
    }

    public boolean conflict(String key, boolean reverse) {
        return checker.getValue().equals(key) && this.reverse != reverse;
    }
}
