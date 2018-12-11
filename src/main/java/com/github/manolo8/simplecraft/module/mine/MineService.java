package com.github.manolo8.simplecraft.module.mine;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.module.mine.block.MineBlock;
import com.github.manolo8.simplecraft.module.mine.block.drops.BlockDrop;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import com.github.manolo8.simplecraft.utils.def.PageableList;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Collections;

import static com.github.manolo8.simplecraft.utils.def.StringUtils.doubleToString;

@SuppressWarnings("unused")
public class MineService extends ContainerService<Mine, MineRepository> {

    public MineService(MineRepository repository) {
        super(repository, 2);
    }

    @Override
    public MineProvider initProvider(WorldInfo worldInfo) {
        return new MineProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == Mine.class;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("mine")
    public void addInfo(Command command) {
        command.setAliases(Collections.singletonList("mina"));
        command.setDescription("Comandos úteis das minas");
    }

    @CmdMapping("mine create <name>")
    @CmdDescription("Cria uma mina")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(value = MineProvider.class), @Param(SimpleArea.class)})
    public void mineCreate(User user, String name, MineProvider provider, SimpleArea area) throws SQLException {
        if (provider.exists(name)) {
            user.sendMessage(MessageType.ERROR, "Já existe uma mina com esse nome!");
        } else if (!provider.canAdd(area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível, criar a mina neste local");
        } else {
            provider.create(user.points().asSimpleArea(), name);
            user.sendMessage(MessageType.SUCCESS, "A mina foi criada!");
        }
    }

    @CmdMapping("mine -e <mine> tp")
    @CmdDescription("Teleporta para um local disponível na area")
    @CmdPermission("simplecraft.admin")
    public void mineTp(User user, Mine mine) {
        if (user.teleport(mine)) {
            user.sendMessage(MessageType.SUCCESS, "Teleportando...");
        }
    }

    @CmdMapping("mine -e <mine> clear")
    @CmdDescription("Remove uma mina")
    @CmdPermission("simplecraft.admin")
    public void mineRemove(Sender sender, Mine mine) {
        mine.remove();

        sender.sendMessage(MessageType.SUCCESS, "A mina foi removida com sucesso!");
    }

    @CmdMapping("mine -e <mine> update")
    @CmdDescription("Atualiza a mina")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(MineProvider.class), @Param(SimpleArea.class)})
    public void regionUpdateArea(User user, Mine mine, MineProvider provider, SimpleArea area) {

        if (!provider.canUpdate(mine, area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível atualizar a mina!");
        } else {

            provider.updateArea(mine, area);
            user.sendMessage(MessageType.SUCCESS, "A mina foi atualizada!");
        }

    }

    @CmdMapping("mine -e <mine> reset")
    @CmdDescription("Reseta uma mina")
    @CmdPermission("simplecraft.admin")
    public void mineReset(Sender sender, Mine mine) {
        mine.forceReset();

        sender.sendMessage(MessageType.SUCCESS, "A mina está sendo resetada!");
    }

    @CmdMapping("mine -e <mine> block add <block> <percent>")
    @CmdDescription("Adiciona um bloco na mina")
    @CmdPermission("simplecraft.admin")
    public void mineBlocksAdd(Sender sender, Mine mine, Material block, double percent) throws SQLException {
        if (mine.getBlock(block) != null) {
            sender.sendMessage(MessageType.ERROR, "A mina já tem este bloco!");
        } else if (mine.availablePercent() < percent) {
            sender.sendMessage(MessageType.ERROR, "A mina tem apenas " + mine.availablePercent() + " disponível");
        } else {
            mine.addBlock(block, percent);
            sender.sendMessage(MessageType.SUCCESS, "O bloco foi adicionado");
        }
    }

    @CmdMapping("mine -e <mine> block -e <block> clear")
    @CmdDescription("Remover um bloco da mina")
    @CmdPermission("simplecraft.admin")
    public void mineBlocksRemove(Sender sender, Mine mine, MineBlock mineBlock) {
        mine.removeBlock(mineBlock);

        sender.sendMessage(MessageType.SUCCESS, "O bloco foi removido com sucesso!");
    }

    @CmdMapping("mine -e <mine> block list <?page>")
    @CmdDescription("Ver a lista de blocos da mina")
    @CmdPermission("simplecraft.admin")
    public void mineBlocksList(Sender sender, Mine mine, int pg) {
        PageableList<MineBlock> page = new PageableList(mine.getBlocks(), "§c->  Lista de blocos da mina " + mine.getName(), pg, 10);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append("§7");
            StringUtils.fill(true, 100, current, ' ', builder);
            builder.append("º  §r");
            StringUtils.fill(false, 400, item.getMaterial().name(), '.', builder);
            builder.append(" §7").append(item.getPercent()).append("\n");
        }));
    }

    @CmdMapping("mine -e <mine> block -e <block> drop list <?page>")
    @CmdDescription("Ver a lista de drops da mina")
    @CmdPermission("simplecraft.admin")
    public void mineBlockDropsList(Sender sender, Mine mine, MineBlock block, int pg) {
        PageableList<BlockDrop> page = new PageableList(block.getDrops(), "§c->  Lista de drops do bloco " + block.getMaterial().name(), pg, 10);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append("§7");
            StringUtils.fill(true, 100, current, ' ', builder);
            builder.append("º  §r");
            StringUtils.fill(false, 400, item.getDrop().getType().name(), '.', builder);
            builder.append(" §7").append("1/").append(doubleToString(1 / item.getChance())).append("\n");
        }));
    }

    @CmdMapping("mine -e <mine> block -e <block> drop add <item> <1/x>")
    @CmdDescription("Adiciona um drop ao bloco da mina")
    @CmdPermission("simplecraft.admin")
    public void mineBlockDropsAdd(User user, Mine mine, MineBlock block, ItemStack item, double chance) throws SQLException {
        if (block.getDrop(item.getType().name()) != null) {
            user.sendMessage(MessageType.ERROR, "O bloco já tem este DROP!");
        } else if (block.availableChance() < chance) {
            user.sendMessage(MessageType.ERROR, "O bloco tem apenas " + block.availableChance() + " disponível");
        } else {
            block.addDrop(item, chance);
            user.sendMessage(MessageType.SUCCESS, "O drop foi adicionado a mina " + mine.getName() + "!");
        }
    }

    @CmdMapping("mine -e <mine> block -e <block> drop -e <drop> set chance <1/x>")
    @CmdDescription("Seta a chance de drop de um item")
    @CmdPermission("simplecraft.admin")
    public void mineBlockDropsRemove(Sender sender, Mine mine, MineBlock block, BlockDrop drop, double chance) {
        if (block.availableChance() + drop.getChance() < chance) {
            double available = 1 / (block.availableChance() + drop.getChance());
            sender.sendMessage(MessageType.ERROR, "Há apenas 1/" + available + " disponível!");
        } else {
            drop.setChance(chance);
            block.recalculate();
            sender.sendMessage(MessageType.SUCCESS, "O drop foi alterado!");
        }
    }

    @CmdMapping("mine -e <mine> block -e <block> drop -e <drop> clear")
    @CmdDescription("Remove um drop do bloco da mina")
    @CmdPermission("simplecraft.admin")
    public void mineBlockDropsRemove(Sender sender, Mine mine, MineBlock block, BlockDrop drop) {
        block.removeDrop(drop);
        sender.sendMessage(MessageType.SUCCESS, "O drop foi removido!");
    }
    //======================================================
    //=========================TOOLS========================
    //======================================================

    @SupplierOptions("mine")
    class MineConvert implements Supplier.Convert<Mine> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<Mine> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Mine mine = repository.findByName(value);

            if (mine == null) return new Result.Error("A mina " + value + " não foi encontrada!");

            return new Result(mine);
        }
    }

    @SupplierOptions("block")
    class MineBlockConvert implements Supplier.Convert<MineBlock> {

        @Override
        public void tabComplete(TabArguments arguments) {
            Mine mine = arguments.parameters().getByType(Mine.class);

            if (mine != null) {

                for (MineBlock block : mine.getBlocks())
                    arguments.offer(block.getMaterial().name());
            }
        }

        @Override
        public Result<MineBlock> convert(ParameterBuilder builder, Sender sender, String value) {
            Mine mine = builder.getByType(Mine.class);

            MineBlock block = mine.getBlock(Material.getMaterial(value.toUpperCase()));

            if (block == null) return new Result.Error("O bloco " + value + " não foi encontrado!");

            return new Result(block);
        }
    }

    @SupplierOptions("drop")
    class BlockDropConvert implements Supplier.Convert<BlockDrop> {

        @Override
        public void tabComplete(TabArguments arguments) {
            MineBlock block = arguments.parameters().getByType(MineBlock.class);

            if (block != null) {
                for (BlockDrop drop : block.getDrops())
                    arguments.offer(drop.getDrop().getType().name());
            }
        }

        @Override
        public Result<BlockDrop> convert(ParameterBuilder builder, Sender sender, String value) {
            MineBlock block = builder.getByType(MineBlock.class);

            BlockDrop drop = block.getDrop(value.toUpperCase());

            if (drop == null) return new Result.Error("O drop " + value + " não foi encontrado!");

            return new Result(drop);
        }
    }
    //======================================================
    //========================_TOOLS========================
    //======================================================

    //======================================================
    //======================_COMMANDS=======================
    //======================================================
}
