package com.mesim.sc.api.rest.admin.consortium;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.admin.arrange.ArrangeService;
import com.mesim.sc.service.admin.consortium.ConsortiumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/consortium")
public class ConsortiumController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("consortiumService")
    public void setService(CrudService service) {
        this.name = "컨소시엄별 리스트";
        this.service = service;
    }

    /**
     * 통계 쿼리 조회
     * @param select
     * @param groupItem
     * @param table
     * @return
     * @throws BackendException
     */
    @RequestMapping(value = "getRowGenreSum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getRowGenreSum(@RequestParam(value = "select", required = false) String select,
                                      @RequestParam(value = "groupItem", required = false) String groupItem,
                                      @RequestParam(value = "table") String table) throws BackendException {
        try {
            return new ApiResponseDto(true, ((ConsortiumService) service).getRowGenreSum(select, groupItem, table));
        } catch (Exception e) {
            throw new BackendException(this.name + " 쿼리 생성 조회 중 오류발생", e);
        }
    }

    /**
     * 페이지 목록 조회
     *
     * @param select 검색 기본값
     * @param index 페이지 번호
     * @param size 페이지 크기
     * @param sortProperties 정렬기준 (ex: sort;asc,modDate;desc)
     * @param keywords 검색 키워드 (ex: keywords=관제,재난상황)
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND (ex: searchOp=OR)
     * @param fromDate 검색 시작 날짜 (ex: 2020-06-01)
     * @param toDate 검색 마지막 날짜 (ex: 2020-06-30)
     * @return 페이지 목록
     */
    @RequestMapping(value = "singerListPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getSingerList(
            @RequestParam(value = "select", required = false) String[] select,
            @RequestParam(value = "index") int index,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sortProperties", required = false) String[] sortProperties,
            @RequestParam(value = "keywords", required = false) String[] keywords,
            @RequestParam(value = "searchOp", required = false) String searchOp,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) throws BackendException {
        try {
            return new ApiResponseDto(true, ((ConsortiumService) service).getSingerListPage(select, index, size, sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

}
