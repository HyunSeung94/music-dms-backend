package com.mesim.sc.service.admin.authority;

import com.mesim.sc.repository.rdb.admin.authority.Authority;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityMenuMapper;
import com.mesim.sc.service.CrudDto;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthorityMenuMapperDto extends AdminDto {

    private int authorityId;
    private int menuId;
    private String menuNm;
    private int pmenuId;
    private String pmenuNm;
    private boolean searchYn;
    private boolean createYn;
    private boolean updateYn;
    private boolean deleteYn;
    private boolean downloadYn;
    private Authority authority;

    public AuthorityMenuMapperDto() {}

    public AuthorityMenuMapperDto(AuthorityMenuMapper entity) {
        this.authorityId = entity.getAuthorityId();
        this.menuId = entity.getMenuId();

        if (entity.getMenu() != null) {
            this.menuNm = entity.getMenu().getName();
            this.pmenuId = entity.getMenu().getPid();

            if (entity.getMenu().getPmenu() != null) {
                this.pmenuNm = entity.getMenu().getPmenu().getName();
            }
        }

        this.searchYn = entity.getSearchYn() == 1;
        this.createYn = entity.getCreateYn() == 1;
        this.updateYn = entity.getUpdateYn() == 1;
        this.deleteYn = entity.getDeleteYn() == 1;
        this.downloadYn = entity.getDownloadYn() == 1;
        this.authority = entity.getAuthority();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public AuthorityMenuMapper toEntity() {
        return AuthorityMenuMapper.builder()
                .authorityId(this.authorityId)
                .menuId(this.menuId)
                .searchYn(this.searchYn)
                .createYn(this.createYn)
                .updateYn(this.updateYn)
                .deleteYn(this.deleteYn)
                .downloadYn(this.downloadYn)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }
}
