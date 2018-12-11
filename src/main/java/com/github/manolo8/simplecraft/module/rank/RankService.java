package com.github.manolo8.simplecraft.module.rank;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.NamedHolderService;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;

import java.sql.SQLException;
import java.util.Collections;

@SuppressWarnings("unused")
public class RankService extends NamedHolderService<Rank, RankRepository> {

    public static RankService instance;

    public RankService(RankRepository repository) {
        super(repository);
        instance = this;
    }

    public Rank findNextRank(Rank current) {
        return findByPoints(current.get() + 1);
    }

    public Rank findByPoints(int i) {
        for (Rank rank : entities) {
            if (rank.get() == i) {
                return rank;
            }
        }

        return null;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("rank")
    public void addInfo(Command command) {
        command.setAliases(Collections.singletonList("rank"));
        command.setDescription("Comandos úteis dos ranks");
    }

    @CmdMapping("rank")
    @CmdDescription("Informações do RANK")
    @CmdPermission("simplecraft.user")
    public void rank(User user) {
        user.createView(new RankView());
    }

    @CmdMapping("rank create <name>")
    @CmdDescription("Cria um rank")
    @CmdPermission("simplecraft.admin")
    public void rankCreate(Sender sender, String name) throws SQLException {

        if (exists(name)) {
            sender.sendMessage(MessageType.ERROR, "O rank já existe!");
        } else {
            Rank rank = create(name);
            sender.sendMessage(MessageType.SUCCESS, "O rank '" + name + "' foi criado!");
        }
    }

    @CmdMapping("rank -e <rank> set name <name>")
    @CmdDescription("Seta o nome de um rank")
    @CmdPermission("simplecraft.admin")
    public void rankSetName(Sender sender, Rank rank, String name) {

        if (exists(name)) {
            sender.sendMessage(MessageType.ERROR, "O rank já existe!");
        } else {
            rank.setName(name);
            sender.sendMessage(MessageType.SUCCESS, "O nome do rank foi alterado!");
        }
    }

    @CmdMapping("rank -e <rank> set tag <tag>")
    @CmdDescription("Seta a tag de um rank")
    @CmdPermission("simplecraft.admin")
    public void rankSetTag(Sender sender, Rank rank, String tag) {

        rank.setTag(tag);
        sender.sendMessage(MessageType.SUCCESS, "A tag foi alterada!");

    }

    @CmdMapping("rank -e <rank> set points <value>")
    @CmdDescription("Seta os pontos de um rank")
    @CmdPermission("simplecraft.admin")
    public void rankSetPoints(Sender sender, Rank rank, int value) {

        rank.setRank(value);
        sender.sendMessage(MessageType.SUCCESS, "O valor foi alterado!");

    }

    @CmdMapping("rank -e <rank> set data <coins>")
    @CmdDescription("Seta o custo de um rank")
    @CmdPermission("simplecraft.admin")
    public void rankSetPoints(Sender sender, Rank rank, double coins) {

        rank.setCost(coins);
        sender.sendMessage(MessageType.SUCCESS, "O valor foi alterado!");

    }

    @CmdMapping("rank user <user> set <rank>")
    @CmdDescription("Seta um jogador em um rank")
    @CmdPermission("simplecraft.admin")
    public void rankUserSet(Sender sender, User target, Rank rank) {

        target.changeRank(rank);
        sender.sendMessage(MessageType.SUCCESS, "O rank do jogador foi alterado!");

    }

    @CmdMapping("rank user <user> up")
    @CmdDescription("Evolui o rank do jogador")
    @CmdPermission("simplecraft.admin")
    public void rankSetUp(Sender sender, User target) {

        Rank next = findNextRank(target.rank());

        if (next == null) {
            sender.sendMessage(MessageType.ERROR, "O jogador está no último rank!");
        } else {
            target.changeRank(next);
            sender.sendMessage(MessageType.SUCCESS, "O jogador evoluiu para o RANK " + next.getTag() + "!");
        }

    }

    @SupplierOptions("rank")
    class RankConverter implements Supplier.Convert<Rank> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<Rank> convert(ParameterBuilder builder, Sender sender, String value) {
            Rank rank = findByName(value);

            if (rank == null) return new Result.Error("O rank '" + value + "' não foi encontrado!");

            return new Result(rank);
        }
    }
    //======================================================
    //=======================COMMANDS=======================
    //======================================================
}