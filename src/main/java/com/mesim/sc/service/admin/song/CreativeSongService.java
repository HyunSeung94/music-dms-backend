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

        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath+'/'+savedSongDto.getId());
//        String filePath = FileUtil.makePath(this.fileBasePath, this.songPath);
//        File deleteFile = new File(FileUtil.makePath(filePath, fileName + "." + ext));
//        if (deleteFile.exists()) {
//            deleteFile.delete();
//        }

        try {
            for (MultipartFile file : files) {
                FileUtil.upload(filePath, file.getOriginalFilename(), file);
            }
        } catch (
                IOException e) {
            throw new BackendException("파일 업로드 중 오류 발생", e);
        }

        return savedSongDto;
    }


//    @Override
//    public Object get(String id) {
////        List<String> fileNameList = new ArrayList<>();
////        String filePath = this.fileBasePath+'/'+this.songPath+'/'+id;
////        File rw = new File(filePath);
////
////        /*파일 경로에 있는 파일 리스트 fileList[] 에 넣기*/
////        File[] fileList = rw.listFiles();
////        if(fileList != null){
////            /*fileList에 있는거 for 문 돌려서 출력*/
////            for(File file : fileList) {
////                if(file.isFile()) {
////                    String fileName =  file.getName();
////                    fileNameList.add(fileName);
////                    System.out.println("fileName : " + fileName);
////                }
////            }
////        }
//        Optional<Object> optEntity = this.repository.findById(id);
//        return optEntity.map(o -> {
//            try {
//                return this.toDto(o, 0);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }).orElse(null);
//    }


    public Object fileList(String id){
        List<String> fileNameList = new ArrayList<>();
        String filePath = this.fileBasePath+'/'+this.songPath+'/'+id;
        File rw = new File(filePath);

        /*파일 경로에 있는 파일 리스트 fileList[] 에 넣기*/
        File[] fileList = rw.listFiles();
        if(fileList != null){
            /*fileList에 있는거 for 문 돌려서 출력*/
            for(File file : fileList) {
                if(file.isFile()) {
                    String fileName =  file.getName();
                    fileNameList.add(fileName);
                    System.out.println("fileName : " + fileName);
                }
            }
        }
        return fileNameList != null ? fileNameList : null ;
    }
}
