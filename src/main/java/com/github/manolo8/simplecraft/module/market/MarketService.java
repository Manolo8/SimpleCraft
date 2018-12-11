package com.github.manolo8.simplecraft.module.market;

import com.github.manolo8.simplecraft.core.commands.line.Command;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.service.RepositoryService;
import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.module.market.view.MarketOwnerView;
import com.github.manolo8.simplecraft.module.market.view.MarketView;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class MarketService extends RepositoryService<MarketItemRepository> {

    //SIMPLE REQUEST CACHE
    private long cacheTime = 1000 * 16; //16 segundos
    private long[][] lastRequest = new long[MarketCategory.values().length][32];
    private List[][] lastResults = new List[MarketCategory.values().length][32];
    public MarketService(MarketItemRepository repository) {
        super(repository);
    }
    //SIMPLE REQUEST CACHE

    public List<MarketItem> findByCategory(MarketCategory category, int page) throws SQLException {
        if (page > 31) {
            return new ArrayList<>();
        } else if (lastRequest[category.id][page] > System.currentTimeMillis() - cacheTime) {
            List<MarketItem> items = lastResults[category.id][page];
            items.removeIf(BaseEntity::isRemoved);
            return items;
        } else {
            List items = repository.findByCategory(category, page);

            lastRequest[category.id][page] = System.currentTimeMillis();
            lastResults[category.id][page] = items;

            return items;
        }
    }

    public int countByCategory(MarketCategory category) throws SQLException {
        return findByCategory(category, 0).size();
    }

    public List<MarketItem> findByIdentity(Identity identity, int page) throws SQLException {
        return repository.findByIdentity(identity, page);
    }

    public MarketItem create(User owner, ItemStack item, double cost) throws SQLException {
        return repository.create(owner, item, cost);
    }

    public int countItems(User user) throws SQLException {
        return repository.countByIdentity(user.identity());
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("market")
    public void addEnderchestInfo(Command command) {
        command.setAliases(Collections.singletonList("mercado"));
        command.setDescription("Mercado virtual");
    }

    @CmdMapping("market")
    @CmdDescription("Ver o mercado")
    @CmdPermission("simplecraft.user")
    public void market(User user) {
        user.createView(new MarketView(this));
    }

    @CmdMapping("market vender <value>")
    @CmdDescription("Adiciona uma oferta no mercado do item em sua mão")
    @CmdPermission("simplecraft.user")
    @CmdParams(@Param(ItemStack.class))
    @CmdChecker("canCreateMore")
    public void marketCreate(User user, double cost, ItemStack item) throws SQLException {
        if (item.getType() == Material.AIR) {
            user.sendMessage(MessageType.ERROR, "Item inválido!");
        } else if (cost < 100) {
            user.sendMessage(MessageType.ERROR, "O valor mínimo é 100!");
        } else {
            MarketItem marketItem = create(user, item, cost);
            item.setAmount(0);
            user.sendMessage(MessageType.SUCCESS, "O item agora está a venda na categoria " + marketItem.getCategory().name + "!");
        }
    }

    @CmdMapping("market meu")
    @CmdDescription("Ver os seus itens no mercado")
    @CmdPermission("simplecraft.user")
    public void marketItems(User user) {
        user.createView(new MarketOwnerView(this));
    }

    @CheckerOptions(value = "canCreateMore", message = "Você atingiu o limite de itens! (Expirados também contam)")
    public boolean canCreateMore(User user) throws SQLException {
        return user.getPermissionQuantity("simplecraft.market.quantity") > countItems(user);
    }

    //======================================================
    //======================_COMMANDS=======================
    //======================================================
}
