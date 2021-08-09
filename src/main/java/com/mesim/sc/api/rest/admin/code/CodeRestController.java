package com.mesim.sc.api.rest.admin.code;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.code.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("api/admin/code")
public class CodeRestController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("consortiumService")
    public void setService(CrudService service) {
        this.name = "공통코드";
        this.service = service;
    }

    @RequestMapping(value = "listPage/{typeCd}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@PathVariable(value = "typeCd") String typeCd,
                                  @RequestParam(value = "index") int index,
                                  @RequestParam(value = "size") int size,
                                  @RequestParam(value = "sortProperties", required = false) String[] sortProperties,
                                  @RequestParam(value = "keywords", required = false) String[] keywords,
                                  @RequestParam(value = "searchOp", required = false) String searchOp) throws BackendException {
        try {
            return new ApiResponseDto(true, ((CodeService) this.service).getListPage(typeCd, index, size, sortProperties, keywords, searchOp));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "list/{typeCd}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@PathVariable("typeCd") String typeCd) throws BackendException {
        try {
            return new ApiResponseDto(true, ((CodeService) service).getList(typeCd));
        } catch (Exception e) {
            throw new BackendException(this.name + " 목록 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "listSelect/{typeCd}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getListSelect(@PathVariable("typeCd") String typeCd) throws BackendException {
        try {
            return new ApiResponseDto(true, ((CodeService) service).getListSelect(typeCd));
        } catch (Exception e) {
            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "listSelect", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
        public ApiResponseDto getListMultiSelect(@RequestParam("typeCds") ArrayList typeCds) throws BackendException {
            try {
                return new ApiResponseDto(true, ((CodeService) service).getListMultiSelect(typeCds));
            } catch (Exception e) {
                throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
            }
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"cd", "typeCd"})
    public ApiResponseDto get(@RequestParam(value = "cd") String cd,
                              @RequestParam(value = "typeCd") String typeCd) throws BackendException {
        try {
            return new ApiResponseDto(true, ((CodeService) service).get(cd, typeCd));
        } catch (Exception e) {
            throw new BackendException(this.name + " 조회 중 오류발생", e);
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
