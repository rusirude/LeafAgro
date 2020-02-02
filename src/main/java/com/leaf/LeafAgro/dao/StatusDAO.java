package com.leaf.LeafAgro.dao;

import com.leaf.LeafAgro.entity.StatusEntity;

public interface StatusDAO {
	
	/**
	 * Find Status Entity By Code
	 * @param code
	 * @return {@link StatusEntity}
	 */
	StatusEntity findStatusEntityByCode(String code);
}
