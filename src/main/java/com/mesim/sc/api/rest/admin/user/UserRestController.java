package com.mesim.sc.api.rest.admin.user;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.user.UserDto;
import com.mesim.sc.service.admin.user.UserService;
import com.mesim.sc.service.admin.vocal.VocalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            return new ApiResponseDto(true, ((UserService) service).changePassword(o));
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

    /**
     * 사용자 추가
     *
     * @param dto 등록할 데이터 DTO
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto add(
            @RequestPart(value = "data") Object dto,
            Authentication authentication
    ) throws BackendException {
        try {
            String userId = authentication.getPrincipal().toString();
            return new ApiResponseDto(true, ((UserService) service).add(dto));
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

    /**
     * 사용자 수정
     *
     * @param dto 수정할 데이터 DTO
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.ALL_VALUE)
    public ApiResponseDto update(
            @RequestPart(value = "data") Object dto,
            Authentication authentication
    ) throws BackendException {
        try {
            String userId = authentication.getPrincipal().toString();
            return new ApiResponseDto(true, ((UserService) service).update(dto));
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
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
