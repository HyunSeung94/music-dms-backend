package com.mesim.sc.service;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class CrudDto {

    protected int seq;

    protected String regId;
    protected String regNm;
    protected String regDate;
    protected String modId;
    protected String modNm;
    protected String modDate;

    public CrudDto() {}

    public CrudDto(CrudEntity entity) {
        this.regId = entity.getRegId();
        this.regNm = entity.getRegUser() != null ? entity.getRegUser().getName() : null;
        this.regDate = entity.getRegDate() != null ? DateUtil.toFormat(entity.getRegDate().getTime()) : null;
        this.modId = entity.getModId();
        this.modNm = entity.getModUser() != null ? entity.getModUser().getName() : null;
        this.modDate = entity.getModDate() != null ? DateUtil.toFormat(entity.getModDate().getTime()) : null;
    }

    abstract public Object toEntity();
}
