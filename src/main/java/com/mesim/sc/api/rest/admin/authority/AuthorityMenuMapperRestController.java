package com.mesim.sc.api.rest.admin.authority;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.authority.AuthorityMenuMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/ammapper")
public class AuthorityMenuMapperRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("authorityMenuMapperService")
    public void setService(CrudService service) {
        this.name = "권한-화면 매핑정보";
        this.service = service;
    }

    @RequestMapping(value = "listPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"id", "index", "size"})
    public ApiResponseDto getList(@RequestParam(value = "id") String id,
                                  @RequestParam(value = "index") int index,
                                  @RequestParam(value = "size") int size) throws BackendException {
        try {
            return new ApiResponseDto(true, ((AuthorityMenuMapperService) service).getListPage(id, index, size));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto modify(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, ((AuthorityMenuMapperService) service).update(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 수정 중 오류발생", e);
        }
    }

    @RequestMapping(value = "detail", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto modifyDetail(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, ((AuthorityMenuMapperService) service).updateDetail(o));
        } catch(Exception e) {
            throw new BackendException(this.name + " 상세 수정 중 오류발생", e);
        }
    }
}
