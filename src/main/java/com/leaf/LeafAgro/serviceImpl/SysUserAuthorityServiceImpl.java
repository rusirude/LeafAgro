package com.leaf.LeafAgro.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leaf.LeafAgro.dao.AuthorityDAO;
import com.leaf.LeafAgro.dao.SysRoleAuthorityDAO;
import com.leaf.LeafAgro.dao.SysUserAuthorityDAO;
import com.leaf.LeafAgro.dao.SysUserDAO;
import com.leaf.LeafAgro.dao.SysUserSysRoleDAO;
import com.leaf.LeafAgro.dto.SysUserAuthorityDTO;
import com.leaf.LeafAgro.dto.SysUserDTO;
import com.leaf.LeafAgro.dto.common.DataTableResponseDTO;
import com.leaf.LeafAgro.dto.common.DropDownDTO;
import com.leaf.LeafAgro.dto.common.ResponseDTO;
import com.leaf.LeafAgro.entity.AuthorityEntity;
import com.leaf.LeafAgro.entity.SysRoleEntity;
import com.leaf.LeafAgro.entity.SysUserAuthorityEntity;
import com.leaf.LeafAgro.entity.SysUserAuthorityEntityId;
import com.leaf.LeafAgro.entity.SysUserEntity;
import com.leaf.LeafAgro.enums.DefaultStatusEnum;
import com.leaf.LeafAgro.enums.ResponseCodeEnum;
import com.leaf.LeafAgro.service.SysUserAuthorityService;

@Service
public class SysUserAuthorityServiceImpl implements SysUserAuthorityService {
	
	;
	private SysUserDAO sysUserDAO;
	private SysRoleAuthorityDAO sysRoleAuthorityDAO;
	private SysUserAuthorityDAO sysUserAuthorityDAO;
	private SysUserSysRoleDAO sysUserSysRoleDAO;
	private AuthorityDAO authorityDAO;

	

