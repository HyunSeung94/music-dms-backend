package com.mesim.sc.service.admin;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.repository.rdb.admin.Inspection.InspectionInfoRepository;
import com.mesim.sc.repository.rdb.admin.Inspection.InspectionRepository;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityMenuMapperRepository;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityRepository;
import com.mesim.sc.repository.rdb.admin.code.CodeRepository;
import com.mesim.sc.repository.rdb.admin.code.CodeTypeRepository;
import com.mesim.sc.repository.rdb.admin.consortium.ConsortiumRepository;
import com.mesim.sc.repository.rdb.admin.group.GroupRepository;
import com.mesim.sc.repository.rdb.admin.user.UserRepository;


import com.mesim.sc.repository.rdb.admin.menu.MenuRepository;
import com.mesim.sc.repository.rdb.admin.song.CreativeSongRepository;
import com.mesim.sc.repository.rdb.admin.vocal.VocalRepository;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.Inspection.InspectionDto;
import com.mesim.sc.service.admin.Inspection.InspectionInfoDto;
import com.mesim.sc.service.admin.authority.AuthorityDto;
import com.mesim.sc.service.admin.authority.AuthorityMenuMapperDto;
import com.mesim.sc.service.admin.code.CodeDto;
import com.mesim.sc.service.admin.code.CodeTypeDto;
import com.mesim.sc.service.admin.consortium.ConsortiumDto;
import com.mesim.sc.service.admin.group.GroupDto;
import com.mesim.sc.service.admin.user.UserDto;
import com.mesim.sc.service.admin.menu.MenuDto;
import com.mesim.sc.service.admin.song.CreativeSongDto;
import com.mesim.sc.service.admin.vocal.VocalDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AdminService extends CrudService {

    /**
     * 해당 Repository 에 맞는 Dto 클래스를 반환
     *
     * @return Dto Class
     */
    public Class getClazz() throws BackendException {

        Class clazz = null;

        if (this.repository instanceof CodeTypeRepository) {
            clazz = CodeTypeDto.class;
        } else if (this.repository instanceof CodeRepository) {
            clazz = CodeDto.class;
        } else if (this.repository instanceof MenuRepository) {
            clazz = MenuDto.class;
        } else if (this.repository instanceof GroupRepository) {
            clazz = GroupDto.class;
        } else if (this.repository instanceof UserRepository) {
            clazz = UserDto.class;
        } else if (this.repository instanceof AuthorityRepository) {
            clazz = AuthorityDto.class;
        } else if (this.repository instanceof AuthorityMenuMapperRepository) {
            clazz = AuthorityMenuMapperDto.class;
        } else if (this.repository instanceof CreativeSongRepository) {
            clazz = CreativeSongDto.class;
        } else if (this.repository instanceof VocalRepository) {
            clazz = VocalDto.class;
        } else if (this.repository instanceof InspectionInfoRepository) {
            clazz = InspectionInfoDto.class;
        } else if (this.repository instanceof InspectionRepository) {
            clazz = InspectionDto.class;
        } else if (this.repository instanceof ConsortiumRepository) {
            clazz = ConsortiumDto.class;
        }

        if (clazz == null) {
            throw new BackendException("Dto Class 를 찾을 수 없음");
        }

        return clazz;
    }

}
