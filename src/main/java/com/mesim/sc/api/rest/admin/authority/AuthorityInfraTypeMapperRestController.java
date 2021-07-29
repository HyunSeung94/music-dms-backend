package com.mesim.sc.api.rest.admin.authority;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.authority.AuthorityInfraTypeMapperService;
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
@RequestMapping("/api/admin/aimapper")
public class AuthorityInfraTypeMapperRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("authorityInfraTypeMapperService")
    public void setService(CrudService service) {
        this.name = "권한-인프라타입 매핑정보";
        this.service = service;
    }

    @RequestMapping(value = "listPageByRoleId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@RequestParam(value = "id") int id,
                                  @RequestParam(value = "index") int index,
                                  @RequestParam(value = "size") int size,
                                  @RequestParam(value = "sortProperties", required = false) String[] sortProperties,
                                  @RequestParam(value = "keywords", required = false) String[] keywords,
                                  @RequestParam(value = "searchOp", required = false) String searchOp,
                                  @RequestParam(value = "fromDate", required = false) String fromDate,
                                  @RequestParam(value = "toDate", required = false) String toDate) throws BackendException {
        try {
            return new ApiResponseDto(true, ((AuthorityInfraTypeMapperService)this.service).listPageByRoleId(id, index, size, sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"roleId", "infraTypeCd"})
    public ApiResponseDto get(@RequestParam(value = "roleId") int roleId,
                              @RequestParam(value = "infraTypeCd") String infraTypeCd) throws BackendException {
        try {
            return new ApiResponseDto(true, ((AuthorityInfraTypeMapperService) service).get(roleId, infraTypeCd));
        } catch (Exception e) {
            throw new BackendException(this.name + " 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"roleId"})
    public ApiResponseDto getByRoleId(@RequestParam(value = "roleId") int roleId) throws BackendException {
        try {
            return new ApiResponseDto(true, ((AuthorityInfraTypeMapperService) service).getByRoleId(roleId));
        } catch (Exception e) {
            throw new BackendException(this.name + " 조회 중 오류발생", e);
        }
    }
}