	@Autowired
	public SysUserAuthorityServiceImpl(SysUserDAO sysUserDAO,
			SysRoleAuthorityDAO sysRoleAuthorityDAO, SysUserAuthorityDAO sysUserAuthorityDAO, SysUserSysRoleDAO sysUserSysRoleDAO, AuthorityDAO authorityDAO) {		
		this.sysUserDAO =sysUserDAO;
		this.sysRoleAuthorityDAO = sysRoleAuthorityDAO;
		this.sysUserAuthorityDAO = sysUserAuthorityDAO;
		this.sysUserSysRoleDAO = sysUserSysRoleDAO;
		this.authorityDAO = authorityDAO;		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public DataTableResponseDTO getSysUserAuthorityForSysUser(SysUserDTO sysUserDTO) {
		Map<String, SysUserAuthorityDTO> sysUserAuthorityMap = new HashMap<>();
		List<SysUserAuthorityDTO> sysUserAuthorities = new ArrayList<>();
		DataTableResponseDTO responseDTO = new DataTableResponseDTO();
		try {
			List<SysRoleEntity> sysRoleEntities = sysUserSysRoleDAO.getSysUserSysRoleEntitiesBySysUser(sysUserDTO.getUsername())
					.stream()
					.map(_entity -> _entity.getSysRoleEntity())
					.collect(Collectors.toList());
			SysUserEntity sysUserEntity = sysUserDAO.getSysUserEntityByUsername(sysUserDTO.getUsername());

			authorityDAO.findAuthorityEntitiesByStatus(DefaultStatusEnum.ACTIVE.getCode()).stream()
					.forEach(authority -> {
						SysUserAuthorityDTO sysUserAuthorityDTO = new SysUserAuthorityDTO();
						sysUserAuthorityDTO.setUsername(sysUserDTO.getUsername());
						sysUserAuthorityDTO.setTitleDescripton(sysUserEntity.getTitleEntity().getDescription());
						sysUserAuthorityDTO.setName(sysUserEntity.getName());
						sysUserAuthorityDTO.setAuthorityCode(authority.getCode());
						sysUserAuthorityDTO.setAuthorityDescription(authority.getDescription());
						sysUserAuthorityDTO.setEnable(false);

						sysUserAuthorityMap.put(sysUserAuthorityDTO.getAuthorityCode(), sysUserAuthorityDTO);

					});
			
			if(! sysRoleEntities.isEmpty()) {
				sysRoleAuthorityDAO.getSysRoleAuthorityEntitiesBySysRoles(sysRoleEntities).stream()
					.forEach(sysRoleAuthority -> {
						SysUserAuthorityDTO sysUserAuthorityDTO = Optional.ofNullable(sysUserAuthorityMap
								.get(sysRoleAuthority.getAuthorityEntity().getCode())).orElse(null);
						if(sysUserAuthorityDTO != null) {
							sysUserAuthorityDTO.setEnable(true);
							sysUserAuthorityMap.put(sysUserAuthorityDTO.getAuthorityCode(), sysUserAuthorityDTO);
						}
	
					});
			}
			
			sysUserAuthorityDAO.getSysUserAuthorityEntitiesBySysUser(sysUserDTO.getUsername()).stream()
					.forEach(sysUserAuthorty -> {
						SysUserAuthorityDTO sysUserAuthorityDTO = Optional.ofNullable(sysUserAuthorityMap
								.get(sysUserAuthorty.getAuthorityEntity().getCode())).orElse(null);
						if(sysUserAuthorityDTO != null) {
							sysUserAuthorityDTO.setEnable(sysUserAuthorty.getIsGrant()==1);
							sysUserAuthorityMap.put(sysUserAuthorityDTO.getAuthorityCode(), sysUserAuthorityDTO);
						}
					});
			

			sysUserAuthorities = sysUserAuthorityMap.values().stream().collect(Collectors.toList());
			responseDTO.setData(sysUserAuthorities);

		} catch (Exception e) {
			System.err.println("Getting SysUser Authority for Sys User Issue");
			responseDTO.setData(sysUserAuthorities);
		}
		return responseDTO;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ResponseDTO<HashMap<String, Object>> getReferenceDataForSysUserAuthority() {

		HashMap<String, Object> map = new HashMap<>();
		String code = ResponseCodeEnum.FAILED.getCode();
		try {
			
			List<DropDownDTO> sysUser = sysUserDAO.findAllSysUsereEntities(DefaultStatusEnum.ACTIVE.getCode())
					.stream()
					.map(ra-> new DropDownDTO(ra.getUsername(),ra.getUsername())).collect(Collectors.toList());
			
			

			map.put("sysUser", sysUser);

			code = ResponseCodeEnum.SUCCESS.getCode();
		} catch (Exception e) {
			System.err.println("Sys User Authority Ref Data Issue");
		}
		return new ResponseDTO<HashMap<String, Object>>(code, map);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ResponseDTO<SysUserAuthorityDTO> saveSysUserAuthority(SysUserAuthorityDTO sysUserAuthorityDTO){
		String code = ResponseCodeEnum.FAILED.getCode();
		String description = "Sys User Authority Save Faield";
		try {	
			boolean[] isSysRoleAuthority = {false};
			List<SysRoleEntity> sysRoleEntities = sysUserSysRoleDAO.getSysUserSysRoleEntitiesBySysUser(sysUserAuthorityDTO.getUsername())
					.stream()
					.map(_entity -> _entity.getSysRoleEntity())
					.collect(Collectors.toList());
			if(! sysRoleEntities.isEmpty()) {
				sysRoleAuthorityDAO.getSysRoleAuthorityEntitiesBySysRoles(sysRoleEntities).stream()
					.forEach(sysRoleAuthority -> {
						if(sysUserAuthorityDTO.getAuthorityCode().equals(sysRoleAuthority.getAuthorityEntity().getCode())) {
							isSysRoleAuthority[0] = true;
						}
				});
			}
			
			SysUserEntity sysUserEntity = sysUserDAO.getSysUserEntityByUsername(sysUserAuthorityDTO.getUsername());
			AuthorityEntity authorityEntity = authorityDAO.findAuthorityEntityByCode(sysUserAuthorityDTO.getAuthorityCode());
			
			
			if(isSysRoleAuthority[0]){
				sysUserAuthorityDAO.deleteSysUserAuthorityEntityBySysUserAndAuthority(sysUserAuthorityDTO.getUsername(), authorityEntity.getId());
			}
			
			else {
				
				sysUserAuthorityDAO.deleteSysUserAuthorityEntityBySysUserAndAuthority(sysUserAuthorityDTO.getUsername(), authorityEntity.getId());				
				
				SysUserAuthorityEntity sysUserAuthorityEntity = new SysUserAuthorityEntity();
				SysUserAuthorityEntityId id = new SysUserAuthorityEntityId();
				
				id.setSysUser(sysUserAuthorityDTO.getUsername());
				id.setAuthority(authorityEntity.getId());
				
				sysUserAuthorityEntity.setSysUserAuthorityEntityId(id);
				sysUserAuthorityEntity.setSysUserEntity(sysUserEntity);
				sysUserAuthorityEntity.setAuthorityEntity(authorityEntity);
				sysUserAuthorityEntity.setIsGrant(new Long(1));
				
				sysUserAuthorityDAO.saveSysUserAuthorityentity(sysUserAuthorityEntity);				
				
			}
			
			description = "Sys User Authority Save Successfully";
			code = ResponseCodeEnum.SUCCESS.getCode();
		} catch (Exception e) {
			System.err.println("SysRoleAuthority Save Issue");
		}
		return new ResponseDTO<SysUserAuthorityDTO>(code, description);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ResponseDTO<SysUserAuthorityDTO> deleteSysUserAuthority(SysUserAuthorityDTO sysUserAuthorityDTO){
		String code = ResponseCodeEnum.FAILED.getCode();
		String description = "Sys User Authority Delete Faield";
		try {			
			
			boolean[] isSysRoleAuthority = {false};
			List<SysRoleEntity> sysRoleEntities = sysUserSysRoleDAO.getSysUserSysRoleEntitiesBySysUser(sysUserAuthorityDTO.getUsername())
					.stream()
					.map(_entity -> _entity.getSysRoleEntity())
					.collect(Collectors.toList());
			if(! sysRoleEntities.isEmpty()) {
				sysRoleAuthorityDAO.getSysRoleAuthorityEntitiesBySysRoles(sysRoleEntities).stream()
					.forEach(sysRoleAuthority -> {
						if(sysUserAuthorityDTO.getAuthorityCode().equals(sysRoleAuthority.getAuthorityEntity().getCode())) {
							isSysRoleAuthority[0] = true;
						}
				});
			}
			
			SysUserEntity sysUserEntity = sysUserDAO.getSysUserEntityByUsername(sysUserAuthorityDTO.getUsername());
			AuthorityEntity authorityEntity = authorityDAO.findAuthorityEntityByCode(sysUserAuthorityDTO.getAuthorityCode());
			
			
			if(isSysRoleAuthority[0]){
				
				sysUserAuthorityDAO.deleteSysUserAuthorityEntityBySysUserAndAuthority(sysUserAuthorityDTO.getUsername(), authorityEntity.getId());				
				
				SysUserAuthorityEntity sysUserAuthorityEntity = new SysUserAuthorityEntity();
				SysUserAuthorityEntityId id = new SysUserAuthorityEntityId();
				
				id.setSysUser(sysUserAuthorityDTO.getUsername());
				id.setAuthority(authorityEntity.getId());
				
				sysUserAuthorityEntity.setSysUserAuthorityEntityId(id);
				sysUserAuthorityEntity.setSysUserEntity(sysUserEntity);
				sysUserAuthorityEntity.setAuthorityEntity(authorityEntity);
				sysUserAuthorityEntity.setIsGrant(new Long(0));
				
				sysUserAuthorityDAO.saveSysUserAuthorityentity(sysUserAuthorityEntity);	
				
			}
			
			else {
				sysUserAuthorityDAO.deleteSysUserAuthorityEntityBySysUserAndAuthority(sysUserAuthorityDTO.getUsername(), authorityEntity.getId());				
			}
			
			description = "Sys User Authority Delete Successfully";
			code = ResponseCodeEnum.SUCCESS.getCode();
		} catch (Exception e) {
			System.err.println("SysRoleAuthority Delete Issue");
		}
		return new ResponseDTO<SysUserAuthorityDTO>(code, description);
	}



}
