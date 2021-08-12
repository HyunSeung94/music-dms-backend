package com.mesim.sc.service.admin.vocal;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.repository.rdb.admin.vocal.VocalRepository;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("vocalService")
public class VocalService extends AdminService {

    @Value("${file.data.base.path}")
    private String fileBasePath;

    @Value("${file.data.song.path}")
    private String songPath;

    @Value("${file.data.temp.path}")
    private String fileTempPath;

    @Autowired
    @Qualifier("vocalRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"id", "songCd", "songSongNm", "singerCd", "singerConsortiumNm", "studioName"};
        this.joinedSortField = new String[]{"song", "singer", "studio"};

        this.addRefEntity("song", "songNm");
        this.addRefEntity("singer", "consortiumNm");
        this.addRefEntity("studio", "name");

        super.init();
    }

    public PageWrapper getListPage(String typeCd, int index, int size, String[] sortProperties, String[] keywords, String searchOp) throws BackendException {
        Specification<Object> spec = AdminSpecs.typeCd(typeCd);
        PageRequest pageRequest = super.getPageRequest(index, size, sortProperties);

        if (keywords != null && keywords.length > 0) {
            spec = Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        Page<Vocal> page = ((VocalRepository) this.repository).findAll(spec, pageRequest);

        PageWrapper result = new PageWrapper(page);
        final AtomicInteger i = new AtomicInteger(1);

        result.setList(page.get()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement() + (result.getNumber() * size))))
                .collect(Collectors.toList())
        );

        return result;
    }

    @Override
    public Object get(String id) {
        Optional<Object> optEntity = this.repository.findById(id);

        return optEntity.map(o -> {
            try {
                VocalDto dto = (VocalDto) toDto(o, 0);
                String filePath = FileUtil.makePath(this.fileBasePath, this.songPath, dto.getSongCd());
                List<String> fileNameList = FileUtil.fileList(filePath);
                dto.setFileList(fileNameList);
                return dto;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
    }

    @Override
    public Object add(Object data, MultipartFile[] files) throws BackendException {
        VocalDto savedVocalDto = (VocalDto) this.save(data);

        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath, savedVocalDto.getSongCd());
        String tempPath = FileUtil.makePath(this.fileBasePath, this.fileTempPath);

        try {
            for (MultipartFile file : files) {
                FileUtil.moveFile(filePath, file.getOriginalFilename(), tempPath);
            }
        } catch (IOException e) {
            throw new BackendException("파일 업로드 중 오류 발생", e);
        }

        return savedVocalDto;
    }

    @Override
    public boolean delete(Object o) {
        Map<String, Object> map = (Map<String, Object>) o;
        List<Object> deleteObjects =((List<Object>) map.get("ids"))
                .stream()
                .map(ExceptionHandler.wrap(object -> this.toEntity(object)))
                .collect(Collectors.toList());

        this.repository.deleteAll(deleteObjects);

        List<Map<String,Object>> list = (List<Map<String, Object>>) map.get("ids");

        for (int i = 0; i < list.size(); i++){
            String id = list.get(i).get("id").toString();
            String filePath = FileUtil.makePath(this.fileBasePath, this.songPath, id);

            FileUtil.deleteFile(filePath);
        }

        return true;
    }

    public byte[] fileDownload(String id,String fileName)  throws BackendException {
        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath, id, fileName);
        return FileUtil.download(filePath);
    }

    public byte[] fileAllDownload(String id) throws BackendException {
        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath, id);
        String outPath = FileUtil.makePath(this.fileBasePath, this.songPath, id);

        List<String> fileList = FileUtil.fileList(filePath);
        File zipFile = FileUtil.compress(outPath,filePath,fileList);
        String zipFilePath = FileUtil.makePath(this.fileBasePath, this.songPath, id, zipFile.getName());
        byte[] zipFileByte = FileUtil.download(zipFilePath);

        File deleteFile = new File(zipFilePath);
        if (deleteFile.exists()) {
            deleteFile.delete();
        }

        return zipFileByte;
    }

    public byte[] getFiletoByte(String songCd, String fileName) throws BackendException {
        String soundPath = FileUtil.makePath(this.fileBasePath, this.songPath, songCd, fileName);
        File soundFile = new File(soundPath);

        try {
            if (soundFile != null) {
                return FileUtil.download(soundPath);
            } else {
                throw new BackendException("사운드 없음");
            }
        } catch (Exception e) {
            throw new BackendException("사운드 읽는 중 오류발생", e);
        }
    }

}
