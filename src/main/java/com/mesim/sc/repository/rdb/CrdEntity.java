package com.mesim.sc.repository.rdb;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
@Getter
public class CrdEntity implements Serializable {

    @Column(name = "REG_ID", nullable = false, updatable = false)
    protected String regId;

    @CreationTimestamp
    @Column(name = "REG_DATE", nullable = false, updatable = false)
    protected Timestamp regDate;

    public CrdEntity() {}

    public CrdEntity(String regId) {
        this.regId = regId;
    }
}
