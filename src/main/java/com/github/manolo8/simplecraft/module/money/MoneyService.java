package com.github.manolo8.simplecraft.module.money;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolder;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolderBuilder;
import com.github.manolo8.simplecraft.core.placeholder.annotation.PlaceHolderMapping;
import com.github.manolo8.simplecraft.core.service.RepositoryService;
import com.github.manolo8.simplecraft.module.money.view.CashView;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.PageableList;
import com.github.manolo8.simplecraft.utils.def.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class MoneyService extends RepositoryService<MoneyRepository> {

    private List<Money> moneyTop;

    public MoneyService(MoneyRepository repository) {
        super(repository);

        this.moneyTop = new ArrayList<>();
    }

    public Money getMoney(String name) throws SQLException {
        return repository.findOneByIdentity(name);
    }

    public void updateMoneyTop() {
        moneyTop.clear();

        try {
            moneyTop.addAll(repository.findMoneyTop());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        moneyTop.sort((o1, o2) -> Double.compare(o2.getCoins(), o1.getCoins()));
    }

    public List<Money> getMoneyTop() {
        return moneyTop;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================

    @CmdInfo("money")
    public void addInfo(Command command) {
        command.setAliases(Arrays.asList("eco", "dinheiro"));
        command.setDescription("Informações básicas em relação ao money");
    }

    @CmdMapping("cash")
    @CmdDescription("Cash")
    @CmdPermission("simplecraft.user")
    public void cash(User user) {
        user.createView(new CashView());
    }

    @CmdMapping("money")
    @CmdDescription("Ver o seu money")
    @CmdPermission("simplecraft.user")
    public void money(User user) {
        user.sendMessage(MessageType.INFO, "Você tem " + user.money().getCoinsFormatted() + " coins");
    }

    @CmdMapping("money <user>")
    @CmdDescription("Ver o money de outro jogador")
    @CmdPermission("simplecraft.user")
    public void moneyOthers(Sender sender, Money target) {
        sender.sendMessage(MessageType.INFO, target.getIdentity().getName() + " tem " + target.getCoinsFormatted() + " coins");
    }

    @CmdMapping("money <user> pagar <quantity>")
    @CmdDescription("Pagar um jogador")
    @CmdPermission("simplecraft.user")
    public void moneyPay(User user, Money target, double value) {
        if (value < 100) {
            user.sendMessage(MessageType.ERROR, "O valor mínimo para transferir é 100!");
        } else if (user.money().withdrawCoins(value)) {
            target.depositCoins(value);
            user.sendMessage(MessageType.SUCCESS, "Foi depositado " + StringUtils.doubleToString0D(value) + " na conta de " + target.getIdentity().getName() + "!");
        } else {
            user.sendMessage(MessageType.ERROR, "Você não tem esse valor!");
        }
    }

    @CmdMapping("money top <?page>")
    @CmdDescription("Ver o ranking dos mais ricos")
    @CmdPermission("simplecraft.user")
    public void moneyTop(Sender sender, int pg) {
        PageableList<Money> page = new PageableList(moneyTop, "§c->  Money TOP 100", pg, 10);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append("§7");
            StringUtils.fill(true, 100, current, ' ', builder);
            builder.append("º  §r");
            StringUtils.fill(false, 400, item.getIdentity().getName(), '.', builder);
            StringUtils.fill(true, 400, "§7" + StringUtils.doubleToString0D(item.getCoins()), '.', builder);
            builder.append("\n");
        }));
    }

    @CmdMapping("money -e <user> set <quantity>")
    @CmdDescription("Setar o money de um jogador")
    @CmdPermission("simplecraft.admin")
    public void moneySet(Sender sender, Money money, double value) {

        money.setCoins(value);
        sender.sendMessage(MessageType.SUCCESS, "O valor foi setado!");

    }

    @CmdMapping("money -e <user> give <quantity>")
    @CmdDescription("Dar money para um jogador")
    @CmdPermission("simplecraft.admin")
    public void moneyGive(Sender sender, Money money, double value) {

        money.depositCoins(value);
        sender.sendMessage(MessageType.SUCCESS, "O valor foi adicionado");

    }

    @CmdMapping("money -e <user> withdraw <quantity>")
    @CmdDescription("Tirar money de um jogador")
    @CmdPermission("simplecraft.admin")
    public void moneyWithdraw(Sender sender, Money money, double value) {

        if (money.withdrawCoins(value)) {
            sender.sendMessage(MessageType.SUCCESS, "O valor foi retirado com sucesso!");
        } else {
            sender.sendMessage(MessageType.ERROR, "O jogador não tem esse valor!");
        }

    }

    //======================================================
    //=======================REGISTRY=======================
    //======================================================

    @SupplierOptions("user")
    class MoneyUserSupplier implements Supplier.Convert<Money> {

        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.getIdentityRepository().findNames(arguments.getComplete()));
        }

        @Override
        public Result<Money> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Money money = repository.findOneByIdentity(value);

            if (money == null) return new Result.Error("O jogador " + value + " não foi encontrado");

            return new Result<>(money);
        }
    }
    //======================================================
    //======================_REGISTRY=======================
    //======================================================


    //======================================================
    //======================_COMMANDS=======================
    //======================================================

    @PlaceHolderMapping("cash")
    class CashPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final Money money = target.money();

                @Override
                public String value() {
                    return money.getCashFormatted();
                }

                @Override
                public long lastModified() {
                    return money.getLastModified();
                }
            };
        }
    }

    @PlaceHolderMapping("money")
    class MoneyPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final Money money = target.money();

                @Override
                public String value() {
                    return money.getCoinsFormatted();
                }

                @Override
                public long lastModified() {
                    return money.getLastModified();
                }
            };
        }
    }

    //======================================================
    //===================_PLACE_HOLDERS=====================
    //======================================================
}
