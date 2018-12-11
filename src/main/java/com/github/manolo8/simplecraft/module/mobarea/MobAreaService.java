package com.github.manolo8.simplecraft.module.mobarea;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.world.provider.Provider;
import com.github.manolo8.simplecraft.module.mobarea.mobs.Mob;
import com.github.manolo8.simplecraft.module.mobarea.mobs.MobInfo;
import com.github.manolo8.simplecraft.module.mobarea.mobs.item.MobDrop;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import com.github.manolo8.simplecraft.utils.def.PageableList;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Collections;

@SuppressWarnings("unused")
public class MobAreaService extends ContainerService<MobArea, MobAreaRepository> {

    public MobAreaService(MobAreaRepository repository) {
        super(repository, 4);
    }

    @Override
    public Provider<MobArea, MobAreaService> initProvider(WorldInfo worldInfo) {
        return new MobAreaProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == MobArea.class;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("mobarea")
    public void addInfo(Command command) {
        command.setAliases(Collections.singletonList("mobarea"));
        command.setDescription("Comandos úteis das áreas de stored");
    }

    @CmdMapping("mobarea create <name>")
    @CmdDescription("Cria uma area de stored")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(MobAreaProvider.class), @Param(SimpleArea.class)})
    public void mobAreaCreate(User user, String name, MobAreaProvider provider, SimpleArea area) throws SQLException {

        if (provider.exists(name)) {
            user.sendMessage(MessageType.ERROR, "Já existe uma MobArea com esse nome!");
        } else if (!provider.canAdd(area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível, criar a MobArea neste local");
        } else {
            provider.create(area, name);
            user.sendMessage(MessageType.SUCCESS, "A MobArea foi criada!!");
        }

    }

    @CmdMapping("mobarea list <?page>")
    @CmdDescription("Mostra todas as mobarea")
    @CmdPermission("simplecraft.admin")
    public void mobAreaList(Sender sender, int pg) throws SQLException {
        PageableList<MobArea> page = new PageableList(findAll(), "§c->  Lista de MobAreas", pg, 10);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append("§7");
            StringUtils.fill(true, 100, current, ' ', builder);
            builder.append("º  §r");
            StringUtils.fill(false, 400, item.getName(), '.', builder);
            builder.append(" §7Mobs: ").append(item.getMobInfos().size()).append("\n");
        }));
    }

    @CmdMapping("mobarea -e <area> clear")
    @CmdDescription("Remove uma area de stored")
    @CmdPermission("simplecraft.admin")
    public void mobAreaRemove(Sender sender, MobArea area) {
        area.remove();

        sender.sendMessage(MessageType.SUCCESS, "A area de stored foi removida!");
    }

    @CmdMapping("mobarea -e <area> respawn ")
    @CmdDescription("Respawna os stored de uma area")
    @CmdPermission("simplecraft.admin")
    public void mobAreaRespawn(Sender sender, MobArea area) {
        area.respawnMobs(true);

        sender.sendMessage(MessageType.SUCCESS, "Os stored serão respawnados!");
    }

    @CmdMapping("mobarea -e <area> tp")
    @CmdDescription("Teleporta para um local disponível na area")
    @CmdPermission("simplecraft.admin")
    public void mobAreaTp(User user, MobArea area) {
        if (user.teleport(area)) {
            user.sendMessage(MessageType.SUCCESS, "Teleportando...");
        }
    }

    @CmdMapping("mobarea -e <area> mob add <mob> <quantity>")
    @CmdDescription("Adiciona uma mob a area")
    @CmdPermission("simplecraft.admin")
    public void mobAreaAdd(Sender sender, MobArea area, Mob mob, int value) throws SQLException {

        if (area.hasMob(mob)) {
            sender.sendMessage(MessageType.ERROR, "A area já tem esse mob! Troque seu nome!");
        } else if (value > 2000) {
            sender.sendMessage(MessageType.ERROR, "A quantidade não pode ser maior que 2000");
        } else {
            area.create(mob, value);
            area.respawnMobs(false);

            sender.sendMessage(MessageType.SUCCESS, "O mob foi adicionado!");
        }

    }

    @CmdMapping("mobarea -e <area> mob list <?page>")
    @CmdDescription("Mostra todas as mobarea")
    @CmdPermission("simplecraft.admin")
    public void mobAreaList(Sender sender, MobArea area, int pg) {
        PageableList<MobInfo> page = new PageableList(area.getMobInfos(), "§c->  Lista de Mobs da area " + area.getName(), pg, 10);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append("§7");
            StringUtils.fill(true, 100, current, ' ', builder);
            builder.append("º  §r");
            StringUtils.fill(false, 300, item.getName(), '.', builder);
            builder.append(" §7Drops: ").append(item.getDrops().size()).append(" Quantia: ").append(item.getMaxQuantity()).append("\n");
        }));
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> clear")
    @CmdDescription("Remove um mob da area")
    @CmdPermission("simplecraft.admin")
    public void areaRemoveMob(Sender sender, MobArea area, MobInfo mob) {

        area.removeMob(mob);
        area.respawnMobs(true);

        sender.sendMessage(MessageType.SUCCESS, "O mob foi removido!");
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> set quantity <quantity>")
    @CmdDescription("Atualiza a quantidade de stored na area")
    @CmdPermission("simplecraft.admin")
    public void areaMobSetQuantity(Sender sender, MobArea area, MobInfo info, int value) {

        if (value > 2000) {
            sender.sendMessage(MessageType.ERROR, "A quantidade não pode ser maior que 2000");
        } else {

            info.setMaxQuantity(value);
            area.respawnMobs(false);

            sender.sendMessage(MessageType.SUCCESS, "A quantidade de stored foi alterada!");
        }

    }


    @CmdMapping("mobarea -e <area> mob -e <mob> set name <text...>")
    @CmdDescription("Atualiza o nome")
    @CmdPermission("simplecraft.admin")
    public void areaSetMobName(Sender sender, MobArea area, MobInfo info, String name) {

        if (area.getMob(StringUtils.removeColors(name).replaceAll(" ", "").toLowerCase()) != null) {
            sender.sendMessage(MessageType.ERROR, "Já existe um mob com esse nome.");
        } else {

            info.changeName(name);
            area.respawnMobs(true);

            sender.sendMessage(MessageType.SUCCESS, "O nome foi alterado!");
        }

    }

    @CmdMapping("mobarea -e <area> mob -e <mob> set life <value>")
    @CmdDescription("Atualiza a quantidade de vida")
    @CmdPermission("simplecraft.admin")
    public void areaSetMobLife(Sender sender, MobArea area, MobInfo info, int life) {

        if (life <= 0 || life > 2048) {
            sender.sendMessage(MessageType.ERROR, "A vida deve ser entre 1 e 2048");
        } else {
            info.setLife(life);
            area.respawnMobs(true);
            sender.sendMessage(MessageType.SUCCESS, "A vida foi alterada!");
        }
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> set range <value>")
    @CmdDescription("Atualiza a distância de perseguição")
    @CmdPermission("simplecraft.admin")
    public void areaSetMobRange(Sender sender, MobArea area, MobInfo info, int range) {

        info.setRange(range);
        area.respawnMobs(true);

        sender.sendMessage(MessageType.SUCCESS, "O range foi alterado!");
    }


    @CmdMapping("mobarea -e <area> mob -e <mob> set speed <value>")
    @CmdDescription("Atualiza a velocidade")
    @CmdPermission("simplecraft.admin")
    public void areaSetMobSpeed(Sender sender, MobArea area, MobInfo info, double speed) {

        info.setSpeed(speed);
        area.respawnMobs(true);

        sender.sendMessage(MessageType.SUCCESS, "A velocidade foi alterada!");
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> set damage <value>")
    @CmdDescription("Atualiza o dano")
    @CmdPermission("simplecraft.admin")
    public void areaSetMobDamage(Sender sender, MobArea area, MobInfo info, int damage) {

        info.setDamage(damage);
        area.respawnMobs(true);

        sender.sendMessage(MessageType.SUCCESS, "O dano foi alterado!");
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> set exp <value>")
    @CmdDescription("Atualiza a quantidade exp dropada (skill)")
    @CmdPermission("simplecraft.admin")
    public void areaSetMobExp(Sender sender, MobArea area, MobInfo info, int exp) {

        info.setExp(exp);
        area.respawnMobs(true);

        sender.sendMessage(MessageType.SUCCESS, "A experiência foi alterada!");
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> drop add <item> <percent>")
    @CmdDescription("Adiciona um drop ao mob")
    @CmdPermission("simplecraft.admin")
    public void areaDropAdd(Sender sender, MobArea area, MobInfo info, ItemStack item, double percent) throws SQLException {

        MobDrop drop = info.getDrop(item.getType().name());

        if (drop != null) {
            sender.sendMessage(MessageType.ERROR, "O mob já tem este DROP!");
        } else if (info.availableChance() < percent) {
            sender.sendMessage(MessageType.ERROR, "O mob tem apenas " + info.availableChance() + " disponível");
        } else {

            info.addDrop(item, percent);
            sender.sendMessage(MessageType.SUCCESS, "Drop adicionado!");
        }

    }


    @CmdMapping("mobarea -e <area> mob -e <mob> drop -e <drop> clear ")
    @CmdDescription("Remove um drop do mob")
    @CmdPermission("simplecraft.admin")
    public void areaDropRemove(Sender sender, MobArea area, MobInfo info, MobDrop drop) {

        info.removeDrop(drop);
        sender.sendMessage(MessageType.SUCCESS, "O drop foi removido!");
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> drop -e <drop> get ")
    @CmdDescription("Pega um drop")
    @CmdPermission("simplecraft.admin")
    public void areaDropGet(User user, MobArea area, MobInfo info, MobDrop drop) {

        user.base().getInventory().addItem(drop.getItem().clone());
        user.sendMessage(MessageType.SUCCESS, "Drop recebido!");
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> drop -e <drop> set chance <percent>")
    @CmdDescription("Remove um drop do mob")
    @CmdPermission("simplecraft.admin")
    public void areaMobSetChance(Sender sender, MobArea area, MobInfo info, MobDrop drop, double percent) {

        if (info.availableChance() + drop.getChance() < percent) {
            sender.sendMessage(MessageType.ERROR, "A chance disponível é " + (info.availableChance() + drop.getChance()));
        } else {
            drop.setChance(percent);
            sender.sendMessage(MessageType.SUCCESS, "O drop foi alterado!");
        }
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> drop -e <drop> set amount <value>")
    @CmdDescription("Remove um drop do mob")
    @CmdPermission("simplecraft.admin")
    public void areaMobSetAmount(Sender sender, MobArea area, MobInfo info, MobDrop drop, int amount) {

        drop.getItem().setAmount(amount);
        drop.modified();
        sender.sendMessage(MessageType.SUCCESS, "A quantidade foi alterada!");
    }

    @CmdMapping("mobarea -e <area> mob -e <mob> drop list <?page>")
    @CmdDescription("Ver a lista de drops do mob")
    @CmdPermission("simplecraft.admin")
    public void mineBlockDropsList(Sender sender, MobArea area, MobInfo info, int pg) {
        PageableList<MobDrop> page = new PageableList(info.getDrops(), "§c->  Lista de drops do mob " + info.getDisplayName(), pg, 10);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append("§7");
            StringUtils.fill(true, 100, current, ' ', builder);
            builder.append("º  §r");
            StringUtils.fill(false, 400, item.getItem().getType().name(), '.', builder);
            builder.append(" §7").append(item.getChance()).append("\n");
        }));
    }

    //======================================================
    //=======================REGISTRY=======================
    //======================================================

    @SupplierOptions("area")
    class MobAreaConverter implements Supplier.Convert<MobArea> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<MobArea> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {

            MobArea area = repository.findByName(value);

            if (area == null) return new Result.Error("A area com o nome " + value + " não foi encontrada!");

            return new Result(area);
        }
    }

    @SupplierOptions("mob")
    class MobConverter implements Supplier.Convert<Mob> {

        @Override
        public void tabComplete(TabArguments arguments) {

            MobArea mobArea = arguments.parameters().getByType(MobArea.class);

            if (mobArea != null) {
                for (Mob mob : repository.getMobInfoRepository().getMobs()) {
                    arguments.offer(mob.getName());
                }
            }
        }

        @Override
        public Result<Mob> convert(ParameterBuilder builder, Sender sender, String value) {
            Mob mob = repository.getMobInfoRepository().findMob(value);

            if (mob == null) return new Result.Error("O mob " + value + " não foi encontrado!");

            return new Result<>(mob);
        }
    }

    @SupplierOptions("mob")
    class MobInfoConverter implements Supplier.Convert<MobInfo> {

        @Override
        public void tabComplete(TabArguments arguments) {
            MobArea mobArea = arguments.parameters().getByType(MobArea.class);

            if (mobArea != null) {
                for (MobInfo info : mobArea.getMobInfos()) {
                    arguments.offer(info.getFastName());
                }
            }
        }

        @Override
        public Result<MobInfo> convert(ParameterBuilder builder, Sender sender, String value) {
            MobArea mobArea = builder.getByType(MobArea.class);

            MobInfo info = mobArea.getMob(value);

            if (info == null) return new Result.Error("A area não tem o Mob " + value + "!");

            return new Result(info);
        }
    }

    @SupplierOptions("drop")
    class MobDropConverter implements Supplier.Convert<MobDrop> {

        @Override
        public void tabComplete(TabArguments arguments) {

            MobInfo info = arguments.parameters().getByType(MobInfo.class);

            if (info != null) {
                for (MobDrop drop : info.getDrops()) {
                    arguments.offer(drop.getItem().getType().name());
                }
            }
        }

        @Override
        public Result<MobDrop> convert(ParameterBuilder builder, Sender sender, String value) {

            MobInfo info = builder.getByType(MobInfo.class);

            MobDrop drop = info.getDrop(value.toUpperCase());

            if (drop == null) return new Result.Error("O drop '" + value + "' não foi encontrado");

            return new Result<>(drop);
        }
    }
    //======================================================
    //======================_REGISTRY=======================
    //======================================================


    //======================================================
    //======================_COMMANDS=======================
    //======================================================
}