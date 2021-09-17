package com.mesim.sc.service.admin.vocal;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.song.CreativeSongRepository;
import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.repository.rdb.admin.vocal.VocalRepository;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.service.admin.song.CreativeSongDto;
import com.mesim.sc.service.admin.song.CreativeSongService;
import com.mesim.sc.util.CSV;
import com.mesim.sc.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Value("${file.data.vocal.path}")
    private String vocalPath;

    @Value("${file.data.temp.path}")
    private String fileTempPath;

    @Value("${file.data.csv.path}")
    private String csvPath;

    @Autowired
    private CreativeSongService songService;

    @Autowired
    private CreativeSongRepository creativeSongRepository;

    @Autowired
    @Qualifier("vocalRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        this.selectSortField = "id";
        this.searchFields = new String[]{"id", "songCd", "songSongNm", "singerCd", "singerConsortiumNm", "studioName"};
        this.joinedSortField = new String[]{"song", "singer", "studio"};

        this.addRefEntity("song", "songNm");
        this.addRefEntity("singer", "consortiumNm");
        this.addRefEntity("studio", "name");

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

    public List<Object> getListSelect(String regGroupId, String[] select) {
        Specification<Object> spec = null;

        if (!regGroupId.equals(Integer.toString(CommonConstants.GROUP_ROOT_ID))) {
            if (regGroupId.equals(Integer.toString(CommonConstants.GROUP_CHILL_ROOT_ID))) {
                spec = AdminSpecs.regGroupId(CommonConstants.GROUP_CHILL_ID).or(AdminSpecs.regGroupId(CommonConstants.GROUP_CHILL_ROOT_ID));
            } else if (regGroupId.equals(Integer.toString(CommonConstants.GROUP_FKMP_ROOT_ID))) {
                spec = AdminSpecs.regGroupId(CommonConstants.GROUP_FKMP_ID).or(AdminSpecs.regGroupId(CommonConstants.GROUP_FKMP_ROOT_ID));
            }
        }

        Sort sort = Sort.by(this.selectSortDirection, this.selectSortField);

        if (select != null && select.length > 0) {
            spec = spec == null ? this.getSelectSpec(select) : spec.and(this.getSelectSpec(select));
        }

        if (this.root != null && MapUtils.isNotEmpty(this.root)) {
            spec = spec == null ? this.getNotEqSpec(this.root) : Specification.where(this.getNotEqSpec(this.root)).and(spec);
        }

        List<Object> list = spec == null ? this.repository.findAll(sort) : this.repository.findAll(spec, sort);
        final AtomicInteger i = new AtomicInteger(1);

        return list
                .stream()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement())))
                .collect(Collectors.toList());
    }

    @Override
    public Object get(String id) {
        Optional<Object> optEntity = this.repository.findById(id);

        return optEntity.map(o -> {
            try {
                VocalDto dto = (VocalDto) toDto(o, 0);
                String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, dto.getId());

                List<String> fileNameList = new ArrayList<>();
                FileUtil.fileList(filePath).forEach(f -> {
                    if (f.contains("vdata")) {
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


    public Object add(Object data, MultipartFile[] files, String userId) throws BackendException {
        VocalDto savedVocalDto = (VocalDto) this.save(data);

        String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, savedVocalDto.getId());
        String tempPath = FileUtil.makePath(this.fileBasePath, this.fileTempPath, userId);

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
        List<Object> deleteObjects = ((List<Object>) map.get("data"))
                .stream()
                .map(ExceptionHandler.wrap(object -> this.toEntity(object)))
                .collect(Collectors.toList());

        this.repository.deleteAll(deleteObjects);

        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("data");

        for (int i = 0; i < list.size(); i++) {
            String id = list.get(i).get("id").toString();
            String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id);

            FileUtil.fileList(filePath).forEach(f -> {
                if (f.contains("vdata")) {
                    FileUtil.deleteFile(filePath + System.getProperty("file.separator") + f);
                }
            });
        }

        return true;
    }

    public byte[] fileDownload(String id, String fileName) throws BackendException {
        String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id, fileName);
        return FileUtil.download(filePath);
    }

    public byte[] fileAllDownload(String id) throws BackendException {

        Optional<Object> optEntity = this.repository.findById(id);

        return optEntity.map(o -> {
            try {
                VocalDto dto = (VocalDto) toDto(o, 0);

                String songFilePath = FileUtil.makePath(this.fileBasePath, this.songPath, dto.getSongCd());
                String vocalFilePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id);

                List<String> fileList = new ArrayList<>();
                FileUtil.fileList(songFilePath).forEach(f -> {
                    fileList.add(songFilePath + System.getProperty("file.separator") + f);
                });
                FileUtil.fileList(vocalFilePath).forEach(f -> {
                    if (f.contains("vdata")) {
                        fileList.add(vocalFilePath + System.getProperty("file.separator") + f);
                    }
                });

                String outPath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id);
                File zipFile = FileUtil.compress(outPath, fileList);
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

    @Override
    public void importCsv(MultipartFile[] multipartFile, String userId) throws IOException, BackendException {
        songService.getListFileCheck();

        String fileName = multipartFile[0].getOriginalFilename();
        String ext = FileUtil.getExt(fileName);
        String time = DateTimeFormatter.ofPattern("yyyyMMddhhmmss").format(LocalDateTime.now());
        String saveFileName = userId +"_"+ time + ".csv";
        if (!ext.equals("csv")) {
            throw new BackendException("지원하지 않는 파일 형식입니다.");
        }

        String tempPath = FileUtil.makePath(fileBasePath, csvPath, fileTempPath, userId);
        String savePath = FileUtil.makePath(fileBasePath, csvPath, vocalPath, userId);
        File file = new File(tempPath + System.getProperty("file.separator") + fileName);

        InputStream in = new FileInputStream(file);
        try  {

            CSV csv = new CSV(true, ',', in);
            List<Vocal> vocalList = new ArrayList<>();
            List<String> fieldNames = null;
            if (csv.hasNext()) fieldNames = new ArrayList<>(csv.next());

            while (csv.hasNext()) {
                List<String> x = csv.next();
                if (x.get(0).equals("")) {
                    break;
                }

                Optional<CreativeSong> resultSong = creativeSongRepository.findById(x.get(1));
                if(resultSong.isPresent()){
                    if (resultSong.get().getStatus().equals("0")) {
                        throw new BackendException("창작곡 파일이 없습니다..");
                    }
                }else{
                    throw new BackendException(x.get(1)+"의 창작곡이 없습니다..");
                }

                Vocal vocal = Vocal.builder()
                        .id(x.get(0))
                        .songCd(x.get(1))
                        .singerCd(x.get(2))
                        .recordLength(x.get(3))
                        .recordDate(Date.valueOf(x.get(4)))
                        .vibe(x.get(5))
                        .studioCd(x.get(6))
                        .micNm(x.get(7))
                        .audioIfNm(x.size() >= fieldNames.size() ? x.get(8) : "")
                        .importYn("Y")
                        .status("1")
                        .regId(userId)
                        .modId(userId)
                        .build();

                vocalList.add(vocal);

            }
            this.repository.saveAll(vocalList);

        }finally {
            in.close();
        }
        FileUtil.moveCsvFile(savePath,saveFileName,fileName, tempPath);
        getListFileCheck();
    }

    @Override
    public Object getListFileCheck() {

        List<Vocal> list = this.repository.findAll();
        String[] fileCheck = {"vdata.wav"};
        List<String> checkList = Arrays.asList(fileCheck);
        List<Vocal> vocalList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            List<String> outList = new ArrayList<>();
            String id = list.get(i).getId();
            String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id);

            FileUtil.fileList(filePath).forEach(fileName -> {
                String[] fileId = fileName.split("_");
                if (fileId[0].equals(id)) {

                    for (String filed : fileCheck) {
                        if (fileName.contains(filed)) {
                            outList.add(filed);
                        }
                    }
                }
            });

            Vocal vocal = Vocal.builder()
                .id(list.get(i).getId())
                .songCd(list.get(i).getSongCd())
                .singerCd(list.get(i).getSingerCd())
                .recordLength(list.get(i).getRecordLength())
                .recordDate(list.get(i).getRecordDate())
                .vibe(list.get(i).getVibe())
                .studioCd(list.get(i).getStudioCd())
                .micNm(list.get(i).getMicNm())
                .audioIfNm(list.get(i).getAudioIfNm())
                .importYn(list.get(i).getImportYn())
                .status(outList.containsAll(checkList) != true? "0" : "1")
                .regId(list.get(i).getRegId())
                .modId(list.get(i).getModId())
                .build();
                vocalList.add(vocal);
        }

        this.repository.saveAll(vocalList);
        return vocalList;
    }

}
