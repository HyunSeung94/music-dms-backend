package com.mesim.sc.api.rest.admin.user;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
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
            throw new BackendException(this.name + " 비밀번호 확인 중 오류발생", e);
        }
    }

//    @RequestMapping(value = "connectUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ApiResponseDto connectUser() throws BackendException {
//        try {
//            return new ApiResponseDto(true, ((UserService) service).connectUser());
//        } catch (Exception e) {
//            throw new BackendException("접속 " + this.name + " 목록 조회 중 오류발생", e);
//        }
//    }

}
