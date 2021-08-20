package com.mesim.sc.repository.rdb.admin.arrange;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import com.mesim.sc.repository.rdb.admin.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Date;


@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_ARRANGE")
@ToString
public class Arrange extends CrudEntity {

    @Id
    @Column(name = "ARRANGE_ID")
    @SequenceGenerator(name = "COL_GEN_ARRANGE_ID_SEQ", sequenceName = "ARRANGE_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_ARRANGE_ID_SEQ")
    private int id;

    @Column(name = "CONTENTS_CD")
    private String contentsCd;

    @Column(name = "ARRANGER_CD")
    private String arrangerCd;

    @Column(name = "ARRANGE_DATE")
    private Date arrangeDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ARRANGER_CD", referencedColumnName = "CONSORTIUM_ID", insertable = false, updatable = false)
    private Consortium arranger;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "REG_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    private User regUser;

    @Builder
    public Arrange(int id, String contentsCd, String arrangerCd, Date arrangeDate, String regId, String modId) {
        super(regId, modId);

        this.id = id;
        this.contentsCd = contentsCd;
        this.arrangerCd = arrangerCd;
        this.arrangeDate = arrangeDate;
    }

}
