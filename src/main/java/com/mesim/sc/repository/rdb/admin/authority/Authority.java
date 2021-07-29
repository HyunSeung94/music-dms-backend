package com.mesim.sc.repository.rdb.admin.authority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_ROLE")
@ToString
public class Authority extends CrudEntity {

    @Id
    @Column(name = "ROLE_ID")
    @SequenceGenerator(name = "COL_GEN_ROLE_ID_SEQ", sequenceName = "ROLE_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_ROLE_ID_SEQ")
    private int id;

    @Column(name = "ROLE_NM")
    private String name;


    @Column(name = "RMK")
    protected String rmk;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "authority")
    private List<AuthorityMenuMapper> authorityMenuMappers;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "authority")
    private List<AuthorityInfraTypeMapper> authorityInfraTypeMappers;

    @Builder
    public Authority(int id, String name, String rmk, String regId, String modId) {

        super(regId, modId);

        this.id = id;
        this.name = name;
        this.rmk = rmk;
    }

}
