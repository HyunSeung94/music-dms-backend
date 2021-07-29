package com.mesim.sc.api.rest.admin.group;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.security.JwtTokenProvider;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.Inspection.InspectionInfoService;
import com.mesim.sc.service.admin.group.UserService;
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

    @Override
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto add(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) service).add(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto modify(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) service).modify(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 수정 중 오류발생", e);
        }
    }

    @RequestMapping(value = "ids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getIds() throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) service).getIds());
        } catch (Exception e) {
            throw new BackendException(this.name + " 계정 목록 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto changePassword(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) service).changePassword(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 비밀번호 변경 중 오류발생", e);
        }
    }

    @RequestMapping(value = "confirmPassword", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto confirmPassword(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) service).confirmPassword(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 비밀번호 변경 중 오류발생", e);
        }
    }

    @RequestMapping(value="/connect_user", method = RequestMethod.GET)
    public ApiResponseDto logout() {
        try {
            return new ApiResponseDto(true, JwtTokenProvider.userConnectSelect());
        } catch(Exception e) {
            log.error("로그인 사용자 목록 조회 중 오류발생", e);
            return new ApiResponseDto(false, null);
        }
    }

    @RequestMapping(value = "getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@RequestParam(value = "groupId") int groupId) throws BackendException {
        try {
            return new ApiResponseDto(true, ((UserService) service).getListByGroupId(groupId));
        } catch (Exception e) {
            throw new BackendException(this.name + " 목록 조회 중 오류발생", e);
        }
    }
}
