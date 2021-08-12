package com.mesim.sc.repository.rdb.admin.menu;

import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
public class MenuTranslatePk implements Serializable {

    @NotNull
    private int id;

    @NotNull
    private String translateCd;

    public MenuTranslatePk() {}

    public MenuTranslatePk(int id, String translateCd) {
        this.id = id;
        this.translateCd = translateCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        MenuTranslatePk taskId = (MenuTranslatePk) o;
        if (id != taskId.id) {
            return false;
        }
        return translateCd.equals(taskId.translateCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, translateCd);
    }

}
