package com.github.manolo8.simplecraft.module.skill;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.RepositoryService;
import com.github.manolo8.simplecraft.module.skill.user.SkillUser;
import com.github.manolo8.simplecraft.module.skill.view.SkillView;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;

import java.sql.SQLException;
import java.util.Collections;

@SuppressWarnings("unused")
public class SkillService extends RepositoryService<SkillRepository> {

    public SkillService(SkillRepository skillRepository) {
        super(skillRepository);
    }

    public void create(User user, int type) throws SQLException {
        Skill skill = repository.create(user.identity(), type);

        user.skill().add(skill);
    }

    public Skill[] getTypes() {
        return repository.getTypes();
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("skill")
    public void addInfo(Command command) {
        command.setAliases(Collections.singletonList("skills"));
        command.setDescription("Comandos úteis das skills");
    }

    @CmdMapping("skill")
    @CmdDescription("Ver suas skills")
    @CmdPermission("simplecraft.user")
    public void skill(User user) {
        user.createView(new SkillView(this));
    }

    @CmdMapping("skill user <user> set level <value>")
    @CmdDescription("Seta o level de um jogador")
    @CmdPermission("simplecraft.admin")
    public void skillSetLevel(Sender sender, SkillUser user, int level) {

        user.setLevel(level);
        sender.sendMessage(MessageType.SUCCESS, "Alteração feita com sucesso!");

    }

    @CmdMapping("skill user <user> give level <value>")
    @CmdDescription("Da level de skill para um jogador")
    @CmdPermission("simplecraft.admin")
    public void skillGiveLevel(Sender sender, SkillUser user, int level) {

        user.setLevel(user.getLevel() + level);
        sender.sendMessage(MessageType.SUCCESS, "Alteração feita com sucesso!");

    }

    @CmdMapping("skill user <user> give exp <value>")
    @CmdDescription("Da exp de skill para um jogador")
    @CmdPermission("simplecraft.admin")
    public void skillGiveExp(Sender sender, SkillUser user, int exp) {

        user.giveExp(exp);
        sender.sendMessage(MessageType.SUCCESS, "Alteração feita com sucesso!");

    }

    @CmdMapping("skill user <user> clear")
    @CmdDescription("Remove todas as skills de um jogador")
    @CmdPermission("simplecraft.admin")
    public void skillClear(Sender sender, SkillUser user) {

        user.clearSkills();
        sender.sendMessage(MessageType.SUCCESS, "As skills foram removidas!");

    }


    //======================================================
    //=========================TOOLS========================
    //======================================================


    //======================================================
    //========================_TOOLS========================
    //======================================================

    @SupplierOptions("user")
    class SkillUserConverter implements Supplier.Convert<SkillUser> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.getSkillUserRepository().getIdentityRepository().findNames(arguments.getComplete()));
        }

        @Override
        public Result<SkillUser> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            SkillUser user = repository.getSkillUserRepository().findOneByIdentity(value);

            if (user == null) return new Result.Error("O jogador não foi encontrado");

            return new Result(user);
        }
    }

    //======================================================
    //======================_COMMANDS=======================
    //======================================================
}
