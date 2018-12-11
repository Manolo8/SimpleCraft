package com.github.manolo8.simplecraft.core.commands.line;

import com.github.manolo8.simplecraft.core.commands.line.annotation.Param;
import com.github.manolo8.simplecraft.core.commands.line.inf.CheckerHelper;
import com.github.manolo8.simplecraft.core.commands.line.inf.SupplierHelper;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandSection {

    private int index;
    private int argumentIndex;
    private Argument argument;

    private CommandHandler handler;
    private CommandSection main;

    private SupplierHelper.Basic[] basicParams;
    private List<CommandSection> subSections;

    public CommandSection(String name) {
        index = 0;
        argumentIndex = 0;

        argument = new Argument(name);
        subSections = new ArrayList<>();
    }

    public CommandSection(Argument argument, CommandSection main) {

        this.argument = argument;
        this.index = main.index + 1;
        this.argumentIndex = main.argumentIndex + (argument.isConvert() ? 1 : 0);

        this.main = main;

        this.subSections = new ArrayList<>();
    }

    //======================================================
    //========================SECTION=======================
    //======================================================
    public CommandSection getOrUpdate(String[] args) {
        if (args.length - 1 <= index) return this;

        CommandSection maybe = null;

        for (CommandSection section : subSections) {

            if (section.argument.isConvert()) {
                maybe = section;
            } else if (section.argument.key.equals(args[section.index])) {
                maybe = section;
                break;
            } else if (maybe == null && section.argument.key.startsWith(args[section.index])) {
                maybe = section;
            }

        }

        return maybe == null ? this : maybe.getOrUpdate(args);
    }

    public void build(OptionsRegistry register, String[] args, CommandHandler handler) {

        argument.addPermission(handler.getPermission());

        if (handler.getChecker() == null) {
            argument.checkers.clear();
            argument.blockChecker = true;
        } else if (!argument.blockChecker) {
            String string = handler.getChecker().value();
            boolean reverse = string.startsWith("!");
            String key = reverse ? string.substring(1) : string;

            boolean add = true;

            for (CheckerHelper helper : argument.checkers) {
                if (helper.match(key, reverse)) {
                    add = false;
                    break;
                } else if (helper.conflict(key, reverse)) {
                    argument.blockChecker = true;
                    argument.checkers.clear();
                    add = false;
                    break;
                }
            }

            if (add) {
                argument.checkers.add(new CheckerHelper(register.findChecker(key), reverse));
            }
        }

        if (args.length - 1 == index) {

            this.handler = handler;

            if (handler.getBasicParams() != null) {
                Param[] custom = handler.getBasicParams().value();
                basicParams = new SupplierHelper.Basic[custom.length];

                for (int i = 0; i < custom.length; i++) {
                    basicParams[i] = register.findProvider(custom[i].value(), custom[i].global());
                }

            }

        } else {

            CommandSection sub = getOrCreateSection(register, args[index + 1], handler);

            sub.build(register, args, handler);

        }

    }

    private CommandSection getOrCreateSection(OptionsRegistry register, String key, CommandHandler handler) {

        boolean converter = false;
        boolean required = true;
        boolean infinity = false;

        if (key.startsWith("<") && key.endsWith(">")) {

            converter = true;
            required = key.charAt(1) != '?';
            infinity = key.endsWith("...>");

            key = key.substring(1 + (required ? 0 : 1), key.length() - (1 + (infinity ? 3 : 0)));
        }

        for (CommandSection sub : subSections) {
            if (sub.argument.key.equals(key)) {

                if (sub.argument.isConvert()) {
                    ArgumentConverter ac = (ArgumentConverter) sub.argument;

                    if (!converter) continue;
                    if (required != ac.required) continue;

                } else if (converter) {
                    continue;
                }

                return sub;
            }
        }

        Argument argument;

        if (converter) {
            argument = new ArgumentConverter(key, register.findConvert(key, handler.getMethodParameter(argumentIndex + 1)), required, infinity);
        } else {
            argument = new Argument(key);
        }

        CommandSection sub = new CommandSection(argument, this);

        subSections.add(sub);

        return sub;
    }

    public boolean hasHandler() {
        return handler != null;
    }

    public boolean hasOptionalHandler() {
        if (hasHandler()) return true;
        for (CommandSection section : subSections) {
            if (section.hasHandler()
                    && section.argument instanceof ArgumentConverter &&
                    !((ArgumentConverter) section.argument).required) return true;
        }
        return false;
    }
    //======================================================
    //=======================_SECTION=======================
    //======================================================


    //======================================================
    //========================HANDLER=======================
    //======================================================
    private void sendHelpOrBack(Sender sender) {

        if (subSections.isEmpty()) {
            if (main != null) main.sendHelpOrBack(sender);
        } else {

            List<String> messages = createHelp(sender);

            if (messages.isEmpty()) {
                sender.sendMessage(MessageType.ERROR, "Você não tem permissão para isso!");
            } else {
                sender.sendMessage(MessageType.TITLE, "Ajuda para o comando:");
                for (String string : messages) sender.sendMessage(string);
            }
        }

    }

    public List<String> createHelp(Sender sender) {
        List<String> strings = new ArrayList<>();
        createHelp(sender, strings);
        strings.sort(String::compareTo);
        return strings;
    }

    private void createHelp(Sender sender, List<String> strings) {

        if (handler != null && sender.hasPermission(this)) {
            strings.add("§cUso: §a/" + handler.getUsage() + '\n' + "§c-> §e" + handler.getDescription());
        }

        for (CommandSection section : subSections) {
            section.createHelp(sender, strings);
        }
    }

    public boolean hasPermission(User user) {
        if (!argument.checkers.isEmpty()) {

            boolean has = false;

            for (CheckerHelper checker : argument.checkers)
                if (checker.checkSilent(user))
                    has = true;

            if (!has) return false;
        }


        for (String permission : argument.permissions)
            if (user.hasPermission(permission))
                return true;

        return false;
    }

    public void handle(Sender sender, String[] args) throws SQLException {

        if (handler == null) {

            for (CommandSection section : subSections) {
                if (section.argument.isConvert() && !((ArgumentConverter) section.argument).required) {
                    section.handle(sender, args);
                    return;
                }
            }

            sendHelpOrBack(sender);

        } else if (index + 1 < args.length && !(argument.isConvert() && ((ArgumentConverter) argument).infinity)) {
            System.out.println(index + 1);
            System.out.println(args.length);
            sender.sendMessage(MessageType.ERROR, "Uso correto: " + handler.getUsage());
        } else if (sender.isConsole() && !handler.allowInConsole()) {
            sender.sendMessage(MessageType.ERROR, "Comando desabilitado no console!");
        } else if (!sender.hasPermission(handler.getPermission())) {
            sender.sendMessage(MessageType.ERROR, handler.getPermissionMessage());
        } else {

            //Checker
            for (CheckerHelper checker : argument.checkers) {
                Result result = checker.check(sender.user());

                if (result instanceof Result.Error) {
                    sender.sendMessage(MessageType.ERROR, result.getValue());
                    return;
                }
            }

            if (!handler.allowInPvp() && !sender.isConsole() && sender.user().isInPvp()) {
                sender.sendMessage(MessageType.ERROR, "Comando desativado em PvP!");
            } else {

                ParameterBuilder builder = createParameters(sender, args);

                if (builder.isWrong()) {

                    //Pode ocorrer quando tenta se registrar...
                    if (!sender.isConsole() && !sender.user().isAuthenticated()) {
                        sender.user().base().sendMessage(MessageType.ERROR.format(builder.getError()));
                    } else {
                        sender.sendMessage(MessageType.ERROR, builder.getError());
                    }

                } else {
                    handler.handle(builder.getParameters());
                }
            }
        }
    }

    public void handleTab(Sender sender, String[] args, List<String> strings) throws Exception {
        if (args.length - 1 != index) {
            String current = args[index + 1];

            addCommandLabel(sender, current, strings);

            TabArguments tabArguments = null;

            if (subSections.size() != 0) {
                for (CommandSection section : subSections) {

                    if (!sender.hasPermission(section)) continue;

                    if (tabArguments == null) tabArguments = new TabArguments(this, sender, args, strings, current);

                    if (section.argument.isConvert()) {
                        ((ArgumentConverter) section.argument).helper.tabComplete(tabArguments);
                    }

                }
            } else if (argument.isConvert() && ((ArgumentConverter) argument).infinity) {

                if (tabArguments == null) tabArguments = new TabArguments(this, sender, args, strings, current);

                ((ArgumentConverter) argument).helper.tabComplete(tabArguments);

            }

        } else {

            String current = args[index];

            if (main != null) {
                main.addCommandLabel(sender, current, strings);
            }

            if (!sender.hasPermission(this)) return;

            if (argument.isConvert()) {
                ((ArgumentConverter) argument).helper.tabComplete(new TabArguments(this, sender, args, strings, current));
            }
        }
    }

    private void addCommandLabel(Sender sender, String current, List<String> strings) {
        for (CommandSection section : subSections) {
            if (!section.argument.isConvert()) {
                if (section.argument.key.startsWith(current) && sender.hasPermission(section))
                    strings.add(section.argument.key);
            }
        }
    }

    public ParameterBuilder createParameters(Sender sender, String[] args) throws SQLException {
        ParameterBuilder builder;

        if (handler == null) {
            builder = new ParameterBuilder(argumentIndex + 1, args);
        } else if (basicParams == null) {
            builder = new ParameterBuilder(argumentIndex + 1, args);
            builder.set(0, handler.allowInConsole() ? sender : sender.user());
        } else {
            builder = new ParameterBuilder(argumentIndex + 1 + basicParams.length, args);

            builder.set(0, handler.allowInConsole() ? sender : sender.user());

            int toIndex = argumentIndex + 1;

            for (int i = 0; i < basicParams.length; i++) {

                Result result = basicParams[i].provide(sender);

                if (result instanceof Result.Error) {
                    builder.error(result.getValue());
                    return builder;
                } else {
                    builder.set(toIndex + i, result.getValue());
                }
            }

        }

        createParameters(sender, args, builder);

        return builder;
    }

    private void createParameters(Sender sender, String[] args, ParameterBuilder builder) throws SQLException {

        if (main != null) main.createParameters(sender, args, builder);

        if (builder.isWrong()) return;

        if (argument.isConvert()) {
            ArgumentConverter argumentConverter = ((ArgumentConverter) argument);
            SupplierHelper.Convert convert = argumentConverter.helper;

            if (args.length < index + 1) {
                builder.set(argumentIndex, convert.defaultValue());
            } else {

                String value = ((ArgumentConverter) argument).infinity ? StringUtils.of(args, index, args.length) : args[index];

                Result result = convert.convert(builder, sender, value);

                if (result instanceof Result.Error) {
                    builder.error(result.getValue());
                } else {
                    builder.set(argumentIndex, result.getValue());
                }
            }
        }
    }

    //======================================================
    //=======================_HANDLER=======================
    //======================================================

    public static class Argument {

        String key;
        List<String> permissions;
        boolean blockChecker;
        List<CheckerHelper> checkers;

        public Argument(String key) {
            this.key = key;
            this.permissions = new ArrayList<>();
            this.checkers = new ArrayList<>();
        }

        public boolean isConvert() {
            return false;
        }

        public void addPermission(String permission) {
            if (!permissions.contains(permission)) permissions.add(permission);
        }
    }

    class ArgumentConverter extends Argument {

        SupplierHelper.Convert helper;
        boolean required;
        boolean infinity;

        public ArgumentConverter(String key, SupplierHelper.Convert helper, boolean required, boolean infinity) {
            super(key);

            this.helper = helper;
            this.required = required;
            this.infinity = infinity;
        }

        @Override
        public boolean isConvert() {
            return true;
        }
    }
}
