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
@Entity(name = "TB_ADMIN_HT_USERCONNECT")
public class UserConnect {

    @Id
    @Column(name = "USER_CONNECT_ID")
    @SequenceGenerator(name = "COL_GEN_USER_CONNECT_ID_SEQ", sequenceName = "USER_CONNECT_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_USER_CONNECT_ID_SEQ")
    private int id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NM")
    private String userNm;

    @Column(name = "TYPE_CD")
    private String typeCd;

    @Column(name = "CONNECT_IP")
    private String connectIp;

    @CreationTimestamp
    @Column(name = "CONNECT_DATE")
    private Timestamp connectDate;

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
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'USRCONN'", referencedColumnName = "type_cd"))
    })
    private Code type;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "error_code", referencedColumnName = "cd", insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'USRCONNERR'", referencedColumnName = "type_cd"))
    })
    private Code connError;

    @Builder
    public UserConnect(
            int id,
            String userId,
            String userNm,
            String typeCd,
            String connectIp,
            Timestamp connectDate,
            String errorCode,
            String errorContents,
            int errorYn
    ) {
        this.id = id;
        this.userId = userId;
        this.userNm = userNm;
        this.typeCd = typeCd;
        this.connectIp = connectIp;
        this.connectDate = connectDate;
        this.errorCode = errorCode;
        this.errorContents = errorContents;
        this.errorYn = errorYn;
    }

}
