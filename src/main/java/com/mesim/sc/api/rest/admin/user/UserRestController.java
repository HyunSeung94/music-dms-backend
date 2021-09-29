package com.mesim.sc.api.rest.admin.user;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/user")
public class UserRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("userService")
    public void setService(CrudService service) {
        this.name = "사용자";
        this.service = service;
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto changePassword(@RequestBody Object o) throws BackendException {
        try {
            Object result = ((UserService) service).changePassword(o);
            if (result instanceof ApiResponseDto) {
                return (ApiResponseDto) result;
            }
            return new ApiResponseDto(true, result);
        } catch (Exception e) {
            throw new BackendException(this.name + " 비밀번호 변경 중 오류발생", e);
        }
    }

    // 사용자 전체 조회
    @RequestMapping(value = "listPage/{regId}/{regGroupId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@PathVariable(value = "regId") String regId,
                                  @PathVariable(value = "regGroupId") String regGroupId,
                                  @RequestParam(value = "select", required = false) String[] select,
                                  @RequestParam(value = "index") int index,
                                  @RequestParam(value = "size") int size,
                                  @RequestParam(value = "sortProperties", required = false) String[] sortProperties,
                                  @RequestParam(value = "keywords", required = false) String[] keywords,
                                  @RequestParam(value = "searchOp", required = false) String searchOp,
                                  @RequestParam(value = "fromDate", required = false) String fromDate,
                                  @RequestParam(value = "toDate", required = false) String toDate
    ) throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) this.service).getListPage(regId, regGroupId, select, index, size, sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

    // 아이디 중복 체크
    @RequestMapping(value="duplicate/{id}", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
    public ApiResponseDto checkDuplicated(@PathVariable("id") String id) throws BackendException {
        try {
            boolean result = ((UserService) this.service).isDuplicated(id);
            return new ApiResponseDto(!result, null, "이미 사용중인 ID");
        } catch (Exception e) {
            throw new BackendException(this.name + "중복 체크 중 오류발생", e);
        }
    }

    /**
     * 사용자 승인
     *
     * @return 성공/실패 여부
     */
    @RequestMapping(value="grant/{id}", method = RequestMethod.PUT, produces = MediaType.ALL_VALUE)
    public ApiResponseDto update(@PathVariable("id") String userId) throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) service).grantUser(userId));
        } catch (Exception e) {
            throw new BackendException(this.name + " 승인 중 오류발생", e);
        }
    }

    /**
     * 사용자 승인
     *
     * @return 성공/실패 여부
     */
    @RequestMapping(value="reset-password/{id}", method = RequestMethod.PUT, produces = MediaType.ALL_VALUE)
    public ApiResponseDto resetPassword(@PathVariable("id") String userId) throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) service).resetPassword(userId), "비밀번호가 초기화되었습니다.");
        } catch (Exception e) {
            throw new BackendException(this.name + " 승인 중 오류발생", e);
        }
    }

//    @RequestMapping(value = "confirmPassword", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ApiResponseDto confirmPassword(@RequestBody Object o) throws BackendException {
//        try {
//            return new ApiResponseDto(true, ((UserService) service).confirmPassword(o));
//        } catch (Exception e) {
//            throw new BackendException(this.name + " 비밀번호 확인 중 오류발생", e);
//        }
//    }

//    @RequestMapping(value = "connectUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ApiResponseDto connectUser() throws BackendException {
//        try {
//            return new ApiResponseDto(true, ((UserService) service).connectUser());
//        } catch (Exception e) {
//            throw new BackendException("접속 " + this.name + " 목록 조회 중 오류발생", e);
//        }
//    }

}
