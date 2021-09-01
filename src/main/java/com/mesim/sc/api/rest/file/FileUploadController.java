package com.mesim.sc.api.rest.file;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.util.FileUtil;
import com.mesim.sc.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/file")
public class FileUploadController {

    @Autowired
    private AuthService authService;

    @Value("${file.data.base.path}")
    private String fileBasePath;

    @Value("${file.data.temp.path}")
    private String fileTempPath;

    @Value("${file.data.csv.path}")
    private String csvPath;

    // Cache 타입
    private static class Node {
        String fileName;
        int fileSize;
        int filepos;
        Date lastupdated;
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto upload(@RequestPart(value = "file") MultipartFile[] files, Authentication authentication) throws BackendException {
        try {
            String userId = authentication.getPrincipal().toString();
            return uploadTempFile(files,userId);
        } catch (Exception e) {
            throw new BackendException("파일 업로드 중 오류발생", e);
        }
    }

    @RequestMapping(value = "uploadCsv", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public ApiResponseDto uploadCsv(@RequestPart(value = "file") MultipartFile[] files, Authentication authentication) throws BackendException {
        try {
            String userId = authentication.getPrincipal().toString();
            return uploadCsvFile(files,userId);
        } catch (Exception e) {
            throw new BackendException("파일 업로드 중 오류발생", e);
        }
    }

    public ApiResponseDto uploadTempFile(MultipartFile[] files,String userId) throws IOException{

        String filePath = FileUtil.makePath(this.fileBasePath, this.fileTempPath,userId);

        for (MultipartFile file : files) {
            FileUtil.upload(filePath, file.getOriginalFilename(), file);
        }

        return new ApiResponseDto(true);
    }

    public ApiResponseDto uploadCsvFile(MultipartFile[] files,String userId) throws IOException{

        String filePath = FileUtil.makePath(this.fileBasePath, this.csvPath, this.fileTempPath, userId);

        for (MultipartFile file : files) {
            FileUtil.upload(filePath, file.getOriginalFilename(), file);
        }

        return new ApiResponseDto(true);
    }

    public ApiResponseDto uploadFile(Map request) throws IOException {
        Node node = new Node();

        // 파라미터 타입
        String fileBaseUrl = (String) request.get("fileBaseUrl");
        String fileName = (String) request.get("fileName");
        int fileSize = (int) request.get("fileSize");
        String data = (String) request.get("fileData");

        // 업데이트 정보 등록
        node.fileSize = fileSize;
        node.fileName = fileName;
        node.filepos = -1;
        node.lastupdated = new Date();

        // Base64 타입의 문자열을 byte 배열의 binary 로 변경
        byte[] binary = Base64.getDecoder().decode(data.split(",")[1]);

        if (fileBaseUrl != null) {
            String filePath = FileUtil.makePath(fileBasePath, fileBaseUrl);
            File Folder = new File(filePath);

            if (!Folder.exists()) {
                Folder.mkdirs();
            }

            try (FileOutputStream stream = new FileOutputStream(FileUtil.makePath(filePath, fileName))) {
                stream.write(binary);
            } catch (Exception e) {
                throw e;
            }

            return new ApiResponseDto(true);

        }

        try (FileOutputStream stream = new FileOutputStream(FileUtil.makePath(fileBasePath, fileName))) {
            stream.write(binary);
        } catch (Exception e) {
            throw e;
        }

        return new ApiResponseDto(true);
    }
}
