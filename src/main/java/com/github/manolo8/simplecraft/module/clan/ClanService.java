package com.github.manolo8.simplecraft.module.clan;

import com.github.manolo8.simplecraft.core.chat.Chat;
import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolder;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolderBuilder;
import com.github.manolo8.simplecraft.core.placeholder.annotation.PlaceHolderMapping;
import com.github.manolo8.simplecraft.core.service.RepositoryService;
import com.github.manolo8.simplecraft.module.clan.invite.ClanInvite;
import com.github.manolo8.simplecraft.module.clan.user.ClanUser;
import com.github.manolo8.simplecraft.module.clan.view.ClanView;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.utils.def.StringUtils;

import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("unused")
public class ClanService extends RepositoryService<ClanRepository> {

    private final Chat chat;

    public ClanService(ClanRepository repository, Chat chat) {
        super(repository);
        this.chat = chat;
    }

    public Clan create(User owner, String name, String tag, String tagColored) throws SQLException {
        Clan clan = repository.create(name, tag, tagColored);

        ClanUser clanUser = owner.clan();

        clanUser.changeClan(clan, true);
        clanUser.flags().set(0, true);

        return clan;
    }

    public boolean exists(String name) throws SQLException {
        return repository.findByName(name) != null;
    }

    public boolean tagExists(String tag) throws SQLException {
        return repository.findByTag(tag) != null;
    }

    public Clan findByName(String name) throws SQLException {
        return repository.findByName(name);
    }

