package com.mesim.sc.repository.rdb.admin;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class AdminSpecs {

    public static Specification<Object> regId(String regId) {
        return (Specification<Object>) (root, query, cb) -> cb.equal(root.get("regId"), regId);
    }

    public static Specification<Object> regGroupId(int regGroupId) {
        return (root, query, cb) -> {
            Join userJoin = root.join("regUser");
            Join groupJoin = userJoin.join("group");
            return cb.equal(groupJoin.get("id"), regGroupId);
        };
    }

    public static Specification<Object> regGroupNm(String regGroupNm) {
        return (root, query, cb) -> {
            Join userJoin = root.join("regUser");
            Join groupJoin = userJoin.join("group");
            return cb.equal(groupJoin.get("name"), regGroupNm);
        };
    }

    public static Specification<Object> typeCd(String typeCd) {
        return (Specification<Object>) (root, query, cb) -> cb.equal(root.get("typeCd"), typeCd);
    }

    public static Specification<Object> ageRangeCd(String ageRangeCd) {
        return (Specification<Object>) (root, query, cb) -> cb.equal(root.get("ageRangeCd"), ageRangeCd);
    }

    public static Specification<Object> ageRangeCdList(String[] select) {
        Specification<Object> spec = null;

        for (String s : select) {
            String value = s;
            Specification<Object> finalSpec = spec;

            spec = spec == null ?
                    (Specification<Object>) (root, query, cb) -> cb.equal(root.get("ageRangeCd"), value) :
                    Specification.where((root, query, cb) -> cb.equal(root.get("ageRangeCd"), value)).and(finalSpec);
        }
        return spec;
    }

    public static Specification<Object> inspectionCd(String typeCd) {
        return (Specification<Object>) (root, query, cb) -> cb.equal(root.get("inspection_cd"), typeCd);
    }

}
