package com.mesim.sc.service.admin.song;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.song.CreativeSongRepository;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.util.CSV;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
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

    @Value("${file.data.csv.path}")
    private String csvPath;

    @Autowired
    @Qualifier("creativeSongRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.selectSortField = "songNm";
        this.searchFields = new String[]{"id", "genre", "composerConsortiumNm", "lyricist"};

        this.addRefEntity("composer", "consortiumNm");

        super.init();
    }

    public PageWrapper getListPage(String regId, String regGroupId, String[] select, int index, int size, String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        Specification<Object> spec = null;

        if (!regGroupId.equals(Integer.toString(CommonConstants.GROUP_ROOT_ID))) {
            if (regGroupId.equals(Integer.toString(CommonConstants.GROUP_CHILL_ROOT_ID))) {
                spec = AdminSpecs.regGroupId(CommonConstants.GROUP_CHILL_ID).or(AdminSpecs.regGroupId(CommonConstants.GROUP_CHILL_ROOT_ID));
            } else if (regGroupId.equals(Integer.toString(CommonConstants.GROUP_FKMP_ROOT_ID))) {
                spec = AdminSpecs.regGroupId(CommonConstants.GROUP_FKMP_ID).or(AdminSpecs.regGroupId(CommonConstants.GROUP_FKMP_ROOT_ID));
            } else {
                spec = AdminSpecs.regId(regId);
            }
        }

        PageRequest pageRequest = this.getPageRequest(index, size, sortProperties);
        Specification<Object> pageSpec = this.getSpec(select, keywords, searchOp, fromDate, toDate);

        if (pageSpec != null) {
            spec = spec == null ? pageSpec : spec.and(pageSpec);
        }

        Page<Object> page = this.repository.findAll(spec, pageRequest);
        PageWrapper result = new PageWrapper(page);
        final AtomicInteger seq = new AtomicInteger(1);

        List<Object> list = page
                .get()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, seq.getAndIncrement() + (result.getNumber() * size))))
                .collect(Collectors.toList());

        result.setList(list);

        return result;
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

    @Override
    public Object get(String id) {
        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath, id);
        List<String> fileNameList = FileUtil.fileList(filePath);

        Optional<Object> optEntity = this.repository.findById(id);

        return optEntity.map(o -> {
            try {
                CreativeSongDto dto = (CreativeSongDto) toDto(o, 0);
                dto.setFileList(fileNameList);
                return dto;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
    }


    public Object add(Object data, MultipartFile[] files,String userId) throws BackendException {
        CreativeSongDto savedSongDto = (CreativeSongDto) this.save(data);

        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath, savedSongDto.getId());
        String tempPath = FileUtil.makePath(this.fileBasePath, this.fileTempPath, userId);

        try {
            for (MultipartFile file : files) {
                FileUtil.moveFile(filePath, file.getOriginalFilename(), tempPath);
            }
        } catch (IOException e) {
            throw new BackendException("파일 업로드 중 오류 발생", e);
        }

        return savedSongDto;
    }

    @Override
    public boolean delete(Object o) {
        Map<String, Object> map = (Map<String, Object>) o;
        List<Object> deleteObjects =((List<Object>) map.get("data"))
                .stream()
                .map(ExceptionHandler.wrap(object -> this.toEntity(object)))
                .collect(Collectors.toList());

        this.repository.deleteAll(deleteObjects);

        List<Map<String,Object>> list = (List<Map<String, Object>>) map.get("data");

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

    @Override
    public void importCsv(MultipartFile[] multipartFile, String userId) throws IOException, BackendException {
        String fileName= multipartFile[0].getOriginalFilename();
        String ext = FileUtil.getExt(fileName);
        if (!ext.equals("csv")) {
            throw new BackendException("지원하지 않는 파일 형식입니다.");
        }

        String path = FileUtil.makePath(fileBasePath,csvPath,userId);

        File file = new File(path + System.getProperty("file.separator") + fileName);


        try (InputStream in = new FileInputStream(file);) {
            CSV csv = new CSV(true, ',', in );
            List<CreativeSong> songList = new ArrayList < > ();
            List < String > fieldNames = null;
            if (csv.hasNext()) fieldNames = new ArrayList < > (csv.next());

            while (csv.hasNext()) {
                List < String > x = csv.next();
                if(x.get(0).equals("")){
                    break;
                }
                CreativeSong creativeSong = CreativeSong.builder()
                        .id(x.get(0))
                        .composerCd(x.get(1))
                        .genre(x.get(2))
                        .songNm(x.get(3))
                        .songLength(x.get(4))
                        .tonality(x.get(5))
                        .tempo(x.get(6))
                        .vibe(x.get(7))
                        .instrumentCd(x.get(8))
                        .referenceSong(x.get(9))
                        .referenceArtist(x.get(10))
                        .createDate(Date.valueOf(x.get(11)))
                        .importYn("Y")
                        .regId(userId)
                        .modId(userId)
                        .build();

                songList.add(creativeSong);

            }
            this.repository.saveAll(songList);
        }
//        file.delete();
    }

}
