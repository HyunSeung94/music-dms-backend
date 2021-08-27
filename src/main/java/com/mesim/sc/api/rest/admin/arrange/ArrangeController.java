package com.mesim.sc.api.rest.admin.arrange;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;

import com.mesim.sc.service.admin.arrange.ArrangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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

    @RequestMapping(value = "listPage/{regId}/{regGroupId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@PathVariable(value = "regId") String regId,
                                  @PathVariable(value = "regGroupId") String regGroupId,
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
            return new ApiResponseDto(true, ((ArrangeService) this.service).getListPage(regId, regGroupId, select, index, size, sortProperties, keywords, searchOp, fromDate, toDate));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

    /**
     * 파일 업로드
     *
     * @param dto 등록할 데이터 DTO
     * @param files 파일 목록
     * @return 성공/실패 여부
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto upload(
            @RequestPart(value = "data") Object dto,
            @RequestPart(value = "files") MultipartFile[] files,
            Authentication authentication
    ) throws BackendException {
        try {
            String userId = authentication.getPrincipal().toString();
            return new ApiResponseDto(true, ((ArrangeService) service).add(dto, files,userId));
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }


    /**
     *  파일 다운로드
     *
     * @param id 상세 조회할 데이터 ID
     * @return 상세 정보
     */
    @RequestMapping(value = "getFile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getFile(@RequestParam(value = "id") String id,
                                  @RequestParam(value = "fileName") String fileName) throws BackendException {
        try {
            return new ApiResponseDto(true, ((ArrangeService) service).fileDownload(id,fileName));
        } catch (Exception e) {
            throw new BackendException(this.name + " 상세 조회 중 오류발생", e);
        }
    }

    /**
     *  파일 전체 다운로드
     *
     * @param id 상세 조회할 데이터 ID
     * @return 상세 정보
     */
    @RequestMapping(value = "getFileAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getFileAll(@RequestParam(value = "id") String id) throws BackendException {
        try {
            return new ApiResponseDto(true, ((ArrangeService) service).fileAllDownload(id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 상세 조회 중 오류발생", e);
        }
    }

    /**
     * 파일 Bytes 가져오기
     *
     * @param id 조회할 데이터 ID
     * @param fileName 파일 명
     * @return 파일 Bytes
     */
    @RequestMapping(value = "getFiletoByte", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] audio(@RequestParam(value = "id") String id,
                        @RequestParam(value = "fileName") String fileName) throws BackendException {
        try {
            return ((ArrangeService) service).getFiletoByte(id,fileName);
        } catch (Exception e) {
            throw new BackendException(this.name + " 다운로드 중 오류발생", e);
        }
    }

}
