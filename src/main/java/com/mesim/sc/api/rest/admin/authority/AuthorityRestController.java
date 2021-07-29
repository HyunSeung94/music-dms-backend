package com.mesim.sc.api.rest.admin.authority;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.authority.AuthorityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/authority")
public class AuthorityRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("authorityService")
    public void setService(CrudService service) {
        this.name = "권한";
        this.service = service;
    }

    @RequestMapping(value = "listSelect", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getListSelect() throws BackendException {
        try {
            return new ApiResponseDto(true, ((AuthorityService) service).getListSelect());
        } catch(Exception e) {
            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
        }
    }
}
