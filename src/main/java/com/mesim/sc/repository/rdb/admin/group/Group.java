package com.mesim.sc.repository.rdb.admin.group;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.code.Code;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_GROUP")
@ToString
public class Group extends CrudEntity {

    @Id
    @Column(name = "GROUP_ID")
    @SequenceGenerator(name = "COL_GEN_GROUP_ID_SEQ", sequenceName = "GROUP_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_GROUP_ID_SEQ")
    private int id;

    @Column(name = "GROUP_NM")
    private String name;

    @Column(name = "GROUP_PID")
    private int pid;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "GROUP_PID", referencedColumnName = "GROUP_ID", insertable = false, updatable = false)
    private Group pgroup;

    @Column(name = "TYPE_CD")
    private String typeCd;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "TYPE_CD", referencedColumnName = "CD", insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'GRPTYPE'", referencedColumnName = "TYPE_CD"))
    })
    private Code type;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ZIP_CODE")
    private Integer zipCode;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "RMK")
    protected String rmk;

    @Builder
    public Group(int id, String name, int pid, String typeCd,
                 String phone, Integer zipCode, String address,
                 String rmk, String regId, String modId) {

        super(regId, modId);

        this.id = id;
        this.name = name;
        this.pid = pid;
        this.typeCd = typeCd;
        this.phone = phone;
        this.zipCode = zipCode;
        this.address = address;
        this.rmk = rmk;
    }
    
}
