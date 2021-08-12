package com.mesim.sc.repository.rdb.admin.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_MENU")
public class Menu extends CrudEntity implements Serializable {

    @Id
    @Column(name = "MENU_ID")
    @SequenceGenerator(name = "COL_GEN_MENU_ID_SEQ", sequenceName = "MENU_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_MENU_ID_SEQ")
    private int id;

    @Column(name = "MENU_PID")
    private int pid;

    @Column(name = "MENU_NM")
    private String name;

    @Column(name = "SORT")
    private Integer sort;

    @Column(name = "URL")
    private String url;

    @Column(name = "ICON")
    private String icon;

    @Column(name = "POPUP_YN")
    private int popupYn;

    @Column(name = "RMK")
    protected String rmk;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MENU_PID", referencedColumnName = "MENU_ID", insertable = false, updatable = false)
    private Menu pmenu;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menu")
    private List<MenuTranslate> translates;

    @Builder
    public Menu(
            int id,
            int pid,
            String name,
            Integer sort,
            String url,
            String icon,
            int popupYn,
            String rmk,
            String regId,
            String modId
    ) {
        super(regId, modId);

        this.id = id;
        this.pid = pid;
        this.name = name;
        this.sort = sort;
        this.url = url;
        this.icon = icon;
        this.popupYn = popupYn;
        this.rmk = rmk;
    }

}
