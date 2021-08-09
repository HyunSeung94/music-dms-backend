package com.mesim.sc.api.rest.admin.consortium;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.consortium.ConsortiumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("api/admin/consortium")
public class ConsortiumController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("consortiumService")
    public void setService(CrudService service) {
        this.name = "컨소시엄별리스트";
        this.service = service;
    }


    @RequestMapping(value = "listSelect", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getListSelect(@RequestParam(value = "groupId") String groupId,
                                        @RequestParam(value = "role") String role) throws BackendException {
        try {
            return new ApiResponseDto(true, ((ConsortiumService) service).getListSelect(groupId,role));
        } catch (Exception e) {
            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "listAllSelect", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getListSelect(@RequestParam(value = "groupId") String groupId) throws BackendException {
        try {
            return new ApiResponseDto(true, ((ConsortiumService) service).getList(groupId));
        } catch (Exception e) {
            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
        }
    }

}
