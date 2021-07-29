package com.mesim.sc.service.admin;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.service.CrudDto;
import lombok.Data;

@Data
public abstract class AdminDto extends CrudDto {

    protected String rmk;

    public AdminDto() {}

    public AdminDto(CrudEntity entity) {
        super(entity);
    }

    abstract public Object toEntity();

}
