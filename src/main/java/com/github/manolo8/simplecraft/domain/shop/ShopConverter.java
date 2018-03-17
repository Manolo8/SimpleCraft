package com.github.manolo8.simplecraft.domain.shop;

import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.domain.user.UserService;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.event.block.SignChangeEvent;

public class ShopConverter {

    private User owner;
    private int quantity;
    private double sellPrice;
    private double buyPrice;
    private boolean valid;
    private String status;

    /**
     * @param sign Converte uma sign para um shopping
     * @return Shop
     */
    public ShopConverter convert(User user, SignChangeEvent sign) {
        owner = user;

        //Criando um shopping com nome diferente
        if (!user.getName().equals(sign.getLine(0))) {

            //Verificar se o shopping é de ADMIN
            if (sign.getLine(0).equals("SimpleCraft") && user.hasPermission("shopping.admin"))
                //Se for admin, coloca o ownerId para -1
                owner = UserService.instance.getOfflineUser(-1);
                //Caso contrário, atualiza o nome
            else sign.setLine(0, user.getName());
        }

        quantity = NumberUtils.toShort(sign.getLine(1));

        //Verifica se a quantidade é = 0, ou maior que 64, caso
        //Seja verdade, seta a quantidade para 1 e atualiza
        //As informações
        if (0 >= quantity || 64 > quantity) {
            sign.setLine(1, String.valueOf(1));
            quantity = 1;
        }

        //Pega os preços de compra e venda
        sellPrice = findPrice('s', sign.getLine(2));
        buyPrice = findPrice('b', sign.getLine(2));

        //Verifica se esta usando ao menos 1:
        if (sellPrice < 0 && buyPrice < 0) {
            sign.getBlock().breakNaturally();
            //Se ambos forem menor que 0, a loja é inválida
            status = "Valores de compra inválidos!";
            valid = false;
            return this;
        }

        valid = true;
        return this;
    }

    public boolean isValid() {
        return valid;
    }

    public String getStatus() {
        return status;
    }

    public void update(Shop shop) {
        shop.setBuyPrice(buyPrice);
        shop.setSellPrice(sellPrice);
        shop.setTotalSell(0);
        shop.setTotalBuy(0);
        shop.setOwner(owner);
    }

    /**
     * @param f   char S ou B
     * @param str linha
     * @return o preco encontrado
     */
    private double findPrice(char f, String str) {
        boolean contains = false;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (!contains && Character.toLowerCase(c) == f) contains = true;
            if (c == ' ') continue;
            if (c == ':' && contains) break;
            else if (c == ':') builder.setLength(0);
            if (!Character.isDigit(c)) continue;
            builder.append(c);
        }

        if (!contains) return -1;
        if (builder.length() == 0) return -1;
        return Double.parseDouble(builder.toString());
    }
}
