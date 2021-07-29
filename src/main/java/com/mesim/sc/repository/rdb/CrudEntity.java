package com.mesim.sc.repository.rdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mesim.sc.repository.rdb.admin.group.User;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
@Getter
public class CrudEntity implements Serializable {

    @Column(name = "REG_ID", nullable = false, updatable = false)
    protected String regId;

    @JsonIgnore
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "REG_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    protected User regUser;

    @CreationTimestamp
    @Column(name = "REG_DATE", nullable = false, updatable = false)
    protected Timestamp regDate;

    @Column(name = "MOD_ID")
    protected String modId;

    @JsonIgnore
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MOD_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    protected User modUser;

    @UpdateTimestamp
    @Column(name = "MOD_DATE")
    protected Timestamp modDate;

    public CrudEntity() {}

    public CrudEntity(String regId, String modId) {
        this.regId = regId;
        this.modId = modId;
    }
}
