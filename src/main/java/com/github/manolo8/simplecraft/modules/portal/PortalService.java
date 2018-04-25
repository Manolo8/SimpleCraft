package com.github.manolo8.simplecraft.modules.portal;

import com.github.manolo8.simplecraft.modules.portal.data.PortalDao;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Location;

import java.util.List;

public class PortalService {

    private final List<Portal> portals;
    private final PortalDao portalDao;

    public PortalService(PortalDao portalDao) {
        this.portalDao = portalDao;
        this.portals = portalDao.findAll();
    }

    public Portal create(User user, String name) {

        Portal portal = portalDao.create(user, name);

        if (portal == null) return null;

        portals.add(portal);

        return portal;
    }

    public Portal getPortal(String name) {
        for (Portal portal : portals)
            if (portal.getName().equals(name))
                return portal;

        return null;
    }

    public void saveAll() {
        for (Portal portal : portals)
            if (portal.isNeedSave()) portalDao.save(portal);
    }

    public Location getPortalDestination(User user, Location from) {

        if (user.isInPvp()) {
            user.sendMessage("§cVocê está em §aPVP! §cEspere um pouco.");
            return null;
        }

        int x = (int) from.getX();
        int y = (int) from.getY();
        int z = (int) from.getZ();

        for (Portal portal : portals) {
            if (portal.getWorldId() != user.getWorldId()) continue;

            if (portal.getPos1().isInArea(x, y, z, 5)) {
                user.sendMessage(portal.getPos2Message());
                return portal.getPos2().getLocation(from.getWorld());
            } else if (portal.getPos2().isInArea(x, y, z, 5)) {
                user.sendMessage(portal.getPos1Message());
                return portal.getPos1().getLocation(from.getWorld());
            }
        }

        return null;
    }
}
