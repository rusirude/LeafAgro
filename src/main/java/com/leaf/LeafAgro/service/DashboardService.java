package com.leaf.LeafAgro.service;

import com.leaf.LeafAgro.dto.common.MainMenuDTO;

/**
 * @author : rusiru on 7/6/19.
 */
public interface DashboardService {

    /**
     * Load Menu For Logged User
     *
     * @return MainMenuDTO
     */
    MainMenuDTO loadMainMenu();
}
