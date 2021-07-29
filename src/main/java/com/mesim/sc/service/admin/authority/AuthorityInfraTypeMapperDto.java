package com.mesim.sc.service.admin.authority;

import com.mesim.sc.repository.rdb.admin.authority.AuthorityInfraTypeMapper;
import com.mesim.sc.service.CrudDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

@Data
public class AuthorityInfraTypeMapperDto extends CrudDto {

    private int roleId;
    private String infraTypeCd;
    private String infraTypeNm;
    private String areaCds;
    private String[] areaCdList;

    public AuthorityInfraTypeMapperDto() {}

    public AuthorityInfraTypeMapperDto(AuthorityInfraTypeMapper entity) {
        this.roleId = entity.getRoleId();
        this.infraTypeCd = entity.getInfraTypeCd();
        this.infraTypeNm = entity.getInfraType().getName();
        this.areaCds = entity.getAreaCds();
        this.areaCdList = entity.getAreaCds() != null ? entity.getAreaCds().split("#") : null;
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public AuthorityInfraTypeMapper toEntity() {
        return AuthorityInfraTypeMapper.builder()
                .roleId(this.roleId)
                .infraTypeCd(this.infraTypeCd)
                .areaCds(this.areaCds)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }
}
