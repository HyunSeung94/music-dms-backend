package com.mesim.sc.service.admin.code;

import com.mesim.sc.repository.rdb.admin.code.CodeType;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodeTypeDto extends AdminDto {

    private String id;
    private String name;

    public CodeTypeDto() {}

    public CodeTypeDto(CodeType entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.rmk = entity.getRmk();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public CodeType toEntity() {
        return CodeType.builder()
                .id(this.id)
                .name(this.name)
                .rmk(this.rmk)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}