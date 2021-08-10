package com.mesim.sc.service.admin.song;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.song.CreativeSongRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("creativeSongService")
public class CreativeSongService extends AdminService {

    @Value("${file.data.base.path}")
    private String fileBasePath;

    @Value("${file.data.song.path}")
    private String songPath;

    @Value("${file.data.temp.path}")
    private String fileTempPath;

    @Autowired
    @Qualifier("creativeSongRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"id", "genre","composer","lyricist"};
        super.init();
    }

    public PageWrapper getListPage(String typeCd, int index, int size, String[] sortProperties, String[] keywords, String searchOp) throws BackendException {
        Specification<Object> spec = AdminSpecs.typeCd(typeCd);
        PageRequest pageRequest = super.getPageRequest(index, size, sortProperties);

        if (keywords != null && keywords.length > 0) {
            spec = Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        Page<CreativeSong> page = ((CreativeSongRepository) this.repository).findAll(spec, pageRequest);

        PageWrapper result = new PageWrapper(page);
        final AtomicInteger i = new AtomicInteger(1);

        result.setList(page.get()
            .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement() + (result.getNumber() * size))))
            .collect(Collectors.toList())
        );

        return result;
    }

    public Object getList(String typeCd) {
        return ((CreativeSongRepository) this.repository).findAllByGenre(typeCd)
                .stream()
                .map(CreativeSongDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 저장 (생성 또는 수정)
     *
     * @param o 데이터
     * @return 저장된 데이터
     */
    public Object save(Object o) throws BackendException {
        Object entity = this.toEntity(o);
        Object result = this.repository.saveAndFlush(entity);
        this.entityManager.refresh(result);
        return this.toDto(result, 0);
    }

    public Object add(Object data, MultipartFile[] files) throws BackendException {
        CreativeSongDto savedSongDto = (CreativeSongDto) this.save(data);

        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath+System.getProperty("file.separator")+savedSongDto.getSongCd());
        String tempPath = FileUtil.makePath(this.fileBasePath, this.fileTempPath);


        try {
            for (MultipartFile file : files) {
                FileUtil.moveFile(filePath,file.getOriginalFilename(),tempPath);
            }
        } catch (IOException e) {
            throw new BackendException("파일 업로드 중 오류 발생", e);
        }

        return savedSongDto;
    }

    public byte[] fileDownload(String id,String fileName)  throws BackendException {
        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath+System.getProperty("file.separator")+id+System.getProperty("file.separator")+fileName);
        return FileUtil.download(filePath);
    }

    public byte[] fileAllDownload(String id)  throws BackendException {

        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath+System.getProperty("file.separator")+id);
        String outPath = FileUtil.makePath(this.fileBasePath, this.songPath+System.getProperty("file.separator")+id);
        List<String> fileList = FileUtil.fileList(filePath);
        File zipFile = FileUtil.compress(outPath,filePath,fileList);
        String zipFilePath = FileUtil.makePath(this.fileBasePath, this.songPath+System.getProperty("file.separator")+id+System.getProperty("file.separator")+zipFile.getName());
        byte[] zipFileByte = FileUtil.download(zipFilePath);

        File deleteFile = new File(zipFilePath);
        if (deleteFile.exists()) {
            deleteFile.delete();
        }

        return zipFileByte;

    }


    @Override
    public boolean delete(Object o) throws BackendException {
        Map<String, Object> map = (Map<String, Object>) o;
        List<Object> deleteObjects =((List<Object>) map.get("ids"))
                .stream()
                .map(ExceptionHandler.wrap(object -> this.toEntity(object)))
                .collect(Collectors.toList());
        this.repository.deleteAll(deleteObjects);

        List<Map<String,Object>> list = (List<Map<String, Object>>) map.get("ids");
        for (int i=0; i<list.size(); i++){

            String id = list.get(i).get("id").toString();
            String filePath = FileUtil.makePath(this.fileBasePath, this.songPath+System.getProperty("file.separator")+id);

            FileUtil.deleteFile(filePath);
        }


        return true;
    }

    @Override
    public Object get(String id) {
        String filePath = this.fileBasePath+System.getProperty("file.separator")+this.songPath+System.getProperty("file.separator")+id;
        List<String> fileNameList = FileUtil.fileList(filePath);

        Optional<Object> optEntity = this.repository.findById(id);

        return optEntity.map(o -> {
            try {
                CreativeSongDto dto = (CreativeSongDto) toDto(o, 0);
                dto.setFileList(fileNameList);
                return dto;
//                return this.toDto(o, 0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
    }

    public String getSound(String songCd,String fileName) throws BackendException {
        String soundPath = FileUtil.makePath(this.fileBasePath, this.songPath+System.getProperty("file.separator")+songCd+System.getProperty("file.separator")+fileName);
        File soundFile = new File(soundPath);
        try {
            if (soundFile != null) {
                return FileUtil.getAudioBase64Str(soundPath);
            } else {
                throw new BackendException("사운드 없음");
            }
        } catch (Exception e) {
            throw new BackendException("사운드 읽는 중 오류발생", e);
        }
    }

}
