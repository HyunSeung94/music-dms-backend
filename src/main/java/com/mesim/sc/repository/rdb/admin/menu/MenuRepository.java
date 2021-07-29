package com.mesim.sc.repository.rdb.admin.menu;

import com.mesim.sc.repository.rdb.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

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

@Repository
@Qualifier("menuRepository")
public interface MenuRepository extends AdminRepository<Menu, Integer> {

    List<Menu> findAllByIdNotOrderByName(int id);

    Menu findByPidAndUrl(int pid, String url);

}
