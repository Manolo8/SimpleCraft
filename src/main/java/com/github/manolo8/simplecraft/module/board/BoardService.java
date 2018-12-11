package com.github.manolo8.simplecraft.module.board;

import com.github.manolo8.simplecraft.core.commands.line.ParameterBuilder;
import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.core.commands.line.Sender;
import com.github.manolo8.simplecraft.core.commands.line.TabArguments;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdDescription;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdMapping;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdPermission;
import com.github.manolo8.simplecraft.core.commands.line.annotation.SupplierOptions;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolderService;
import com.github.manolo8.simplecraft.core.service.HolderService;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Counter;
import com.github.manolo8.simplecraft.utils.def.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class BoardService extends HolderService<BoardItem, BoardItemRepository> {

    private HashSet<BoardUser> boardList;

    public BoardService(BoardItemRepository boardItemRepository) {
        super(boardItemRepository);

        this.boardList = new HashSet<>();
    }

    public BoardItem create(String name, int priority) throws SQLException {
        BoardItem entity = repository.create(name, priority);

        load(entity);

        return entity;
    }

    public BoardItem findOrCreate(String name, int priority) throws SQLException {
        for (BoardItem item : entities) {

            if (item.getPriority() == priority) {
                return item;
            }

        }

        return create(name, priority);
    }

    public BoardUser handler(User entity) {
        BoardUser boardUser = new BoardUser(entity);

        this.boardList.add(boardUser);

        for (BoardItem item : entities) {
            boardUser.add(item);
        }

        return boardUser;
    }

    public void remove(BoardUser boardUser) {
        this.boardList.remove(boardUser);
    }

    @Override
    protected void load(BoardItem entity) {
        super.load(entity);

        eachDo(boardUser -> boardUser.add(entity));
    }

    @Override
    protected void unload(BoardItem entity) {
        super.unload(entity);

        eachDo(boardUser -> boardUser.remove(entity));
    }

    private void eachDo(Consumer<BoardUser> consumer) {
        synchronized (BoardUser.LOCKER) {
            for (BoardUser board : boardList) {
                consumer.accept(board);
            }
        }
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdMapping("board -e <item> set <board-value...>")
    @CmdDescription("Atualiza o valor de um item do scoreboard")
    @CmdPermission("simplecraft.admin")
    public void boardSetItem(Sender sender, BoardItem item, String text) {

        item.setValue(text);
        sender.sendMessage(MessageType.SUCCESS, "O item foi atualizado!");

    }

    @CmdMapping("board -e <item> remove")
    @CmdDescription("Remove um item do scoreboard")
    @CmdPermission("simplecraft.admin")
    public void boardRemove(Sender sender, BoardItem item) {

        item.remove();
        unload(item);

        sender.sendMessage(MessageType.SUCCESS, "O item foi removido!");
    }

//    @CmdMapping("board -e <item> on <container> hide <boolean>")
//    @CmdDescription("Esconde um item quando estiver no container")
//    @CmdPermission("simplecraft.admin")
//    public void boardOn(Sender sender, BoardItem item, boolean hide) {
//
//        //rule
//
//    }
//
//    @CmdMapping("board -e <item> on <container> set <bord-value...>")
//    @CmdDescription("Seta um valor quando no container")
//    @CmdPermission("simplecraft.admin")
//    public void boardOnSet(Sender sender, BoardItem item, String text) {
//
//        //rule
//
//    }

    //======================================================
    //======================_COMMANDS=======================
    //======================================================

    @SupplierOptions("board-value")
    class MessageConvert implements Supplier.Convert<String> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            String[] args = arguments.getArgs();

            String last = args[args.length - 1];

            boolean found = false;

            StringBuilder complete = new StringBuilder();

            int sub = 0;

            for (int i = 0; i < last.length(); i++) {

                char c = last.charAt(i);

                if (c == '{') {
                    found = true;
                    sub = i + 1;
                } else if (found && c == '}') {
                    found = false;
                    complete.setLength(0);
                } else if (found) {
                    complete.append(c);
                }

            }

            if (found) {
                for (String string : PlaceHolderService.instance.complete(complete.toString(), User.class)) {
                    arguments.offerSafe(last.substring(0, sub) + string + "}");
                }
            } else {
                arguments.offer("vazio");
            }

        }

        @Override
        public Result<String> convert(ParameterBuilder builder, Sender sender, String value) {
            if (value.equalsIgnoreCase("vazio")) {
                return new Result<>(StringUtils.fill(Counter.min(entities, item -> item.value, ' '), ' '));
            }

            return new Result<>(StringUtils.toStringWithColors(value));
        }
    }

    @SupplierOptions("item")
    class BoardItemConvert implements Supplier.Convert<BoardItem> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {

            for (int i = 1; i < 16; i++) {
                arguments.offer("line_" + (i < 10 ? '0' : "") + String.valueOf(i));
            }

            arguments.offer("header");
            arguments.offer("tab_header");
            arguments.offer("tab_footer");

        }

        @Override
        public Result<BoardItem> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {

            value = value.toLowerCase();

            if (value.startsWith("line_")) {
                try {
                    int priority = Integer.parseInt(value.substring(5));

                    if (priority > 0 && priority < 16) {
                        return new Result<>(findOrCreate("EMPTY", priority));
                    }

                } catch (Exception ignored) {
                }
            } else {
                switch (value) {
                    case "header":
                        return new Result<>(findOrCreate("EMPTY", -1));
                    case "tab_header":
                        return new Result<>(findOrCreate("EMPTY", -2));
                    case "tab_footer":
                        return new Result<>(findOrCreate("EMPTY", -3));
                    default:
                }
            }

            return new Result.Error("O valor '" + value + "' não foi encontrado!");
        }
    }
}
