package com.github.manolo8.simplecraft.module.hologram;

import com.github.manolo8.simplecraft.core.commands.line.ParameterBuilder;
import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.core.commands.line.Sender;
import com.github.manolo8.simplecraft.core.commands.line.TabArguments;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.world.provider.Provider;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;

import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("unused")
public class HologramService extends ContainerService<Hologram, HologramRepository> {

    public HologramService(HologramRepository repository) {
        super(repository, 6);
    }

    @Override
    public Provider<Hologram, ?> initProvider(WorldInfo worldInfo) {
        return new HologramProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == Hologram.class && entity.getId() != null;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdMapping("hologram create <name>")
    @CmdDescription("Cria um holograma na sua posição atual")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(HologramProvider.class), @Param(SimpleLocation.class)})
    public void create(User user, String name, HologramProvider provider, SimpleLocation location) throws SQLException {

        if (exists(name)) {
            user.sendMessage(MessageType.ERROR, "Já existe um holograma com esse nome!");
        } else {
            provider.create(new SimpleArea(location, location), name).addLine("/hologram -e " + name + "!");
            user.sendMessage(MessageType.SUCCESS, "O holograma foi criado!");
        }

    }

    @CmdMapping("hologram -e <hologram> remove")
    @CmdDescription("Remove um holograma")
    @CmdPermission("simplecraft.admin")
    public void remove(Sender sender, Hologram hologram) {

        hologram.remove();
        sender.sendMessage(MessageType.SUCCESS, "O holograma foi removido!");

    }

    @CmdMapping("hologram -e <hologram> add <text...>")
    @CmdDescription("Adiciona uma linha ao holograma")
    @CmdPermission("simplecraft.admin")
    public void hologramAddLine(User user, Hologram hologram, String value) {

        hologram.addLine(value);
        user.sendMessage(MessageType.SUCCESS, "A linha foi adicionada!");

    }

    @CmdMapping("hologram -e <hologram> -e <line> set <text...>")
    @CmdDescription("Edita o valor de um holograma")
    @CmdPermission("simplecraft.admin")
    public void hologramSetLine(User user, Hologram hologram, int line, String value) {

        hologram.updateLine(line, value);
        user.sendMessage(MessageType.SUCCESS, "A linha foi atualizada!");

    }

    @CmdMapping("hologram -e <hologram> -e <line> remove")
    @CmdDescription("Remove uma linha do holograma")
    @CmdPermission("simplecraft.admin")
    public void hologramRemoveLine(User user, Hologram hologram, int line) {

        hologram.removeLine(line);
        user.sendMessage(MessageType.SUCCESS, "A linha foi removida!");
    }

    @SupplierOptions("hologram")
    class HologramConvert implements Supplier.Convert<Hologram> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<Hologram> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {

            Hologram hologram = repository.findByName(value);

            if (hologram != null) {
                return new Result<>(hologram);
            } else {
                return new Result.Error("O holograma '" + value + "' não foi encontrado!");
            }
        }
    }

    @SupplierOptions("line")
    class LineConvert implements Supplier.Convert<Integer> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            Hologram hologram = arguments.parameters().getByType(Hologram.class);

            List values = hologram.getLines();

            for (int i = 0; i < values.size(); i++) {
                arguments.offer(String.valueOf(i + 1));
            }

        }

        @Override
        public Result<Integer> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            try {
                int line = Integer.parseInt(value) - 1;

                List values = builder.getByType(Hologram.class).getLines();

                if (line >= 0 && values.size() > line) {
                    return new Result<>(line);
                }

            } catch (Exception ignored) {

            }
            return new Result.Error("A linha '" + value + "' não foi encontrada!");
        }
    }
    //======================================================
    //======================_COMMANDS=======================
    //======================================================
}
