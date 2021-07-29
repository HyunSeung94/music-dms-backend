package com.mesim.sc.service;

import com.mesim.sc.repository.rdb.CrdEntity;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class CrdDto {

    protected int seq;

    protected String regId;
//    protected String regNm;
    protected String regDate;

    public CrdDto() {}

    public CrdDto(CrdEntity entity) {
        this.regId = entity.getRegId();
//        this.regNm = entity.getRegUser() != null ? entity.getRegUser().getName() : null;
        this.regDate = entity.getRegDate() != null ? DateUtil.toFormat(entity.getRegDate().getTime()) : null;
    }

    abstract public Object toEntity();
}
