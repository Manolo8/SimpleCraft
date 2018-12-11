package com.github.manolo8.simplecraft.module.shop;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.event.block.SignChangeEvent;

import static com.github.manolo8.simplecraft.utils.def.StringUtils.doubleToString;

public class ShopConverter {

    protected int identityId;
    protected int worldId;
    protected double sellPrice;
    protected double buyPrice;
    protected boolean valid;
    protected String status;

    /**
     * @param sign Converte uma sign para um shopping
     * @return Shop
     */
    public ShopConverter convert(User user, SignChangeEvent sign) {
        worldId = user.worldInfo().getId();
        identityId = user.identity().getId();

        //Criando um shopping com nome diferente
        if (!user.identity().getName().equals(sign.getLine(0))) {
            //Verificar se o shopping é de ADMIN
            if (sign.getLine(0).equals("SimpleCraft") && user.isAdmin())
                //Se for admin, coloca o ownerId para -1
                identityId = 1;
                //Caso contrário, atualiza o nome
            else sign.setLine(0, user.identity().getName());
        }

        //Pega os preços de compra e venda
        sellPrice = findPrice('v', sign.getLine(2));
        buyPrice = findPrice('c', sign.getLine(2));

        //Verifica se esta usando ao menos 1:
        if ((sellPrice < 0 && buyPrice < 0) || Double.isNaN(sellPrice) || Double.isNaN(buyPrice)) {
            sign.getBlock().breakNaturally();
            //Se ambos forem menor que 0, a loja é inválida
            status = "§cValores de compra inválidos!";
            valid = false;
            return this;
        }

        String price;

        if (buyPrice >= 0 && sellPrice >= 0) {
            price = "§eC§r " + doubleToString(buyPrice) + ":" + doubleToString(sellPrice) + " §aV";
        } else if (buyPrice >= 0 && sellPrice <= 0) {
            price = "§eC§r " + doubleToString(buyPrice);
        } else {
            price = "§aV§r " + doubleToString(sellPrice);
        }

        sign.setLine(2, price);

        valid = true;
        return this;
    }

    public boolean isValid() {
        return valid;
    }

    public String getStatus() {
        return status;
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
            if (!(Character.isDigit(c) || c == '.')) continue;
            builder.append(c);
        }

        if (!contains) return -1;
        if (builder.length() == 0) return -1;
        return Double.parseDouble(builder.toString());
    }
}
