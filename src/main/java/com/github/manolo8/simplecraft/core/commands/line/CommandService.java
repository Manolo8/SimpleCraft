package com.github.manolo8.simplecraft.core.commands.line;

import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.Service;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.provider.Provider;
import com.github.manolo8.simplecraft.module.user.Points;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.def.PageableList;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CommandService
        extends Service {

    private final UserService userService;
    private final WorldService worldService;
    private final OptionsRegistry optionsRegistry;
    private List<Command> commands;

    private List<Object> temp;

    public CommandService(UserService userService, WorldService worldService) {
        this.userService = userService;
        this.worldService = worldService;

        optionsRegistry = new OptionsRegistry();
        commands = new ArrayList<>();
        temp = new ArrayList<>();
    }

    public void init() {

        for (Object object : temp) {
            for (Method method : object.getClass().getMethods()) {

                CmdMapping cmdMapping = method.getAnnotation(CmdMapping.class);
                CmdPermission cmdPermission = method.getAnnotation(CmdPermission.class);
                CmdInfo cmdInfo = method.getAnnotation(CmdInfo.class);

                if (cmdMapping != null) {

                    String[] args = cmdMapping.value().split(" ");

                    Command command = findOrCreateBase(args[0].toLowerCase());

                    command.createHandler(optionsRegistry, object, method, args);
                } else if (cmdInfo != null) {
                    try {
                        method.invoke(object, findOrCreateBase(cmdInfo.value()));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        SimpleCommandMap map = ((CraftServer) Bukkit.getServer()).getCommandMap();

        for (Command command : commands) {
            map.register("simplecraft", command);
        }

        temp = null;
    }

    public void register(Service enabled) {
        optionsRegistry.build(enabled);
        temp.add(enabled);
    }

    private Command findOrCreateBase(String base) {
        for (Command command : commands)
            if (command.getName().equals(base))
                return command;

        Command command = new Command(userService, base);

        commands.add(command);

        return command;
    }

    //======================================================
    //=======================DEFAULTS=======================
    //======================================================
    @CmdMapping("ajuda <?page>")
    @CmdDescription("Obter ajuda")
    @CmdPermission("simplecraft.user")
    public void help(Sender sender, int pg) {

        List<Command> filter;

        if (sender.isConsole()) {
            filter = commands;
        } else {
            filter = new ArrayList<>();
            User user = sender.user();

            for (Command command : commands) {
                if (!command.getName().equals("ajuda") && command.getSection().hasPermission(user))
                    filter.add(command);
            }
        }

        PageableList<Command> page = new PageableList(filter, "§c->  Ajuda para todos os comandos", pg, 5);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append("§a/")
                    .append(item.getName())
                    .append(" ajuda")
                    .append(" §e")
                    .append("Para obter mais informações")
                    .append('\n');
        }));
    }

    @SupplierOptions({"name", "string"})
    class NameConvert implements Supplier.Convert<String> {

        @Override
        public Result<String> convert(ParameterBuilder builder, Sender sender, String value) {
            return new Result(value);
        }
    }

    @SupplierOptions({"message", "text"})
    class MessageConvert implements Supplier.Convert<String> {

        @Override
        public Result<String> convert(ParameterBuilder builder, Sender sender, String value) {
            return new Result(sender.hasPermission("simplecraft.chat.color") ? StringUtils.toStringWithColors(value) : value);
        }
    }

    @SupplierOptions({"title", "tag"})
    class TagConvert implements Supplier.Convert<String> {

        @Override
        public Result<String> convert(ParameterBuilder builder, Sender sender, String value) {
            return new Result(StringUtils.toStringWithColors(value));
        }
    }

    @SupplierOptions({"boolean"})
    class BooleanConvert implements Supplier.Convert<Boolean> {

        @Override
        public Boolean defaultValue() {
            return false;
        }

        @Override
        public void tabComplete(TabArguments arguments) {
            arguments.offer("sim");
            arguments.offer("não");
        }

        @Override
        public Result<Boolean> convert(ParameterBuilder builder, Sender sender, String value) {
            switch (value.toLowerCase()) {
                case "false":
                case "não":
                case "nao":
                case "0":
                case "no":
                    return new Result<>(false);
                case "true":
                case "sim":
                case "1":
                case "yes":
                    return new Result<>(true);
                default:
                    return new Result.Error("O valor '" + value + "' não é um boolean xD");
            }
        }
    }

    @SupplierOptions({"quantity", "percent", "value"})
    class DecimalB0Convert implements Supplier.Convert<Double> {

        @Override
        public Double defaultValue() {
            return 0D;
        }

        @Override
        public Result<Double> convert(ParameterBuilder builder, Sender sender, String value) {
            try {
                double number = Double.parseDouble(value);

                if (number != number) {
                    return new Result.Error("O valor '" + value + "' não é um número");
                } else if (!Double.isFinite(number) || number > 9_000_000_000D) {
                    return new Result.Error("O valor '" + value + "' é inválido");
                } else if (number < 0) {
                    return new Result.Error("O valor '" + value + "' é menor que 0");
                } else {
                    return new Result<>(number);
                }

            } catch (NumberFormatException e) {
                return new Result.Error("O valor '" + value + "' não é um número.");
            }
        }
    }

    @SupplierOptions("coins")
    class CoinsConvert implements Supplier.Convert<Double> {

        @Override
        public Double defaultValue() {
            return 0D;
        }

        @Override
        public Result<Double> convert(ParameterBuilder builder, Sender sender, String value) {
            try {
                double number = Double.parseDouble(value);

                if (number != number) {
                    return new Result.Error("O valor '" + value + "' não é um número");
                } else if (!Double.isFinite(number) || number > 9_000_000_000D) {
                    return new Result.Error("O valor '" + value + "' é inválido");
                } else if (number <= 0) {
                    return new Result.Error("O valor '" + value + "' é menor que 0");
                } else {
                    return new Result<>(number);
                }


            } catch (NumberFormatException e) {
                return new Result.Error("O valor '" + value + "' não é um número.");
            }
        }
    }

    @SupplierOptions({"quantity", "lucky", "value", "priority"})
    class IntegerConvert implements Supplier.Convert<Integer> {

        @Override
        public Integer defaultValue() {
            return 0;
        }

        @Override
        public Result<Integer> convert(ParameterBuilder builder, Sender sender, String value) {
            try {
                int number = Integer.parseInt(value);

                if (number != number) {
                    return new Result.Error("O valor '" + value + "' não é um número. (literalmente '-')");
                } else if (number < 0) {
                    return new Result.Error("O valor " + value + " é menor que 0");
                } else {
                    return new Result<>(number);
                }

            } catch (NumberFormatException e) {
                return new Result.Error("O valor " + value + " é inválido.");
            }
        }
    }

    @SupplierOptions({"x", "y", "z"})
    class CordsConverter implements Supplier.Convert<Integer> {

        @Override
        public Integer defaultValue() {
            return 0;
        }

        @Override
        public Result<Integer> convert(ParameterBuilder builder, Sender sender, String value) {
            try {
                int number = Integer.parseInt(value);

                if (number != number) {
                    return new Result.Error("O valor " + value + " é inválido");
                } else {
                    return new Result<>(number);
                }

            } catch (NumberFormatException e) {
                return new Result.Error("O valor " + value + " é inválido.");
            }
        }
    }

    @SupplierOptions({"time", "delay"})
    class TimeConvert implements Supplier.Convert<Long> {

        @Override
        public Long defaultValue() {
            return 0L;
        }

        @Override
        public void tabComplete(TabArguments arguments) {
            String value = arguments.getComplete();

            if (value.isEmpty()) return;

            char end = value.charAt(value.length() - 1);

            if (!Character.isDigit(end)) {
                value = value.substring(0, value.length() - 1);
            }

            arguments.offer(value + 'm');
            arguments.offer(value + 'h');
            arguments.offer(value + 'd');
            arguments.offer(value + 'w');
        }

        @Override
        public Result<Long> convert(ParameterBuilder builder, Sender sender, String value) {
            try {
                char end = value.charAt(value.length() - 1);
                end = Character.toLowerCase(end);

                int multiplier = 1000;

                if (!Character.isDigit(end)) {
                    switch (end) {
                        case 'm':
                            multiplier *= 60;
                            break;
                        case 'h':
                            multiplier *= 60 * 60;
                            break;
                        case 'd':
                            multiplier *= 60 * 60 * 24;
                            break;
                        case 'w':
                            multiplier *= 60 * 60 * 24 * 7;
                            break;
                    }

                    value = value.substring(0, value.length() - 1);
                }
                long number = Long.parseLong(value) * multiplier;

                if (number != number) {
                    return new Result.Error("O valor '" + value + "' não é um número.");
                } else if (number <= 0) {
                    return new Result.Error("O valor '" + value + "' é <= 0");
                } else {
                    return new Result<>(number);
                }

            } catch (NumberFormatException e) {
                return new Result.Error("O tempo '" + value + "' é inválido.");
            }
        }
    }

    @SupplierOptions("1/x")
    class FractionConvert implements Supplier.Convert<Double> {

        @Override
        public Double defaultValue() {
            return 0d;
        }

        @Override
        public Result<Double> convert(ParameterBuilder builder, Sender sender, String value) {
            try {
                return new Result(1D / Double.parseDouble(value));
            } catch (NumberFormatException e) {
                return new Result.Error("O valor " + value + " não é um número.");
            }
        }
    }

    @SupplierOptions("page")
    class PageConvert implements Supplier.Convert<Integer> {

        @Override
        public Integer defaultValue() {
            return 0;
        }

        @Override
        public Result<Integer> convert(ParameterBuilder builder, Sender sender, String value) {
            try {
                int page = Integer.parseInt(value);

                if (page < 0 || page > 100) return new Result<>(0);

                return new Result(page);

            } catch (NumberFormatException e) {
                return new Result.Error("O valor " + value + " não é um número.");
            }
        }

    }

    @SupplierOptions("block")
    class BlockConvert implements Supplier.Convert<Material> {

        @Override
        public void tabComplete(TabArguments arguments) {
            arguments.offer("hand");

            for (Material material : Material.values())
                if (material.isBlock() && material.isSolid() && !material.isTransparent())
                    arguments.offer(material.name());

        }

        @Override
        public Result<Material> convert(ParameterBuilder builder, Sender sender, String value) {
            if (value.equals("hand")) {
                if (sender.isConsole()) {
                    return new Result.Error("Você é o console '-'");
                } else {
                    ItemStack hand = sender.user().base().getInventory().getItemInMainHand();
                    return new Result(hand.getType());
                }
            } else {
                Material material = Material.getMaterial(value.toUpperCase());

                if (material == null) {
                    return new Result.Error("O material " + value + " não foi encontrado");
                } else {
                    return new Result(material);
                }
            }
        }
    }

    @SupplierOptions("material")
    class MaterialConvert implements Supplier.Convert<Material> {

        @Override
        public void tabComplete(TabArguments arguments) {
            arguments.offer("hand");

            for (Material material : Material.values())
                arguments.offer(material.name());
        }

        @Override
        public Result<Material> convert(ParameterBuilder builder, Sender sender, String value) {
            if (value.equals("hand")) {
                if (sender.isConsole()) {
                    return new Result.Error("Você é o console '-'");
                } else {
                    ItemStack hand = sender.user().base().getInventory().getItemInMainHand();
                    return new Result(hand.getType());
                }
            } else {
                Material material = Material.getMaterial(value.toUpperCase());

                if (material == null) {
                    return new Result.Error("O material " + value + " não foi encontrado");
                } else {
                    return new Result(material);
                }
            }
        }
    }

    @SupplierOptions("item")
    class ItemStackConvert implements Supplier.Convert<ItemStack> {

        @Override
        public void tabComplete(TabArguments arguments) {
            arguments.offer("hand");
        }

        @Override
        public Result<ItemStack> convert(ParameterBuilder builder, Sender sender, String value) {
            if (value.equals("hand")) {
                if (sender.isConsole()) {
                    return new Result.Error("Você é o console '-'");
                } else {
                    ItemStack hand = sender.user().base().getInventory().getItemInMainHand().clone();
                    return new Result(hand);
                }
            } else {
                return new Result.Error("Apenas hand é suportado no momento!");
            }
        }
    }

    @SupplierOptions("world")
    class WorldConverter implements Supplier.Convert<World> {

        @Override
        public World defaultValue() {
            return Bukkit.getWorlds().get(0);
        }

        @Override
        public void tabComplete(TabArguments arguments) {

            for (World world : Bukkit.getWorlds()) {
                arguments.offer(world.getName());
            }

        }

        @Override
        public Result<World> convert(ParameterBuilder builder, Sender sender, String value) {
            World world = Bukkit.getWorld(value);

            if (world == null) {
                return new Result.Error("O mundo '" + value + "' não foi encontrado!");
            } else {
                return new Result(world);
            }
        }
    }

    @SupplierOptions(console = false)
    class ItemStackBasic implements Supplier.Basic<ItemStack> {

        @Override
        public Result provide(Sender sender, Class ignored) {
            return new Result(sender.user().base().getInventory().getItemInMainHand());
        }
    }

    @SupplierOptions(console = false)
    class SimpleAreaBasic implements Supplier.Basic<SimpleArea> {

        @Override
        public Result provide(Sender sender, Class ignored) {
            Points points = sender.user().points();

            if (points.isMarked()) {
                return new Result(points.asSimpleArea());
            } else {
                return new Result.Error("Você deve marcar os 2 pontos antes!");
            }
        }
    }

    @SupplierOptions(console = false)
    class SimpleLocationBasic implements Supplier.Basic<SimpleLocation> {

        @Override
        public Result provide(Sender sender, Class ignored) {
            return new Result(new SimpleLocation(sender.user().base().getLocation()));
        }
    }

    @SupplierOptions(console = false)
    class WorldProviderBasic implements Supplier.Basic<Provider> {

        @Override
        public Result provide(Sender sender, Class<Provider> clazz) {
            User user = sender.user();

            Provider provider = user.worldInfo().getProvider(clazz);

            if (provider == null) {
                provider = worldService.findFirstProvider(clazz);
            }

            if (provider == null) {
                return new Result.Error("O provedor não foi encontrado!");
            } else {
                return new Result<>(provider);
            }
        }

        @Override
        public Result<Provider> provideLocal(Sender sender, Class<Provider> clazz) {
            User user = sender.user();

            Provider provider = user.worldInfo().getProvider(clazz);

            if (provider == null) {
                return new Result.Error("O provedor não foi encontrado para este mundo!");
            } else {
                return new Result<>(provider);
            }
        }
    }
}
