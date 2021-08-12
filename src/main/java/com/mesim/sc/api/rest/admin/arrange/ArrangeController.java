package com.mesim.sc.api.rest.admin.arrange;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;

import com.mesim.sc.service.admin.arrange.ArrangeService;
import com.mesim.sc.service.admin.song.CreativeSongService;
import com.mesim.sc.service.admin.vocal.VocalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/admin/arrange")
public class ArrangeController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("arrangeService")
    public void setService(CrudService service) {
        this.name = "가공 데이터";
        this.service = service;
    }

    @RequestMapping(value = "listPage/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@PathVariable(value = "id") String id,
                                  @RequestParam(value = "index") int index,
                                  @RequestParam(value = "size") int size,
                                  @RequestParam(value = "sortProperties", required = false) String[] sortProperties,
                                  @RequestParam(value = "keywords", required = false) String[] keywords,
                                  @RequestParam(value = "searchOp", required = false) String searchOp) throws BackendException {
        try {
            return new ApiResponseDto(true, ((ArrangeService) this.service).getListPage(id, index, size, sortProperties, keywords, searchOp));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

}