    public ClanUser findClanUser(Identity identity) throws SQLException {
        return repository.getClanUserRepository().findOneByIdentity(identity);
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================

    private boolean checkTagWrong(User user, String tag) {
        if (StringUtils.validateClanTag(tag)) {
            return false;
        } else {
            user.sendMessage(MessageType.ERROR, "A tag deve ter 3 caracteres e no máximo 3 cores!\n§c(As cores &4,&0,&d estão bloqueados)");
            return true;
        }
    }

    @CmdInfo("clan")
    public void addInfo(Command command) {
        command.setDescription("Comandos úteis do clan");
    }

    @CmdMapping("clan criar <name> <tag>")
    @CmdDescription("Cria um clan")
    @CmdPermission("simplecraft.user")
    @CmdChecker("!isIn")
    public void clanCreate(User user, String name, String tag) throws SQLException {

        if (name.length() < 3 || name.length() > 15) {
            user.sendMessage(MessageType.ERROR, "O nome do clan deve ter entre 3 e 15 caracteres!");
        } else if (exists(name)) {
            user.sendMessage(MessageType.ERROR, "Já existe um clan com esse nome!");
        } else if (!checkTagWrong(user, tag)) {
            String tagClean = StringUtils.removeColors(tag);

            if (tagExists(tagClean)) {
                user.sendMessage(MessageType.ERROR, "Já existe um clan com essa tag!");
            } else {
                create(user, name, tagClean, tag);
                user.sendMessage(MessageType.SUCCESS, "O clan foi criado!");
            }

        }
    }

    @CmdMapping("clan info")
    @CmdDescription("Ver informações do clan")
    @CmdPermission("simplecraft.user")
    @CmdParams(@Param(Clan.class))
    public void clanInfo(User user, Clan clan) {
        user.createView(new ClanView(clan));
    }

    @CmdMapping("clan info <clan>")
    @CmdDescription("Ver informações de outro clan")
    @CmdPermission("simplecraft.user")
    public void clanInfoOther(User user, Clan clan) {
        user.createView(new ClanView(clan));
    }

    @CmdMapping(". <message...>")
    @CmdDescription("Chat do clan")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isIn")
    @CmdParams(@Param(Clan.class))
    public void clanChat(User user, String message, Clan clan) {
        chat.userClanMessage(user, message, clan);
    }

    @CmdMapping("clan set tag <tag>")
    @CmdDescription("Modifica a TAG do clan (Apenas as cores)")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isStaff")
    @CmdParams(@Param(Clan.class))
    public void clanSetTag(User user, String tag, Clan clan) {

        if (!checkTagWrong(user, tag)) {

            String tagClean = StringUtils.removeColors(tag).toLowerCase();

            if (clan.tag.equals(tagClean)) {
                clan.changeColoredTag(tag);
                user.sendMessage(MessageType.SUCCESS, "A tag do clan foi alterada para " + tag + "!");
            } else {
                user.sendMessage(MessageType.ERROR, "Você só pode mudar as cores! (" + tagClean + " diferente de " + clan.getTag() + ")");
            }

        }
    }

    @CmdMapping("clan set ff <boolean>")
    @CmdDescription("Seta se o fogo amigo está ativado ou desativado")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isStaff")
    @CmdParams(@Param(Clan.class))
    public void clanSetFF(User user, boolean ff, Clan clan) {

        clan.changeFriendFire(ff);
        user.sendMessage(MessageType.SUCCESS, "O fogo amigo foi " + (ff ? "ativado" : "desativado") + "!");

    }

    @CmdMapping("clan convidar <user>")
    @CmdDescription("Convida um jogador para o clan")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isStaff")
    @CmdParams(@Param(Clan.class))
    public void clanInvite(User user, ClanUser invited, Clan clan) throws SQLException {

        if (invited.isIn()) {
            user.sendMessage(MessageType.ERROR, "O jogador já está em um CLAN!");
        } else if (clan.freeSlots() <= 0) {
            user.sendMessage(MessageType.ERROR, "O limite de membros no clan é 15 (membros + convites)");
        } else if (clan.getInvite(invited.getIdentity()) != null) {
            user.sendMessage(MessageType.ERROR, "Este jogador já foi convidado para este clan!");
        } else {

            clan.createInvite(invited.getIdentity());

            if (invited.getIdentity().isOnline()) {
                user.sendMessage(MessageType.SUCCESS, "§aConvite foi enviado!");
                invited.getIdentity().sendMessage(MessageType.INFO, "§aVocê foi convidado para o clan " + clan.getName() + ". \nDigite §c/clan aceitar " + clan.getTag() + " §apara entrar!");
            } else {
                user.sendMessage(MessageType.SUCCESS, "§aO convite foi enviado!\n§cNo entanto o jogador não está online.");
            }

        }
    }

    @CmdMapping("clan aceitar <invite>")
    @CmdDescription("Aceita o convite de um clan")
    @CmdChecker("!isIn")
    @CmdPermission("simplecraft.user")
    public void clanInviteAccept(User user, ClanInvite invite) {
        ClanUser clanUser = user.clan();

        Clan clan = invite.getClan();

        clan.removeInvite(invite);
        clanUser.changeClan(clan, true);

        UserService.broadcastAction("§aO jogador " + user.identity().getName() + " entrou no clan " + clan.getTag());

        user.sendMessage(MessageType.SUCCESS, "Você entrou no clan " + clan.getColoredTag() + " com sucesso!");
    }

    @CmdMapping("clan sair")
    @CmdDescription("Sai do clan atual")
    @CmdPermission("simplecraft.user")
    @CmdChecker("canDisband")
    @CmdParams(@Param(Clan.class))
    public void clanResign(User user, Clan clan) {

        ClanUser clanUser = user.clan();

        clan.removeMember(clanUser);
        clanUser.changeClan(null, true);
        user.sendMessage(MessageType.SUCCESS, "Você saiu do clan!");

    }

    @CmdMapping("clan abandonar")
    @CmdDescription("Debanda o clan")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isLeader")
    @CmdParams(@Param(Clan.class))
    public void clanDisband(User user, Clan clan) {

        clan.remove();

        UserService.broadcastAction("§aO clan " + clan.getColoredTag() + "§a foi debandado por " + user.identity().getName());

        user.sendMessage(MessageType.SUCCESS, "O clan foi debandado!");
    }

    @CmdMapping("clan -e <clan> set tag <tag>")
    @CmdDescription("Seta a tag de um CLAN")
    @CmdPermission("simplecraft.admin")
    public void clanSet(Sender sender, Clan clan, String tag) {

        clan.changeColoredTag(tag);
        clan.changeTag(StringUtils.removeColors(tag));

        sender.sendMessage(MessageType.SUCCESS, "A tag foi alterada!");

    }

    @CmdMapping("clan -e <clan> clear")
    @CmdDescription("Remove um clan")
    @CmdPermission("simplecraft.admin")
    public void clanRemove(Sender sender, Clan clan) {

        clan.remove();

        UserService.broadcastAction("§aO clan " + clan.getColoredTag() + "§a foi removido por um ADMIN!");

        sender.sendMessage(MessageType.SUCCESS, "O clan foi removido!");
    }

    @CmdMapping("clan -e <clan> user add <user>")
    @CmdDescription("Coloca um jogador em um clan")
    @CmdPermission("simplecraft.admin")
    public void clanUserAdd(Sender sender, Clan clan, ClanUser target) {

        if (clan.freeSlots() <= 0) {
            sender.sendMessage(MessageType.ERROR, "O limite de membros no clan é 15 (membros + convites)");
        } else if (target.get() == clan) {
            sender.sendMessage(MessageType.ERROR, "O jogador já esta neste clan!");
        } else {
            target.changeClan(clan, true);
        }

    }

    //======================================================
    //=========================TOOLS========================
    //======================================================
    @CheckerOptions("isStaff")
    private boolean isStaff(User user) {
        return user.clan().flags().isStaff();
    }

    @CheckerOptions("isLeader")
    private boolean isLeader(User user) {
        return user.clan().flags().isLeader();
    }

    @CheckerOptions("canDisband")
    private boolean canDisband(User user) {
        return isIn(user) && !isLeader(user);
    }

    @CheckerOptions("isIn")
    private boolean isIn(User user) {
        return user.clan().isIn();
    }

    @SupplierOptions(value = "invite", console = false)
    class ClanInviteConverter implements Supplier.Convert<ClanInvite> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            Identity identity = arguments.sender().user().identity();

            for (ClanInvite invite : repository.getClanInviteRepository().findByIdentity(identity))
                arguments.offer(invite.getClan().getTag());
        }

