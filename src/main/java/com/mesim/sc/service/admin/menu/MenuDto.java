package com.mesim.sc.service.admin.menu;

import com.mesim.sc.repository.rdb.admin.menu.Menu;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MenuDto extends AdminDto {

    private int id;
    private int pid;
    private String pName;
    private String name;
    private Integer sort;
    private String url;
    private String icon;
    private boolean popupYn;

    private List<MenuDto> children;

    public MenuDto() {}

    public MenuDto(Menu entity) {
        this.id = entity.getId();
        this.pid = entity.getPid();
        this.pName = entity.getPmenu() != null ? entity.getPmenu().getName() : null;
        this.name = entity.getName();
        this.sort = entity.getSort();
        this.url = entity.getUrl();
        this.icon = entity.getIcon();
        this.popupYn = entity.getPopupYn() == 1;
        this.rmk = entity.getRmk();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

   public Menu toEntity() {
        return Menu.builder()
                .id(this.id)
                .pid(this.pid)
                .name(this.name)
                .sort(this.sort)
                .url(this.url)
                .icon(this.icon)
                .popupYn(this.popupYn ? 1 : 0)
                .rmk(this.rmk)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
   }

}
