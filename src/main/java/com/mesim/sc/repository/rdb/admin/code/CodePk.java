package com.mesim.sc.repository.rdb.admin.code;

import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
public class CodePk implements Serializable {

    @NotNull
    private String cd;

    @NotNull
    private String typeCd;

    public CodePk() {}

    public CodePk(String cd, String typeCd){
        this.cd = cd;
        this.typeCd = typeCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodePk taskId = (CodePk) o;
        if (cd != taskId.cd) return false;
        return typeCd == taskId.typeCd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cd, typeCd);
    }
    
}
