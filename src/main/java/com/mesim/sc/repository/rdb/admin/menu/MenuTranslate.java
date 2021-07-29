package com.mesim.sc.repository.rdb.admin.menu;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author sunhye
 * @version 1.0
 * @see <pre>
 * == 개정이력 (Modification Information) ==
 *
 * 수정일    수정자    수정내용
 * -------  -------  ----------------
 * 2020-03-31  sunhye  최초생성
 *
 * </pre>
 * @since 2020-03-31
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity(name = "TB_ADMIN_IT_MENUTRANSLATE")
@IdClass(MenuTranslatePk.class)
@ToString
public class MenuTranslate implements Serializable {

    @Id
    @Column(name = "MENU_ID")
    private int id;

    @Id
    @Column(name = "TRANSLATE_CD")
    private String translateCd;

    @Column(name = "MENU_NM")
    private String name;

    @Column(name = "REG_ID")
    private String regId;

    @Column(name = "REG_DATE")
    private Timestamp regDate;

    @Column(name = "MOD_ID")
    private String modId;

    @Column(name = "MOD_DATE")
    private Timestamp modDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MENU_ID", insertable = false, updatable = false)
    private Menu menu;

    @Builder
    public MenuTranslate(int id, String translateCd, String name,
                         String regId, long regDate,
                         String modId, long modDate) {

        this.id = id;
        this.translateCd = translateCd;
        this.name = name;
        this.regId = regId;
        this.regDate = new Timestamp(regDate);
        this.modId = modId;
        this.modDate = new Timestamp(modDate);
    }

}
