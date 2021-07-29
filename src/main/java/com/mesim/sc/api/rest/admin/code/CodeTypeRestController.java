package com.mesim.sc.api.rest.admin.code;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.Inspection.InspectionInfoService;
import com.mesim.sc.service.admin.code.CodeTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/admin/codetype")
public class CodeTypeRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("codeTypeService")
    public void setService(CrudService service) {
        this.name = "공통코드 분류";
        this.service = service;
    }

    @RequestMapping(value = "listSelect", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getListSelect() throws BackendException {
        try {
            return new ApiResponseDto(true, ((CodeTypeService) service).getListSelect());
        } catch(Exception e) {
            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
        }
    }

    /**
     * 상세 조회
     *
     * @param id 상세 조회할 데이터 ID
     * @return 상세 정보
     */
    @RequestMapping(value = "get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto get(@RequestParam(value = "id") String id) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.get(id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 상세 조회 중 오류발생", e);
        }
    }
}
