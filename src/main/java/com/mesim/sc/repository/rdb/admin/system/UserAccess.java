package com.mesim.sc.repository.rdb.admin.system;

import com.mesim.sc.repository.rdb.admin.code.Code;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_HT_USERACCESS")
public class UserAccess {

    @Id
    @Column(name = "USER_ACCESS_ID")
    @SequenceGenerator(name = "COL_GEN_USER_ACCESS_ID_SEQ", sequenceName = "USER_ACCESS_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_USER_ACCESS_ID_SEQ")
    private int id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NM")
    private String userNm;

    @Column(name = "TYPE_CD")
    private String typeCd;

    @Column(name = "API_PATH")
    private String apiPath;

    @Column(name = "ACCESS_IP")
    private String accessIp;

    @CreationTimestamp
    @Column(name = "ACCESS_DATE")
    private Timestamp accessDate;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "ERROR_CONTENTS")
    private String errorContents;

    @Column(name = "ERROR_YN")
    private int errorYn;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "type_cd", referencedColumnName = "cd", insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'USRACCESS'", referencedColumnName = "type_cd"))
    })
    private Code type;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "error_code", referencedColumnName = "cd", insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'USRACCESSERR'", referencedColumnName = "type_cd"))
    })
    private Code accessError;

    @Builder
    public UserAccess(
            int id,
            String userId,
            String userNm,
            String typeCd,
            String apiPath,
            String accessIp,
            Timestamp accessDate,
            String errorCode,
            String errorContents,
            int errorYn
    ) {
        this.id = id;
        this.userId = userId;
        this.userNm = userNm;
        this.typeCd = typeCd;
        this.apiPath = apiPath;
        this.accessIp = accessIp;
        this.accessDate = accessDate;
        this.errorCode = errorCode;
        this.errorContents = errorContents;
        this.errorYn = errorYn;
    }

}
