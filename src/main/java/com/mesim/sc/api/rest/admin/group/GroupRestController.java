package com.mesim.sc.api.rest.admin.group;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.group.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/group")
public class GroupRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("groupService")
    public void setService(CrudService service) {
        this.name = "조직";
        this.service = service;
    }

    @RequestMapping(value = "listSelect", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getListSelect() throws BackendException {
        try {
            return new ApiResponseDto(true, ((GroupService) service).getListSelect());
        } catch (Exception e) {
            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
        }
    }
}
