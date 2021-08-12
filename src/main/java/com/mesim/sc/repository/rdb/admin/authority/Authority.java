package com.mesim.sc.repository.rdb.admin.authority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_AUTHORITY")
public class Authority extends CrudEntity {

    @Id
    @Column(name = "AUTHORITY_ID")
    @SequenceGenerator(name = "COL_GEN_AUTHORITY_ID_SEQ", sequenceName = "AUTHORITY_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_AUTHORITY_ID_SEQ")
    private int id;

    @Column(name = "AUTHORITY_NM")
    private String name;


    @Column(name = "RMK")
    protected String rmk;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "authority")
    private List<AuthorityMenuMapper> authorityMenuMappers;

    @Builder
    public Authority(
            int id,
            String name,
            String rmk,
            String regId,
            String modId
    ) {
        super(regId, modId);

        this.id = id;
        this.name = name;
        this.rmk = rmk;
    }

}
