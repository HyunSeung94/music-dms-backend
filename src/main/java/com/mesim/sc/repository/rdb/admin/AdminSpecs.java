package com.mesim.sc.repository.rdb.admin;

import org.springframework.data.jpa.domain.Specification;

public class AdminSpecs {

    public static Specification<Object> typeCd(String typeCd) {
        return (Specification<Object>) (root, query, cb) -> cb.equal(root.get("typeCd"), typeCd);
    }

    public static Specification<Object> inspectionCd(String typeCd) {
        return (Specification<Object>) (root, query, cb) -> cb.equal(root.get("inspection_cd"), typeCd);
    }

}
