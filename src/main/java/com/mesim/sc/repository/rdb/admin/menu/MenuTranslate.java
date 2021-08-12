package com.mesim.sc.repository.rdb.admin.menu;

import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_MENUTRANSLATE")
@IdClass(MenuTranslatePk.class)
public class MenuTranslate extends CrudEntity implements Serializable {

    @Id
    @Column(name = "MENU_ID")
    private int id;

    @Id
    @Column(name = "TRANSLATE_CD")
    private String translateCd;

    @Column(name = "MENU_NM")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MENU_ID", insertable = false, updatable = false)
    private Menu menu;

    @Builder
    public MenuTranslate(
            int id,
            String translateCd,
            String name,
            String regId,
            String modId
    ) {
        super(regId, modId);

        this.id = id;
        this.translateCd = translateCd;
        this.name = name;
        this.regId = regId;
        this.modId = modId;
    }

}
