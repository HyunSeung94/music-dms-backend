package com.mesim.sc.service.admin.authority;

import com.mesim.sc.repository.rdb.admin.authority.Authority;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class AuthorityDto extends AdminDto {

    private int id;
    private String name;
    private List<AuthorityInfraTypeMapperDto> roleInfraTypeMapperList;

    public AuthorityDto() {}

    public AuthorityDto(Authority entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.rmk = entity.getRmk();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());

        this.roleInfraTypeMapperList = entity.getAuthorityInfraTypeMappers()
                .stream()
                .map(AuthorityInfraTypeMapperDto::new)
                .collect(Collectors.toList());;
    }

    @Override
    public Authority toEntity(){
        return Authority.builder()
                .id(this.id)
                .name(this.name)
                .rmk(this.rmk)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }
}
