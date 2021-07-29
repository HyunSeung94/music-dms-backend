package com.mesim.sc.repository.rdb.admin.authority;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
@NoArgsConstructor
public class AuthorityInfraTypeMapperKey implements Serializable {

    @Column(name = "ROLE_ID")
    private int roleId;

    @Column(name = "INFRA_TYPE_CD")
    private String infraTypeCd;

    public AuthorityInfraTypeMapperKey(int roleId){
        this.roleId = roleId;
    }

    public AuthorityInfraTypeMapperKey(int roleId, String infraTypeCd) {
        this.roleId = roleId;
        this.infraTypeCd = infraTypeCd;
    }
}
