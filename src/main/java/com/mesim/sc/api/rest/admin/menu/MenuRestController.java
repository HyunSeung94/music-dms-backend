package com.mesim.sc.api.rest.admin.menu;


import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.menu.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping("/api/admin/menu")
public class MenuRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("menuService")
    public void setService(CrudService service) {
        this.name = "화면";
        this.service = service;
    }

//    @RequestMapping(value = "listSelect", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ApiResponseDto getListSelect() throws BackendException {
//        try {
//            return new ApiResponseDto(true, ((MenuService) service).getListSelect());
//        } catch (Exception e) {
//            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
//        }
//    }

    @RequestMapping(value = "url", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getUrl(@RequestParam("id") int id,
                                 @RequestParam("msgConvertHistoryId") int msgConvertHistoryId) throws BackendException {
        try {
            return new ApiResponseDto(true, ((MenuService) service).getUrl(id, msgConvertHistoryId));
        } catch (Exception e) {
            throw new BackendException(this.name + " URL 조회 중 오류발생", e);
        }
    }
}
