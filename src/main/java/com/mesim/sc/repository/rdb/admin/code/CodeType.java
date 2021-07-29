package com.mesim.sc.repository.rdb.admin.code;

import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_CT_COMMONCODETYPE")
public class CodeType extends CrudEntity {

    @Id
    @Column(name = "CD_TYPE")
    private String id;

    @Column(name = "CD_TYPE_NM")
    private String name;

    @Column(name = "RMK")
    protected String rmk;

    @Builder
    public CodeType(String id, String name,
                    String rmk, String regId, String modId) {

        super(regId, modId);

        this.id = id;
        this.name = name;
        this.rmk = rmk;
    }

}