package com.mesim.sc.service.admin.menu;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.menu.Menu;
import com.mesim.sc.repository.rdb.admin.menu.MenuRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

@Slf4j
@Service
@Qualifier("menuService")
public class MenuService extends AdminService {


    @Autowired
    @Qualifier("menuRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"name"};
        this.defaultSortField = "sort";
        this.joinedSortField = new String[]{"pmenu"};

        this.addRefEntity("pmenu","name");

        this.excludeColumn = new String[]{"pid", "pName", "children"};

        super.init();
    }

    public Object getListSelect() {
        return ((MenuRepository) this.repository).findAllByIdNotOrderByName(CommonConstants.MENU_ROOT_ID);
    }

    public Map<String, String> getUrl(int id, int msgConvertHistoryId) {
        List<Menu> menuList = ((MenuRepository) this.repository).findAll();
        Optional<Menu> optMenu = menuList.stream().filter(menu -> menu.getId() == id).findFirst();

        if (optMenu.isPresent()) {
            Map<String, String> map = new HashMap<String, String>();

            Menu currentMenu = optMenu.get();
            String url = currentMenu.getUrl();

            while (currentMenu.getPid() != CommonConstants.MENU_ROOT_ID) {
                Menu finalCurrentMenu = currentMenu;
                Optional<Menu> optPMenu = menuList.stream().filter(menu -> menu.getId() == finalCurrentMenu.getPid()).findFirst();

                if (optPMenu.isPresent()) {
                    currentMenu = optPMenu.get();
                    url = currentMenu.getUrl() + "/" + url;
                } else {
                    break;
                }
            }

            map.put("url", url);

            return map;
        } else {
            return null;
        }
    }
}