        @Override
        public Result<ClanInvite> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            value = value.toLowerCase();

            List<ClanInvite> invites = repository.getClanInviteRepository().findByIdentity(sender.user().identity());

            for (ClanInvite invite : invites) {
                if (invite.getClan().getTag().equals(value)) {
                    return new Result<>(invite);
                }
            }

            return new Result.Error("O convite para o clan '" + value + "' não foi encontrado!");
        }
    }

    @SupplierOptions("user")
    class ClanUserConverter implements Supplier.Convert<ClanUser> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.getClanUserRepository().getIdentityRepository().findNames(arguments.getComplete()));
        }

        @Override
        public Result<ClanUser> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            ClanUser clanUser = repository.getClanUserRepository().findOneByIdentity(value);

            if (clanUser == null) return new Result.Error("O jogador " + value + " não foi encontrado!");

            return new Result<>(clanUser);
        }
    }

    @SupplierOptions("clan")
    class ClanConverter implements Supplier.Convert<Clan> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findTags(arguments.getComplete().toLowerCase()));
        }

        @Override
        public Result<Clan> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Clan clan = repository.findByTag(value);

            if (clan == null) return new Result.Error("Clan com a tag '" + value + "' não encontrado");

            return new Result<>(clan);
        }
    }

    @SupplierOptions()
    class BasicClanProvider implements Supplier.Basic<Clan> {

        @Override
        public Result<Clan> provide(Sender sender, Class ignored) {
            Clan clan = sender.user().clan().get();

            if (clan == null) return new Result.Error("Você não está em um clan!");

            return new Result<>(clan);
        }
    }
    //======================================================
    //========================_TOOLS========================
    //======================================================


    //======================================================
    //======================_COMMANDS=======================
    //======================================================


    //======================================================
    //====================PLACE_HOLDERS=====================
    //======================================================

    @PlaceHolderMapping("clan")
    class ClanPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final ClanUser user = target.clan();

                @Override
                public String value() {
                    return user.isIn() ? user.get().getColoredTag() : "...";
                }

                @Override
                public long lastModified() {
                    return user.getLastModified() + (user.isIn() ? user.get().getLastModified() : 1);
                }
            };
        }
    }

    @PlaceHolderMapping("kills")
    class KillPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final ClanUser user = target.clan();

                @Override
                public String value() {
                    return String.valueOf(user.getKills());
                }

                @Override
                public long lastModified() {
                    return user.getKills();
                }
            };
        }
    }

    @PlaceHolderMapping("deaths")
    class DeathPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final ClanUser user = target.clan();

                @Override
                public String value() {
                    return String.valueOf(user.getDeaths());
                }

                @Override
                public long lastModified() {
                    return user.getDeaths();
                }
            };
        }
    }

    @PlaceHolderMapping("kdr")
    class KDRPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final ClanUser user = target.clan();

                @Override
                public String value() {
                    return StringUtils.doubleToString0D(user.getKdr());
                }

                @Override
                public long lastModified() {
                    return user.getDeaths() + user.getKills();
                }
            };
        }
    }

    //======================================================
    //===================_PLACE_HOLDERS=====================
    //======================================================
}
