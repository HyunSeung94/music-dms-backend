package com.mesim.sc.service.admin.arrange;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.song.CreativeSongRepository;
import com.mesim.sc.repository.rdb.admin.vocal.VocalRepository;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.service.admin.vocal.VocalDto;
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
@Qualifier("arrangeService")
public class ArrangeService extends AdminService {

    @Value("${file.data.base.path}")
    private String fileBasePath;

    @Value("${file.data.song.path}")
    private String songPath;

    @Value("${file.data.vocal.path}")
    private String vocalPath;

    @Value("${file.data.temp.path}")
    private String fileTempPath;

    @Autowired
    private VocalRepository vocalRepository;

    @Autowired
    @Qualifier("arrangeRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.selectSortField = "id";
        this.searchFields = new String[]{"id", "arrangerCd", "arrangerConsortiumNm"};
        this.joinedSortField = new String[]{"arranger"};

        this.addRefEntity("arranger", "consortiumNm");

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

    @Override
    public Object get(String id) {
        Optional<Object> optEntity = this.repository.findById(Integer.parseInt(id));

        return optEntity.map(o -> {
            try {
                ArrangeDto dto = (ArrangeDto) toDto(o, 0);
                String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, dto.getContentsCd());

                List<String> fileNameList = new ArrayList<>();
                FileUtil.fileList(filePath).forEach(f -> {
                    if (!f.contains("vdata")) {
                        fileNameList.add(f);
                    }
                });

                dto.setFileList(fileNameList);
                return dto;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
    }

    @Override
    public Object add(Object data, MultipartFile[] files) throws BackendException {
        ArrangeDto savedArrangeDto = (ArrangeDto) this.save(data);

        String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, savedArrangeDto.getContentsCd());
        String tempPath = FileUtil.makePath(this.fileBasePath, this.fileTempPath);

        try {
            for (MultipartFile file : files) {
                FileUtil.moveFile(filePath, file.getOriginalFilename(), tempPath);
            }
        } catch (IOException e) {
            throw new BackendException("파일 업로드 중 오류 발생", e);
        }

        return savedArrangeDto;
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

        for (int i = 0; i < list.size(); i++) {
            String contentsCd = list.get(i).get("contentsCd").toString();
            String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, contentsCd);

            FileUtil.fileList(filePath).forEach(f -> {
                if (!f.contains("vdata")) {
                    FileUtil.deleteFile(filePath + System.getProperty("file.separator") + f);
                }
            });
        }

        return true;
    }

    public byte[] fileDownload(String id, String fileName)  throws BackendException {
        String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id, fileName);
        return FileUtil.download(filePath);
    }

    public byte[] fileAllDownload(String id) throws BackendException {

        Optional<Object> optEntity = Optional.of(this.vocalRepository.findById(id));

        return optEntity.map(o -> {
            try {
                VocalDto dto = (VocalDto) toDto(o, 0);

                String songFilePath = FileUtil.makePath(this.fileBasePath, this.songPath, dto.getSongCd());
                String vocalFilePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id);

                List<String> fileList = new ArrayList<>();
                FileUtil.fileList(songFilePath).forEach(f -> {
                    fileList.add(songFilePath +System.getProperty("file.separator")+ f);
                });
                FileUtil.fileList(vocalFilePath).forEach(f -> {
                    fileList.add(vocalFilePath +System.getProperty("file.separator")+ f);
                });

                String outPath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id);
                File zipFile = FileUtil.compress(outPath,fileList);
                String zipFilePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id, zipFile.getName());
                byte[] zipFileByte = FileUtil.download(zipFilePath);

                File deleteFile = new File(zipFilePath);
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
                return zipFileByte;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
    }

    public byte[] getFiletoByte(String contentsCd, String fileName) throws BackendException {
        String soundPath = FileUtil.makePath(this.fileBasePath, this.vocalPath, contentsCd, fileName);
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
