package com.github.manolo8.simplecraft.module.hologram;

import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.Proximity;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.fake.FakeArmorStand;
import com.github.manolo8.simplecraft.utils.fake.PacketSpawnEntity;
import com.github.manolo8.simplecraft.utils.fake.PacketTeleportEntity;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;

import java.util.ArrayList;
import java.util.List;

public class Hologram extends Container implements Proximity, Tickable {

    private List<FakeArmorStand> armorStands;
    private List<String> lines;
    private boolean changed;

    public Hologram() {
        this.armorStands = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
        refreshLines();
        modified();
    }

    public void addLine(String line) {
        this.lines.add(line);
        refreshLines();
        modified();
    }

    public void updateLine(int pos, String line) {
        this.lines.set(pos, line);
        changed = true;
        modified();
    }

    public void removeLine(int pos) {

        FakeArmorStand stand = this.armorStands.get(pos);

        this.lines.remove(pos);
        this.armorStands.remove(pos);

        if (stand != null) {
            eachNearby(user -> user.sendPacket(new PacketPlayOutEntityDestroy(stand.id)));
        }

        refreshLines();
        modified();
    }

    private void refreshLines() {
        int ar = armorStands.size();
        int li = lines.size();

        double x = area.maxX + 0.5;
        double y = area.maxY - 1.75;
        double z = area.maxZ + 0.5;

        if (li > ar) {

            for (int i = ar; i < li; i++) {

                FakeArmorStand armor = new FakeArmorStand(x, y - (0.25 * i), z);

                armor.setCustomName(lines.get(i));
                armor.setCustomNameVisible(true);
                armor.setInvisible(true);
                armor.setNoGravity(true);

                Packet packet = PacketSpawnEntity.instance.create(armor).packet();

                eachNearby(user -> user.sendPacket(packet));

                armorStands.add(i, armor);
            }
        }

        for (int i = 0; i < armorStands.size(); i++) {

            FakeArmorStand armorStand = armorStands.get(i);

            double ny = y - (0.25 * i);

            if (armorStand.x != x || armorStand.y != ny || armorStand.z != z) {

                Packet packet = PacketTeleportEntity.instance.create(armorStand).packet();

                eachNearby(user -> user.sendPacket(packet));

            }

        }
    }

    /**
     * If an container was multiple chunks, that method can be
     * called multiple times
     *
     * @param user when an user is nearby of this container
     */
    @Override
    public void onNearby(User user) {
        for (FakeArmorStand armor : armorStands) {
            user.sendPacket(PacketSpawnEntity.instance.create(armor).packet());
        }
    }

    /**
     * If an container was multiple chunks, that method can be
     * called multiple times
     *
     * @param user when an user is away from this container
     */
    @Override
    public void onAway(User user) {
        for (FakeArmorStand armor : armorStands) {
            user.sendPacket(new PacketPlayOutEntityDestroy(armor.id));
        }
    }

    @Override
    public void unAttachAndRemove() {
        eachNearby(this::onAway);
        super.unAttachAndRemove();
    }

    @Override
    public void tick() {
        if (changed) {

            changed = false;

            for (int i = 0; i < armorStands.size(); i++) {

                FakeArmorStand armorStand = armorStands.get(i);

                String current = lines.get(i);

                if (current.length() > 256) {
                    current = current.substring(0, 256);
                }

                if (!armorStand.getName().equals(current)) {

                    armorStand.setCustomName(current);

                    final Packet packet = new PacketPlayOutEntityMetadata(armorStand.id, armorStand.datawatcher, true);

                    eachNearby(user -> user.sendPacket(packet));
                }

            }

         }
    }
}
