package com.github.manolo8.simplecraft.domain.warp;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.BaseView;
import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.domain.warp.data.WarpDao;

import java.util.List;

public class WarpService extends BaseView {

    private final WarpDao warpDao;
    private final List<Warp> warps;

    public WarpService(WarpDao warpDao) {
        this.warpDao = warpDao;
        this.warps = warpDao.findAll();
    }

    public void createWarp(User user, String name) {
        Warp warp = warpDao.create(name, user.getBase().getLocation(), user.getWorldId());
        warps.add(warp);
    }

    public void saveAll() {
        for (Warp warp : warps)
            if (warp.isNeedSave())
                warpDao.save(warp);
    }

    public Warp findWarp(String name) {
        for (Warp warp : warps)
            if (warp.getName().equals(name))
                return warp;
        return null;
    }

    @Override
    public String getTitle() {
        return "Warps";
    }

    @Override
    public List<? extends ItemAction> getActions() {
        return warps;
    }
}
