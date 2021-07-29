package com.mesim.sc.api.rest;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
public abstract class CrudRestController {

    @Value("${file.data.base.path}")
    private String fileBasePath;

    @Value("${file.data.temp.path}")
    private String csvPath;

    @Value("${csv.temp.file}")
    private String csvFileName;

    protected String name;

    protected CrudService service;

    /**
     * 각 컨트롤러에서 사용하는 Service 객체 주입
     *
     * @param service CrudService 를 상속받는 서비스
     */
    abstract protected void setService(CrudService service);

    /**
     * 페이지 목록 조회
     *
     * @param index 페이지 번호
     * @param size 페이지 크기
     * @param sortProperties 정렬기준 (ex: sort;asc,modDate;desc)
     * @param keywords 검색 키워드 (ex: keywords=관제,재난상황)
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND (ex: searchOp=OR)
     * @param fromDate 검색 시작 날짜 (ex: 2020-06-01)
     * @param toDate 검색 마지막 날짜 (ex: 2020-06-30)
     * @return 페이지 목록
     */
    @RequestMapping(value = "listPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@RequestParam(value = "index") int index,
                                  @RequestParam(value = "size") int size,
                                  @RequestParam(value = "sortProperties", required = false) String[] sortProperties,
                                  @RequestParam(value = "keywords", required = false) String[] keywords,
                                  @RequestParam(value = "searchOp", required = false) String searchOp,
                                  @RequestParam(value = "fromDate", required = false) String fromDate,
                                  @RequestParam(value = "toDate", required = false) String toDate) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getListPage(index, size, sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

    /**
     * 전체 목록 조회
     *
     * @param sortProperties 정렬기준 (ex: sort;asc,modDate;desc)
     * @param keywords 검색 키워드 (ex: keywords=관제,재난상황)
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND (ex: searchOp=OR)
     * @param fromDate 검색 시작 날짜 (ex: 2020-06-01)
     * @param toDate 검색 마지막 날짜 (ex: 2020-06-30)
     * @return 전체 목록
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@RequestParam(value = "sortProperties", required = false) String[] sortProperties,
                                  @RequestParam(value = "keywords", required = false) String[] keywords,
                                  @RequestParam(value = "searchOp", required = false) String searchOp,
                                  @RequestParam(value = "fromDate", required = false) String fromDate,
                                  @RequestParam(value = "toDate", required = false) String toDate) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getList(sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 전체 목록 조회 중 오류발생", e);
        }
    }

    /**
     * 컬럼 목록 조회
     *
     * @return 컬럼 목록
     */
    @RequestMapping(value = "listColumn", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getColumnList() throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getListColumn());
        } catch (Exception e) {
            throw new BackendException(this.name + " 컬럼 목록 조회 중 오류발생", e);
        }
    }

    /**
     * 상세 조회
     *
     * @param id 상세 조회할 데이터 ID
     * @return 상세 정보
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto get(@RequestParam(value = "id") String id) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.get(id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 상세 조회 중 오류발생", e);
        }
    }

    /**
     * 정보 생성
     *
     * @param o 생성할 데이터 객체
     * @return 생성된 정보
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto add(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.save(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

    /**
     * 정보 수정
     *
     * @param o 수정할 데이터 객체
     * @return 수정된 정보
     */
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto modify(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.save(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 수정 중 오류발생", e);
        }
    }

    /**
     * 정보 삭제
     *
     * @param id 삭제할 데이터 ID
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, params = {"id"})
    public ApiResponseDto delete(@RequestParam(value = "id") String id) throws BackendException {
        try {
            return new ApiResponseDto(this.service.delete(id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 삭제 중 오류발생", e);
        }
    }

    /**
     * 정보 삭제
     * 삭제할 정보의 PK가 2개 이상인 경우 해당 메소드 이용
     *11
     * @param o 삭제할 데이터 객체
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto delete(@RequestBody Object o) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.delete(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 삭제 중 오류발생", e);
        }
    }

    /**
     * 위젯에서 그룹핑을 통한 데이터 조회
     * @param select
     * @param table
     * @param group
     * @param groupItem
     * @param standard
     * @param standardUnit
     * @param fromDate
     * @param toDate
     * @return 중복제거 로우 목록
     */
    @RequestMapping(value = "getRowGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getRowGroup(@RequestParam(value = "select", required = false) String select,
                                      @RequestParam(value = "table") String table,
                                      @RequestParam(value = "group") String group,
                                      @RequestParam(value = "groupItem", required = false) String groupItem,
                                      @RequestParam(value = "standard", required = false) String standard,
                                      @RequestParam(value = "standardUnit", required = false) String standardUnit,
                                      @RequestParam(value = "fromDate", required = false) String fromDate,
                                      @RequestParam(value = "toDate", required = false) String toDate) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getRowGroup(select, group, groupItem, table, standard, standardUnit, toDate, fromDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 쿼리 생성 조회 중 오류발생", e);
        }
    }

    /**
     * 위젯에서 기간별을 통한 데이터 조회
     * @param select
     * @param table
     * @param group
     * @param groupItem
     * @param standard
     * @param standardUnit
     * @param rowPeriod
     * @param rowPeriodUnit
     * @param fromDate
     * @param toDate
     * @return 중복제거 로우 목록
     */
    @RequestMapping(value = "getRowPeriod", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getRowGroup(@RequestParam(value = "select", required = false) String select,
                                      @RequestParam(value = "table") String table,
                                      @RequestParam(value = "group", required = false) String group,
                                      @RequestParam(value = "groupItem", required = false) String groupItem,
                                      @RequestParam(value = "standard", required = false) String standard,
                                      @RequestParam(value = "standardUnit", required = false) String standardUnit,
                                      @RequestParam(value = "rowPeriod", required = false) int rowPeriod,
                                      @RequestParam(value = "rowPeriodUnit", required = false) String rowPeriodUnit,
                                      @RequestParam(value = "fromDate", required = false) String fromDate,
                                      @RequestParam(value = "toDate", required = false) String toDate) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getRowPeriod(select, group, groupItem, table, standard, standardUnit, rowPeriod, rowPeriodUnit, toDate, fromDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 쿼리 생성 조회 중 오류발생", e);
        }
    }

    /**
     * 위젯에서 기간별을 통한 데이터 조회
     * @param select
     * @param groupItem
     * @param table
     * @param fromDate
     * @param toDate
     * @return 중복제거 로우 목록
     */
    @RequestMapping(value = "getRowColumn", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getRowGroup(@RequestParam(value = "select", required = false) String select,
                                      @RequestParam(value = "groupItem", required = false) String groupItem,
                                      @RequestParam(value = "table") String table,
                                      @RequestParam(value = "fromDate", required = false) String fromDate,
                                      @RequestParam(value = "toDate", required = false) String toDate) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getRowColumn(select, groupItem, table, toDate, fromDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 쿼리 생성 조회 중 오류발생", e);
        }
    }

    /**
     * 위젯 설정에서 그룹핑을 통한 셀렉트 조회
     * @param select
     * @param table
     * @param where
     * @return 중복제거 로우 목록
     */
    @RequestMapping(value = "getSelectGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getSelectGroup(@RequestParam(value = "select") String select,
                                         @RequestParam(value = "table") String table,
                                         @RequestParam(value = "where", required = false) String where) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getSelectGroup(select, where, table));
        } catch (Exception e) {
            throw new BackendException(this.name + " 쿼리 생성 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "generateCSV", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto generateCSV(@RequestParam(value = "sortProperties", required = false) String[] sortProperties,
                                      @RequestParam(value = "keywords", required = false) String[] keywords,
                                      @RequestParam(value = "searchOp", required = false) String searchOp,
                                      @RequestParam(value = "fromDate", required = false) String fromDate,
                                      @RequestParam(value = "toDate", required = false) String toDate) throws BackendException {
        try {
            List<Object> mapList = this.service.getList(sortProperties, keywords, searchOp, fromDate, toDate);
            this.service.generateCSV(mapList);
            return new ApiResponseDto(true, mapList);
        } catch (Exception e) {
            throw new BackendException(this.name + " CSV 파일 생성 중 오류발생", e);
        }
    }

    @RequestMapping(value = "exportCSV", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto exportCSV() throws BackendException {
        try {
            return new ApiResponseDto(true, FileUtil.download(FileUtil.makePath(fileBasePath, csvPath, csvFileName)));
        } catch (Exception e) {
            throw new BackendException(this.name + " 다운로드 중 오류발생", e);
        }
    }
}
