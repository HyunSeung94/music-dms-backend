package com.mesim.sc.service.admin.arrange;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.song.CreativeSongRepository;
import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.repository.rdb.admin.vocal.VocalRepository;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.service.admin.song.CreativeSongService;
import com.mesim.sc.service.admin.vocal.VocalDto;
import com.mesim.sc.service.admin.vocal.VocalService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Value("${file.data.arrange.path}")
    private String arrangePath;

    @Value("${file.data.temp.path}")
    private String fileTempPath;

    @Value("${file.data.csv.path}")
    private String csvPath;


    @Autowired
    private VocalService vocalService;

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
        Optional<Object> optEntity = this.repository.findById(id);

        return optEntity.map(o -> {
            try {
                ArrangeDto dto = (ArrangeDto) toDto(o, 0);
                String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, dto.getId());

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


    public Object add(Object data, MultipartFile[] files,String userId) throws BackendException {
        ArrangeDto savedArrangeDto = (ArrangeDto) this.save(data);

        String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, savedArrangeDto.getId());
        String tempPath = FileUtil.makePath(this.fileBasePath, this.fileTempPath, userId);

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
            String id = list.get(i).get("id").toString();
            String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id);

            FileUtil.fileList(filePath).forEach(f -> {
                if (!f.contains("vdata")) {
                    FileUtil.deleteFile(filePath + System.getProperty("file.separator")+f);
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

        Optional<Vocal> optEntity = this.vocalRepository.findById(id);

        return optEntity.map(o -> {
            try {
                String songFilePath = FileUtil.makePath(this.fileBasePath, this.songPath, o.getSongCd());
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

    @Override
    public void importCsv(MultipartFile[] multipartFile, String userId) throws IOException, BackendException {
        vocalService.getListFileCheck();

        String fileName= multipartFile[0].getOriginalFilename();
        String ext = FileUtil.getExt(fileName);
        String time = DateTimeFormatter.ofPattern("yyyyMMddhhmmss").format(LocalDateTime.now());
        String saveFileName = userId +"_"+ time + ".csv";
        if (!ext.equals("csv")) {
            throw new BackendException("지원하지 않는 파일 형식입니다.");
        }


        String tempPath = FileUtil.makePath(fileBasePath,csvPath,fileTempPath,userId);
        String savePath = FileUtil.makePath(fileBasePath,csvPath,arrangePath,userId);
        File file = new File(tempPath + System.getProperty("file.separator") + fileName);

        InputStream in = new FileInputStream(file);
        try {
            CSV csv = new CSV(true, ',', in );
            List<Arrange> arrangeList = new ArrayList < > ();
            List < String > fieldNames = null;
            if (csv.hasNext()) fieldNames = new ArrayList < > (csv.next());

            while (csv.hasNext()) {
                List < String > x = csv.next();

                Optional<Vocal> vocal = vocalRepository.findById(x.get(0));
                if(vocal.isPresent()){
                    if (vocal.get().getStatus().equals("0")) {
                        throw new BackendException(x.get(0)+"보컬음원 파일이 없습니다..");
                    }
                }else{
                    throw new BackendException(x.get(0)+"의 보컬음원이 없습니다..");
                }

                Arrange arrange = Arrange.builder()
                        .id(x.get(0))
                        .arrangerCd(x.get(1))
                        .arrangeDate(Date.valueOf(x.get(2)))
                        .importYn("Y")
                        .status("1")
                        .regId(userId)
                        .modId(userId)
                        .build();

                arrangeList.add(arrange);

            }
            this.repository.saveAll(arrangeList);
        }finally {
            in.close();
        }
        FileUtil.moveCsvFile(savePath,saveFileName,fileName, tempPath);
        getListFileCheck();
    }

    @Override
    public Object getListFileCheck() {

        List<Arrange> list = this.repository.findAll();
        String[] fileCheck = { "adata.mid", "adata.csv", "adata.wav" };
        List<String> checkList = Arrays.asList(fileCheck);
        List<Arrange> arrangeList = new ArrayList < > ();

        for(int i=0; i<list.size(); i++){
            List<String> outList = new ArrayList<>();
            String id = list.get(i).getId();
            String filePath = FileUtil.makePath(this.fileBasePath, this.vocalPath, id);

            FileUtil.fileList(filePath).forEach(fileName -> {
                String[] fileId= fileName.split("_");
                if(fileId[0].equals(id)){

                    for(String filed : fileCheck){
                        if (fileName.contains(filed)) {
                            outList.add(filed);
                        }
                    }
                }
            });
            Arrange arrange = Arrange.builder()
                 .id(list.get(i).getId())
                 .arrangerCd(list.get(i).getArrangerCd())
                 .arrangeDate(list.get(i).getArrangeDate())
                 .importYn(list.get(i).getImportYn())
                 .status(outList.containsAll(checkList) != true? "0" : "1")
                 .regId(list.get(i).getRegId())
                 .modId(list.get(i).getModId())
                 .build();
                arrangeList.add(arrange);
        }

        this.repository.saveAll(arrangeList);
        return arrangeList;
    }

}
