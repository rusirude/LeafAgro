package com.leaf.LeafAgro.service;

import java.util.HashMap;

import com.leaf.LeafAgro.dto.TitleDTO;
import com.leaf.LeafAgro.dto.common.DataTableRequestDTO;
import com.leaf.LeafAgro.dto.common.DataTableResponseDTO;
import com.leaf.LeafAgro.dto.common.ResponseDTO;

public interface TitleService {
	/**
	 * Save Title
	 * @param titleDTO
	 * @return {@link ResponseDTO}
	 */
	ResponseDTO<TitleDTO> saveTitle(TitleDTO titleDTO);
	
	/**
	 * Update Title
	 * @param titleDTO
	 * @return {@link ResponseDTO}
	 */
	ResponseDTO<TitleDTO> updateTitle(TitleDTO titleDTO);
	
	/**
	 * Delete Title
	 * @param titleDTO - code
	 * @return {@link ResponseDTO}
	 */
	ResponseDTO<TitleDTO> deleteTitle(TitleDTO titleDTO);
	
	/**
	 * Find Title By Code
	 * @param titleDTO - code
	 * @return {@link ResponseDTO}
	 */
	ResponseDTO<TitleDTO> findTitle(TitleDTO titleDTO);
	
	/**
	 * Load Reference Data For Title Page
	 * @return {@link ResponseDTO}
	 */
	ResponseDTO<HashMap<String, Object>> getReferenceDataForTitle();
	
	/**
	 * Get Titles Data For Data Table
	 * @param dataTableRequestDTO
	 * @return {@link DataTableResponseDTO}
	 */
	DataTableResponseDTO getTitlesForDataTable(DataTableRequestDTO dataTableRequestDTO);
}
