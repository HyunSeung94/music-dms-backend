package com.mesim.sc.api.rest.admin.Inspection;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.Inspection.InspectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/inspection")
public class InspectionRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("inspectionService")
    public void setService(CrudService service) {
        this.name = "창작곡";
        this.service = service;
    }

    
    @RequestMapping(value = "get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto get(@PathVariable("id") String id) throws BackendException {
        try {
            return new ApiResponseDto(true, ((InspectionService) service).get(id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 목록 조회 중 오류발생", e);
        }
    }
    @RequestMapping(value = "saveAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto saveAll(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, ((InspectionService) service).saveAll(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

    @RequestMapping(value = "getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@RequestParam(value = "id") String Id) throws BackendException {
        try {
            return new ApiResponseDto(true, ((InspectionService) service).getListById(Id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 목록 조회 중 오류발생", e);
        }
    }


}
