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
        this.defaultSortField = "menuOrd";
        this.joinedSortField = new String[]{"pmenu"};
        this.searchFields = new String[]{"pmenuName", "name"};
        this.excludeColumn = new String[]{"pid", "pName", "children"};
        this.root.put("id", CommonConstants.MENU_ROOT_ID);

        this.addRefEntity("pmenu","name");

        super.init();
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

//            Optional<MsgConvertHistory> optHistory = this.msgConvertHistoryRepository.findById(msgConvertHistoryId);
//
//            if (optHistory.isPresent()) {
//                MsgConvertHistory history = optHistory.get();
//                map.put("trnsId", history.getTrnsId());
//                map.put("data", history.getConvertData());
//            }
            return map;
        } else {
            return null;
        }
    }

}
