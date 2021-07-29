package com.mesim.sc.repository.rdb.admin.menu;

import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author sunhye
 * @version 1.0
 * @see <pre>
 * == 개정이력 (Modification Information) ==
 *
 * 수정일    수정자    수정내용
 * -------  -------  ----------------
 * 2020-04-01  sunhye  최초생성
 *
 * </pre>
 * @since 2020-04-01
 */

@Embeddable
@Getter
public class MenuTranslatePk implements Serializable {

    @NotNull
    private int id;

    @NotNull
    private String translateCd;

    public MenuTranslatePk() {}

}
