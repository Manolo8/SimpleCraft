package com.github.manolo8.simplecraft.module.user.games;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeView extends BaseView {

    private static ItemStack UP = ItemStackUtils.createSkullByBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTkwYWRlMTBjY2Y0YWQ0YTJhZTZmNjQwMmU2YWUwN2NhMTIyNjJiNmIxZTNjZDZlNjcxNTU5NDUzMTBkYTE3NiJ9fX0=",
            "§eCIMA");
    private static ItemStack DOWN = ItemStackUtils.createSkullByBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWVjYTBmNTVhZjU0NWM0YmE2NDZjMGNkOWVmYzM0MDkwMGJkOWRkNWJiZjRmNTIzMDFkNmQzNjhlMDQ5MTIzIn19fQ===",
            "§eBAIXO");
    private static ItemStack LEFT = ItemStackUtils.createSkullByBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI5M2E2MDcwNTAzMTcyMDcxZjM1ZjU4YzgyMjA0ZTgxOGNkMDY1MTg2OTAxY2ExOWY3ZGFkYmRhYzE2NWU0NCJ9fX0=",
            "§eESQUERDA");
    private static ItemStack RIGHT = ItemStackUtils.createSkullByBase64(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRhNDRjNzY3Y2NhMjU4NjFkM2E1MmZlMTdjMjY0MjhlNjYwZWUyM2RjMGQ3OTNiZjdiZDg2ZWEyMDJmNzAzZCJ9fX0=",
            "§eDIREITA");

    //x(1-9),x(1-5)
    private List<Piece> snake;
    private Piece apple;
    private Random random;
    private int size;
    private int record;
    private boolean ax;
    private boolean di;

    public SnakeView() {
        this.snake = new ArrayList<>();
        this.size = 2;
        this.random = new Random();
        addApple();
    }

    @Override
    public String getTitle() {
        return "Jogo da cobra";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        for (Piece piece : snake) {
            actions.add(new BaseItemAction()
                    .setItem(new ItemStack(Material.STICK))
                    .setIndex(piece.y, piece.x));
        }

        if (apple != null) {
            actions.add(new BaseItemAction()
                    .setItem(new ItemStack(Material.APPLE))
                    .setIndex(apple.y, apple.x));
        }

        actions.add(new BaseItemAction()
                .setItem(UP)
                .setAction(() -> {
                    di = false;
                    ax = false;
                })
                .setIndex(5, 5));
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {

        pagination.add(new BaseItemAction()
                .setItem(DOWN)
                .setIndex(4)
                .setAction(() -> {
                    di = false;
                    ax = true;
                }));

        pagination.add(new BaseItemAction()
                .setItem(LEFT)
                .setIndex(3)
                .setAction(() -> {
                    di = true;
                    ax = false;
                }));

        pagination.add(new BaseItemAction()
                .setItem(RIGHT)
                .setIndex(5)
                .setAction(() -> {
                    di = true;
                    ax = true;
                }));

        pagination.add(new BaseItemAction()
                .setItem(new ItemStack(Material.APPLE, size))
                .setIndex(8));
    }

    @Override
    public void tick() {
        if (UserService.tick % 15 == 0) {
            tick0();
            getMain().update();
        }
    }

    private void tick0() {
        if (snake.size() == 0) {

            snake.add(new Piece(4, 2));

        } else {

            Piece last = snake.get(snake.size() - 1);

            int x = (di ? ax ? last.x + 1 > 9 ? 1 : last.x + 1 : last.x - 1 < 1 ? 9 : last.x - 1 : last.x);
            int y = (!di ? ax ? last.y + 1 > 5 ? 1 : last.y + 1 : last.y - 1 < 1 ? 5 : last.y - 1 : last.y);

            if (apple != null && apple.x == x && apple.y == y) {
                ++size;
                if (size > record) record = size;
                addApple();
                getMain().updatePagination();
            } else if (snake.size() >= size) {
                snake.remove(0);
            }

            for (Piece piece : snake) {
                if (piece.x == x && piece.y == y) {
                    snake.clear();
                    size = 2;
                    getMain().updatePagination();
                    return;
                }
            }

            snake.add(new Piece(x, y));
        }
    }

    private void addApple() {

        int attempts = 10;

        main:
        while (--attempts > 0) {
            int x = random.nextInt(9) + 1;
            int y = random.nextInt(5) + 1;

            if (x != 5 && y != 5) {
                for (Piece piece : snake) {
                    if (piece.x == x && piece.y == y) {
                        continue main;
                    }
                }

                apple = new Piece(x, y);
                break;
            }
        }
    }

    @Override
    public void close() {
        if (record > 10) {
            UserService.broadcastAction("§aO jogador " + user().identity().getName() + " conseguiu um recorde de " + record + " no snake!");
        }
    }

    class Piece {

        private int x;
        private int y;

        public Piece(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
