package com.github.manolo8.simplecraft.module.kit.user;

import com.github.manolo8.simplecraft.module.user.model.identity.BaseIdentity;
import com.github.manolo8.simplecraft.module.kit.Kit;
import com.github.manolo8.simplecraft.module.kit.user.delay.KitDelay;
import com.github.manolo8.simplecraft.module.kit.user.delay.KitDelayRepository;
import com.github.manolo8.simplecraft.utils.entity.LazyLoaderList;

import java.sql.SQLException;

public class KitUser extends BaseIdentity {

    private final KitDelayRepository kitDelayRepository;
    private LazyLoaderList<KitDelay> usedKits;

    public KitUser(KitDelayRepository kitDelayRepository) {
        this.kitDelayRepository = kitDelayRepository;
    }

    public void setUsedKits(LazyLoaderList<KitDelay> usedKits) {
        this.usedKits = usedKits;
    }

    public KitDelay getDelay(Kit kit) throws SQLException {

        for (KitDelay delay : usedKits.get())
            if (delay.getKit() == kit)
                return delay;

        KitDelay delay = kitDelayRepository.create(kit, this);
        usedKits.add(delay);
        return delay;
    }
}
