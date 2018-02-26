package com.github.manolo8.simplecraft.data.dao;

import com.github.manolo8.simplecraft.data.dto.RegionDTO;
import com.github.manolo8.simplecraft.domain.region.Region;

import java.util.List;
import java.util.UUID;

public interface RegionDao {

    /**
     * @param id identification
     * @return the region
     * if does not exists, return null
     */
    RegionDTO findOne(Integer id);

    /**
     * @param name name
     * @return an user to this name
     * if does not exits, return null
     */
    RegionDTO findOne(String name);

    /**
     * @param name of the region
     * @return a new region with the
     * name
     */
    RegionDTO create(String name, UUID world);

    /**
     * @param world the world
     * @return a list of region by the world
     */
    List<RegionDTO> findAllByWorld(UUID world);

    /**
     * Remove an region from the database
     *
     * @param id of region
     */
    void delete(Integer id);

    /**
     * Save an user in the database
     * the user with all UUID references
     *
     * @param region the region
     */
    void save(Region region);
}
