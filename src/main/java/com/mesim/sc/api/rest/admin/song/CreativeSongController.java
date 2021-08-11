package com.mesim.sc.api.rest.admin.song;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.api.rest.admin.AdminRestController;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.CrudService;

import com.mesim.sc.service.admin.song.CreativeSongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/admin/song")
public class CreativeSongController extends AdminRestController {

    @Override
    @Autowired
    @Qualifier("creativeSongService")
    public void setService(CrudService service) {
        this.name = "창작곡";
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
            return new ApiResponseDto(true, ((CreativeSongService) this.service).getListPage(id, index, size, sortProperties, keywords, searchOp));
        } catch (Exception e) {
            throw new BackendException(this.name + " 페이지 목록 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "list/{typeCd}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto getList(@PathVariable("typeCd") String typeCd) throws BackendException {
        try {
            return new ApiResponseDto(true, ((CreativeSongService) service).getList(typeCd));
        } catch (Exception e) {
            throw new BackendException(this.name + " 목록 조회 중 오류발생", e);
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
            return new ApiResponseDto(true, ((CreativeSongService) service).save(o));
        } catch (Exception e) {
            throw new BackendException(this.name + " 등록 중 오류발생", e);
        }
    }

    /**
     * 파일 업로드
     * @param data
     * @param files
     * @return
     * @throws BackendException
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto upload(
            @RequestPart(value = "data") Object data,
            @RequestPart(value = "files") MultipartFile[] files
    ) throws BackendException {
        try {
            return new ApiResponseDto(true, ((CreativeSongService) service).add(data, files));
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
            return new ApiResponseDto(true, ((CreativeSongService) service).fileDownload(id,fileName));
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
            return new ApiResponseDto(true, ((CreativeSongService) service).fileAllDownload(id));
        } catch (Exception e) {
            throw new BackendException(this.name + " 상세 조회 중 오류발생", e);
        }
    }

    @RequestMapping(value = "getSound", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] audio(@RequestParam(value = "id") String id,
                                @RequestParam(value = "fileName") String fileName) throws BackendException {
        try {
            return ((CreativeSongService) service).getSound(id,fileName);
        } catch (Exception e) {
            throw new BackendException(this.name + " 다운로드 중 오류발생", e);
        }
    }
}
