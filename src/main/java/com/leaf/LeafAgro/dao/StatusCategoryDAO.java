package com.leaf.LeafAgro.dao;

import com.leaf.LeafAgro.entity.StatusCategoryEntity;

public interface StatusCategoryDAO {

	/**
	 * Find Status Category By Code
	 * @param code
	 * @return {@link StatusCategoryEntity}
	 */
	StatusCategoryEntity findStatusCategoryByCode(String code);
}
