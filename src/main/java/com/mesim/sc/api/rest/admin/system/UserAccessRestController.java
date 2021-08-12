package com.mesim.sc.api.rest.admin.system;

import com.mesim.sc.annotation.Annotation;
import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.system.UserAccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/admin/useraccess")
public class UserAccessRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("userAccessService")
    public void setService(CrudService service) {
        this.name = "사용자 접근이력";
        this.service = service;
    }

    @Annotation.NoAspect
    @RequestMapping(value = "indivAdd", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto indivAdd(
            @RequestParam(value = "typeCd") String typeCd,
            @RequestParam(value = "apiPath") String apiPath,
            @RequestParam(value = "errorCode", required = false) String errorCode,
            @RequestParam(value = "errorContents", required = false) String errorContents,
            HttpServletRequest request
    ) throws BackendException {
        try {
            if (errorContents == null) {
                return new ApiResponseDto(true, ((UserAccessService) service).save(request, typeCd, apiPath));
            } else {
                return new ApiResponseDto(true, ((UserAccessService) service).save(request, typeCd, apiPath, errorCode, errorContents));
            }
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

}
