package com.mesim.sc.repository.rdb.admin.authority;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.menu.Menu;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_AUTHORITYMENUMAPPER")
@IdClass(AuthorityMenuMapperPk.class)
public class AuthorityMenuMapper extends CrudEntity {

    @Id
    @Column(name = "AUTHORITY_ID")
    private int authorityId;

    @Id
    @Column(name = "MENU_ID")
    private int menuId;

    @Column(name = "SEARCH_YN")
    private int searchYn;

    @Column(name = "CREATE_YN")
    private int createYn;

    @Column(name = "UPDATE_YN")
    private int updateYn;

    @Column(name = "DELETE_YN")
    private int deleteYn;

    @Column(name = "DOWNLOAD_YN")
    private int downloadYn;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "AUTHORITY_ID", insertable = false, updatable = false)
    private Authority authority;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "MENU_ID", insertable = false, updatable = false)
    private Menu menu;

    @Builder
    public AuthorityMenuMapper(
            int authorityId,
            int menuId,
            boolean searchYn,
            boolean createYn,
            boolean updateYn,
            boolean deleteYn,
            boolean downloadYn,
            String regId,
            String modId
    ) {
        super(regId, modId);

        this.authorityId = authorityId;
        this.menuId = menuId;
        this.searchYn = searchYn ? 1 : 0;
        this.createYn = createYn ? 1 : 0;
        this.updateYn = updateYn ? 1 : 0;
        this.deleteYn = deleteYn ? 1 : 0;
        this.downloadYn = downloadYn ? 1 : 0;
        this.regId = regId;
        this.modId = modId;
    }

}
