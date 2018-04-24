package com.github.manolo8.simplecraft.utils;

import com.github.manolo8.simplecraft.modules.region.IWorldRegion;
import com.github.manolo8.simplecraft.modules.region.Region;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;

import java.util.ArrayList;
import java.util.List;

public class RegionUtils {

    /**
     * Ordena paras as regiões filhas
     * Suporta no máximo 1 de depth
     */
    public static List<Region> order(List<Region> regions) {
        List<Region> ordered = new ArrayList<>();

        for (Region region : regions) {

            boolean isSubRegion = false;

            for (Region loop : regions) {

                //Se estiver no mesmo local da memória continua
                if(region == loop) continue;

                if (loop.getArea().isInside(region.getArea())) {
                    loop.addSubRegion(region);

                    System.out.println(loop.getName() + " has an subregion named " + region.getName());

                    isSubRegion = true;
                }
            }

            if(isSubRegion) continue;

            System.out.println(region.getName() + " is not subregion");
            ordered.add(region);
        }

        return ordered;
    }

    public static boolean isAvailable(IWorldRegion iWorldRegion, SimpleArea area) {
        for (Region region : iWorldRegion.getRegions()) {
            if (!region.getArea().isConflicting(area)) continue;
            if (!region.getArea().isInside(area)) return false;
            if (region.notHasSubRegions()) return true;
            for (Region sub : region.getSubRegions())
                if (sub.getArea().isConflicting(area)) return false;
        }
        return true;
    }

    public static void addToIWorldRegion(IWorldRegion iWorldRegion, Region region) {
        for (Region loop : iWorldRegion.getRegions()) {
            if (region.getArea().isInside(loop.getArea())) {
                region.addSubRegion(loop);
                System.out.println(region.getName() + " has an subregion named " + loop.getName());
                return;
            }
        }
        System.out.println(region.getName() + " is not subregion");
        iWorldRegion.addRegion(region);
    }
}
