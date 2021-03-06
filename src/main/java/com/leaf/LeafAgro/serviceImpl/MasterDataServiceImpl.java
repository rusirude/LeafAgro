package com.leaf.LeafAgro.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.leaf.LeafAgro.dao.CountryDAO;
import com.leaf.LeafAgro.dto.common.DropDownDTO;
import com.leaf.LeafAgro.enums.DefaultStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leaf.LeafAgro.dao.MasterDataDAO;
import com.leaf.LeafAgro.dto.MasterDataDTO;
import com.leaf.LeafAgro.dto.common.ResponseDTO;
import com.leaf.LeafAgro.entity.MasterDataEntity;
import com.leaf.LeafAgro.enums.ResponseCodeEnum;
import com.leaf.LeafAgro.service.MasterDataService;
import com.leaf.LeafAgro.utility.CommonMethod;

@Service
public class MasterDataServiceImpl implements MasterDataService {
	
	private MasterDataDAO masterDataDAO;
	private CountryDAO countryDAO;
	private CommonMethod commonMethod;
	
	@Autowired
	public MasterDataServiceImpl(MasterDataDAO masterDataDAO, CountryDAO countryDAO, CommonMethod commonMethod) {
		this.masterDataDAO = masterDataDAO;
		this.countryDAO = countryDAO;
		this.commonMethod = commonMethod;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ResponseDTO<HashMap<String, Object>> getReferenceDataForMasterData() {

		HashMap<String, Object> map = new HashMap<>();
		String code = ResponseCodeEnum.FAILED.getCode();
		try {
			List<DropDownDTO> countries = countryDAO.findAllCountryEntities(DefaultStatusEnum.ACTIVE.getCode())
					.stream().map(c -> new DropDownDTO(c.getCode(), c.getDescription()))
					.collect(Collectors.toList());

			map.put("countries", countries);

			code = ResponseCodeEnum.SUCCESS.getCode();
		} catch (Exception e) {
			System.err.println("Country Ref Data Issue");
		}
		return new ResponseDTO<HashMap<String, Object>>(code, map);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ResponseDTO<List<MasterDataDTO>> loadAllMasterData() {
		String code = ResponseCodeEnum.FAILED.getCode();	
		List<MasterDataDTO> masterData = null;
		try {
			masterData = masterDataDAO.findAllMasterDataEntities()
					.stream().map(entity->{
						MasterDataDTO dataDTO = new MasterDataDTO();
						dataDTO.setCode(entity.getCode());
						dataDTO.setValue(entity.getValue());
						return dataDTO;
					}).collect(Collectors.toList());
			code = ResponseCodeEnum.SUCCESS.getCode();
			
		} catch (Exception e) {
			System.err.println("Getting all master data issue");
		}
		return new ResponseDTO<List<MasterDataDTO>>(code, masterData);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ResponseDTO<MasterDataDTO> saveOrUpdateMasterData(List<MasterDataDTO> masterDataDTOS) {
		
		String code = ResponseCodeEnum.FAILED.getCode();
		String description = "Master Data Save Faild";
		try {
			masterDataDTOS.stream().forEach(dto->{
				MasterDataEntity entity = masterDataDAO.findMasterDataEntity(dto.getCode());
				if(entity != null) {
					entity.setValue(String.valueOf(dto.getValue()));
					commonMethod.getPopulateEntityWhenUpdate(entity);
					
					masterDataDAO.updateMasterDataEntity(entity);
				}
				else {
					entity = new MasterDataEntity();
					entity.setCode(dto.getCode());
					entity.setValue(String.valueOf(dto.getValue()));
					commonMethod.getPopulateEntityWhenInsert(entity);
					
					masterDataDAO.saveMasterDataEntity(entity);
				}
			});
			
			code = ResponseCodeEnum.SUCCESS.getCode();
			description = "Master Data Saved Successfully";
			
		} catch (Exception e) {
			System.err.println("Save or Update master data issue");
		}
		return new ResponseDTO<MasterDataDTO>(code, description);
	}

}
