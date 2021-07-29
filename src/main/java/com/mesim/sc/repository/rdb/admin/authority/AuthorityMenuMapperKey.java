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
public class AuthorityMenuMapperKey implements Serializable {

    @Column(name = "ROLE_ID")
    private int roleId;

    @Column(name = "MENU_ID")
    private int menuId;

    public AuthorityMenuMapperKey(int roleId){
        this.roleId = roleId;
    }

    public AuthorityMenuMapperKey(int roleId, int menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }

}
