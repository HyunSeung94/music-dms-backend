package com.mesim.sc.repository.rdb.admin.authority;

import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
public class AuthorityMenuMapperPk implements Serializable {

    @NotNull
    private int authorityId;

    @NotNull
    private int menuId;

    public AuthorityMenuMapperPk() {}

    public AuthorityMenuMapperPk(int authorityId, int menuId) {
        this.authorityId = authorityId;
        this.menuId = menuId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        AuthorityMenuMapperPk taskId = (AuthorityMenuMapperPk) o;
        if (authorityId != taskId.authorityId) {
            return false;
        }
        return menuId == taskId.menuId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId, menuId);
    }

}
