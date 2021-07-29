package com.mesim.sc.service.admin.menu;

import com.mesim.sc.repository.rdb.admin.menu.Menu;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

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

@Data
public class MenuDto extends AdminDto {

    private int id;
    private String name;
    private int pid;
    private String pName;
    private String url;
    private String icon;
    private int sort;
    private boolean popupYn;

    private List<MenuDto> children;

    public MenuDto() {}

    public MenuDto(int id, String name, int pid, String url) {
        this.id = id;
        this.name = name;
        this.pid = pid;
        this.url = url;
    }

    public MenuDto(Menu entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.pid = entity.getPid();
        this.pName = entity.getPmenu() != null ? entity.getPmenu().getName() : null;
        this.url = entity.getUrl();
        this.icon = entity.getIcon();
        this.sort = entity.getSort();
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
                .name(this.name)
                .pid(this.pid)
                .url(this.url)
                .icon(this.icon)
                .sort(this.sort)
                .popupYn(this.popupYn ? 1 : 0)
                .rmk(this.rmk)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
   }
}
