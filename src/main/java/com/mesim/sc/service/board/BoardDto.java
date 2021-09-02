package com.mesim.sc.service.board;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.service.CrudDto;
import lombok.Data;

@Data
public abstract class BoardDto extends CrudDto {

    public BoardDto() {}

    public BoardDto(CrudEntity entity) {
        super(entity);
    }

    abstract public Object toEntity();

}
