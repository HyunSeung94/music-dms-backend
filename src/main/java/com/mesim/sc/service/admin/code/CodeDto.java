package com.mesim.sc.service.admin.code;

import com.mesim.sc.repository.rdb.admin.code.Code;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

@Data
public class CodeDto extends AdminDto {

    private String cd;
    private String name;
    private String typeCd;
    private String typeNm;
    private String refVal1;
    private String refVal2;
    private String refVal3;
    private boolean useYn;

    public CodeDto() {}

    public CodeDto(Code entity) {
        this.cd = entity.getCd();
        this.name = entity.getName();
        this.typeCd = entity.getTypeCd();
        this.typeNm = entity.getType() != null ? entity.getType().getName() : null;
        this.refVal1 = entity.getRefVal1();
        this.refVal2 = entity.getRefVal2();
        this.refVal3 = entity.getRefVal3();
        this.useYn = entity.getUseYn() == 1;
        this.rmk = entity.getRmk();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public Code toEntity() {
        return Code.builder()
                .cd(this.cd)
                .name(this.name)
                .typeCd(this.typeCd)
                .refVal1(this.refVal1)
                .refVal2(this.refVal2)
                .refVal3(this.refVal3)
                .useYn(this.useYn ? 1 : 0)
                .rmk(this.rmk)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }
}