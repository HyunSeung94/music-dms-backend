package com.mesim.sc.repository.rdb.admin.menu;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("menuTranslateRepository")
public interface MenuTranslateRepository extends AdminRepository<MenuTranslate, MenuTranslatePk> {

    MenuTranslate findByIdAndTranslateCd(int id, String translateCd);

}
