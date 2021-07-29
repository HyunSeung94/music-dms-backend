package com.mesim.sc.repository.rdb.admin.Inspection;

import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
public class InspectionPk implements Serializable {

    @NotNull
    private String id;

    @NotNull
    private String inspectionId;

    public InspectionPk() {}

    public InspectionPk(String id, String inspectionId) {
        this.id = id;
        this.inspectionId = inspectionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspectionPk taskId = (InspectionPk) o;
        if (id != taskId.id) return false;
        return inspectionId == taskId.inspectionId;
    }



    @Override
    public int hashCode() {
        return Objects.hash(id, inspectionId);
    }
    
}
