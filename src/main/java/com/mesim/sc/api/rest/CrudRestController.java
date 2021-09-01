package com.mesim.sc.api.rest;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
public abstract class CrudRestController {

    @Value("${file.data.base.path}")
    private String fileBasePath;

    @Value("${file.data.temp.path}")
    private String tempPath;

    @Value("${file.data.csv.path}")
    private String csvPath;


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
    @RequestMapping(value = {"listPage"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(
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
            return new ApiResponseDto(true, this.service.getListPage(select, index, size, sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

    /**
     * 전체 목록 조회
     *
     * @param select 검색 기본값
     * @param sortProperties 정렬기준 (ex: sort;asc,modDate;desc)
     * @param keywords 검색 키워드 (ex: keywords=관제,재난상황)
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND (ex: searchOp=OR)
     * @param fromDate 검색 시작 날짜 (ex: 2020-06-01)
     * @param toDate 검색 마지막 날짜 (ex: 2020-06-30)
     * @return 전체 목록
     */
    @RequestMapping(value = {"list"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(
            @RequestParam(value = "select", required = false) String[] select,
            @RequestParam(value = "sortProperties", required = false) String[] sortProperties,
            @RequestParam(value = "keywords", required = false) String[] keywords,
            @RequestParam(value = "searchOp", required = false) String searchOp,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getList(select, sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 전체 목록 조회 중 오류발생", e);
        }
    }

    /**
     * 선택 목록 조회
     *
     * @param select 검색 기본값
     * @return 선택 목록
     */
    @RequestMapping(value = {"listSelect"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getSelectList(@RequestParam(value = "select", required = false) String[] select) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getListSelect(select));
        } catch (Exception e) {
            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
        }
    }

    /**
     * 선택 목록 다중 조회
     *
     * @param select 검색 기본값
     * @return 선택 목록
     */
    @RequestMapping(value = "listMultiSelect", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getMultiSelectList(@RequestParam(value = "select") String[][] select) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getListMultiSelect(select));
        } catch (Exception e) {
            throw new BackendException(this.name + " 선택 목록 조회 중 오류발생", e);
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
     * @return 상세 데이터 DTO
     */
    @RequestMapping(value = "get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto get(@PathVariable(value = "id") String id) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.get(id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 상세 조회 중 오류발생", e);
        }
    }

    /**
     * 상세 조회
     * 상세 조회할 Entity 의 기본키가 2개 이상인 경우 해당 메소드 이용
     *
     * @param pk 상세 조회할 데이터 기본키
     * @return 상세 데이터 DTO
     */
    @RequestMapping(value = "get", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto get(@RequestBody Object pk) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.get(pk));
        } catch (Exception e) {
            throw new BackendException(this.name + " 상세 조회 중 오류발생", e);
        }
    }


    /**
     * 등록
     *
     * @param dto 등록할 데이터 DTO
     * @return 등록된 데이터 DTO
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto add(@RequestBody Object dto) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.add(dto));
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

    /**
     * 등록
     * 파일 업로드가 포함된 경우 해당 메소드 활용
     *
     * @param dto 등록할 데이터 DTO
     * @param file 파일
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "add/upload", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto add(
            @RequestPart(value = "data") Object dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletResponse response
    ) throws BackendException {
        try {
            Object result = this.service.add(dto, file);
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return new ApiResponseDto(false, null, "Not suuport API");
            } else {
                return new ApiResponseDto(true, result);
            }
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

    /**
     * 등록
     * 파일 다중 업로드가 포함된 경우 해당 메소드 활용
     *
     * @param dto 등록할 데이터 DTO
     * @param files 파일 목록
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "add/multiUpload", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto add(
            @RequestPart(value = "data") Object dto,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            HttpServletResponse response
    ) throws BackendException {
        try {
            Object result = this.service.add(dto, files);
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return new ApiResponseDto(false, null, "Not suuport API");
            } else {
                return new ApiResponseDto(true, result);
            }
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

    /**
     * 수정
     *
     * @param dto 수정할 데이터 DTO
     * @return 수정된 데이터 DTO
     */
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto modify(@RequestBody Object dto) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.modify(dto));
        } catch (Exception e) {
            throw new BackendException(this.name + " 수정 중 오류발생", e);
        }
    }

    /**
     * 수정
     * 파일 업로드가 포함된 경우 해당 메소드 활용
     *
     * @param dto 수정할 데이터 DTO
     * @param file 파일
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "modify/upload", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto modify(
            @RequestPart(value = "data") Object dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletResponse response
    ) throws BackendException {
        try {
            Object result = this.service.modify(dto, file);
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return new ApiResponseDto(false, null, "Not suuport API");
            } else {
                return new ApiResponseDto(true, result);
            }
        } catch (Exception e) {
            throw new BackendException(this.name + " 수정 중 오류발생", e);
        }
    }

    /**
     * 수정
     * 파일 다중 업로드가 포함된 경우 해당 메소드 활용
     *
     * @param dto 수정할 데이터 DTO
     * @param files 파일 목록
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "modify/multiUpload", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto modify(
            @RequestPart(value = "data") Object dto,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "delete", required = false) Object delete,
            HttpServletResponse response
    ) throws BackendException {
        try {
            Object result = this.service.modify(dto, files, delete);
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return new ApiResponseDto(false, null, "Not suuport API");
            } else {
                return new ApiResponseDto(true, result);
            }
        } catch (Exception e) {
            throw new BackendException(this.name + " 수정 중 오류발생", e);
        }
    }

    /**
     * 삭제
     *
     * @param id 삭제할 데이터 ID
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto delete(@PathVariable(value = "id") String id) throws BackendException {
        try {
            return new ApiResponseDto(this.service.delete(id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 삭제 중 오류발생", e);
        }
    }

    /**
     * 삭제
     * 삭제할 Entity 의 기본키가 2개 이상이거나 여러 Entity 를 삭제하는 경우 해당 메소드 이용
     *
     * @param dto 삭제할 데이터 DTO
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto delete(@RequestBody Object dto) throws BackendException {
        try {
            return new ApiResponseDto(this.service.delete(dto));
        } catch (Exception e) {
            throw new BackendException(this.name + " 삭제 중 오류발생", e);
        }
    }

    /**
     * 파일 업로드 Temp 파일 삭제
     *
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "deleteTempFile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto deleteTempFile(Authentication authentication) throws BackendException {
        try {
            String userId = authentication.getPrincipal().toString();
            return new ApiResponseDto(true, FileUtil.deleteFile(FileUtil.makePath(fileBasePath, tempPath,userId)));
        } catch (Exception e) {
            throw new BackendException(this.name + " 다운로드 중 오류발생", e);
        }
    }

    /**
     * 파일 업로드 Temp 파일 삭제
     *
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "deleteCsvFile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto deleteCsvFile(Authentication authentication) throws BackendException {
        try {
            String userId = authentication.getPrincipal().toString();
            return new ApiResponseDto(true, FileUtil.deleteFile(FileUtil.makePath(fileBasePath, csvPath,tempPath,userId)));
        } catch (Exception e) {
            throw new BackendException(this.name + " 다운로드 중 오류발생", e);
        }
    }

    /**
     * 미리보기
     *
     * @param id 미리보기할 데이터 ID
     * @return 미리보기 이미지
     */
    @ResponseBody
    @RequestMapping(value = "preview/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] preview(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "refId", required = false) Object refId,
            HttpServletResponse response
    ) throws BackendException {
        try {
            byte[] imgBytes = this.service.preview(id, refId);
            if (imgBytes == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            } else {
                return imgBytes;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new BackendException(this.name + " 미리보기 중 오류발생", e);
        }
    }

    /**
     * 미리보기
     * 상세 조회할 Entity 의 기본키가 2개 이상인 경우 해당 메소드 이용
     *
     * @param pk 미리보기할 데이터 기본키
     * @return 미리보기 이미지
     */
    @ResponseBody
    @RequestMapping(value = "preview", method = RequestMethod.POST, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] preview(
            @RequestBody Object pk,
            HttpServletResponse response
    ) throws BackendException {
        try {
            byte[] imgBytes = this.service.preview(pk);
            if (imgBytes == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            } else {
                return imgBytes;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new BackendException(this.name + " 미리보기 중 오류발생", e);
        }
    }

    /**
     * 내보내기
     *
     * @param sortProperties 정렬기준 (ex: sort;asc,modDate;desc)
     * @param keywords 검색 키워드 (ex: keywords=관제,재난상황)
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND (ex: searchOp=OR)
     * @param fromDate 검색 시작 날짜 (ex: 2020-06-01)
     * @param toDate 검색 마지막 날짜 (ex: 2020-06-30)
     * @return CSV 파일
     */
    @RequestMapping(value = "export", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto export(
            @RequestParam(value = "sortProperties", required = false) String[] sortProperties,
            @RequestParam(value = "keywords", required = false) String[] keywords,
            @RequestParam(value = "searchOp", required = false) String searchOp,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate
    ) throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.export(sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 다운로드 중 오류발생", e);
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

    /**
     * 파일 업로드
     *
     * @param files 파일 목록
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "importCsv", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto importCsv(@RequestPart(value = "file") MultipartFile[] files,
                                        Authentication authentication ) throws BackendException {
        try {
            String userId = authentication.getPrincipal().toString();
            this.service.importCsv(files, userId);
            return new ApiResponseDto(true);
        } catch (Exception e) {
            throw new BackendException(this.name + "오류 발생", e);
        }
    }


    /**
     * fileCheck
     * @return
     * @throws BackendException
     */
    @RequestMapping(value = "fileCheck", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto fileCheck() throws BackendException {
        try {
            return new ApiResponseDto(true, this.service.getListFileCheck());
        } catch (Exception e) {
            throw new BackendException(this.name + " 쿼리 생성 조회 중 오류발생", e);
        }
    }

}
