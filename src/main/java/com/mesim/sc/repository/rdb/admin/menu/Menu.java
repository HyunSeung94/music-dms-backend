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
@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_MENU")
@ToString
public class Menu extends CrudEntity implements Serializable {

    @Id
    @Column(name = "MENU_ID")
    @SequenceGenerator(name = "COL_GEN_MENU_ID_SEQ", sequenceName = "MENU_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_MENU_ID_SEQ")
    private int id;

    @Column(name = "MENU_NM")
    private String name;

    @Column(name = "MENU_PID")
    private int pid;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MENU_PID", referencedColumnName = "MENU_ID", insertable = false, updatable = false)
    private Menu pmenu;

    @Column(name = "URL")
    private String url;

    @Column(name = "ICON")
    private String icon;

    @Column(name = "SORT")
    private int sort;

    @Column(name = "POPUP_YN")
    private int popupYn;

    @Column(name = "RMK")
    protected String rmk;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menu")
    private List<MenuTranslate> translates;

    @Builder
    public Menu(int id, String name, int pid,
                String url, String icon, int sort, int popupYn,
                String rmk, String regId, String modId) {

        super(regId, modId);

        this.id = id;
        this.name = name;
        this.pid = pid;
        this.url = url;
        this.icon = icon;
        this.sort = sort;
        this.popupYn = popupYn;
        this.rmk = rmk;
    }

}
