package com.github.manolo8.simplecraft.core.commands.inventory.base;

import com.github.manolo8.simplecraft.core.commands.inventory.Handler;
import com.github.manolo8.simplecraft.core.commands.inventory.InventoryView;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class StringView extends AbstractView {

    private String title;
    private Handler old;

    public StringView(String title) {
        this.title = title;
    }

    public StringView(String title, Handler old) {
        this.title = title;
        this.old = old;
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Handler createHandler() {
        return new StringHandler();
    }

    public abstract void handleName(String name);

    @Override
    public void close() {

    }

    class StringHandler implements Handler<StringView> {

        private Inventory inventory;
        private EntityPlayer player;
        private int windowId;
        private String title;
        private StringView view;

        @Override
        public void prepare(InventoryView main, StringView view) {

            this.view = view;

            player = ((CraftPlayer) main.user().base()).getHandle();

            FakeAnvil container = new FakeAnvil(player);

            windowId = player.nextContainerCounter();
            player.activeContainer = container;
            player.activeContainer.windowId = windowId;
            player.activeContainer.addSlotListener(player);

            inventory = container.getBukkitView().getTopInventory();

            title = view.getTitle();
        }

        @Override
        public void open() {
            player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(windowId, "minecraft:anvil", new ChatComponentText(title), 0));
        }

        @Override
        public void click(int index, ItemStack cursor, boolean ignored) {

            if (index == 2) {

                ItemStack item = inventory.getItem(index);

                if (item != null && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();

                    if (meta.hasDisplayName()) {
                        view.handleName(meta.getDisplayName());
                        main.back(old);
                    }
                }

            } else {
                main.back(old);
            }

        }

        @Override
        public void update(boolean items, boolean pagination) {
            inventory.setItem(0, ItemStackUtils.create(Material.PAPER, "Dê um nome!"));
        }

        class FakeAnvil extends ContainerAnvil {

            public FakeAnvil(EntityHuman entityHuman) {
                super(entityHuman.inventory, entityHuman.world, new BlockPosition(0, 0, 0), entityHuman);
            }


            @Override
            public boolean canUse(EntityHuman entityHuman) {
                return true;
            }
        }
    }
}
