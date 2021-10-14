package com.mesim.sc.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.code.Code;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.service.admin.vocal.VocalDto;
import com.mesim.sc.util.CSV;
import com.mesim.sc.util.DataTypeUtil;
import com.mesim.sc.util.DateUtil;
import com.mesim.sc.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Transactional
public abstract class CrudService {

    @PersistenceContext
    protected EntityManager entityManager;

    @Value("${file.data.base.path}")
    private String fileBasePath;

    @Value("${file.data.temp.path}")
    private String tempPath;

    @Value("${file.data.csv.path}")
    private String csvPath;


    protected static final String SELECT_FIELD_SPLITTER = ";";
    protected static final String JOIN_FIELD_DELIMITER = ".";
    protected static final String SORT_FIELD_SPLITTER = ";";
    protected static final String SORT_DESC = "desc";
    protected static final String AND = "AND";
    protected static final String OR = "OR";
    private static final String FILE_NAME_PK_SPLITTER = "_";

    protected ObjectMapper mapper = new ObjectMapper();

    protected CrudRepository repository;

    // 페이지, 전체, 선택 목록 조회 시, 기본 검색 조건에 해당하는 필드
    protected String selectField = "id";

    // 페이지, 전체 목록 조회 시, 정렬 기본 및 조인 필드
    protected String defaultSortField = "regDate";
    protected String[] joinedSortField;

    // 페이지, 전체 목록 조회 시, 검색 필드
    protected Set<String> searchFieldSet;
    protected String[] searchFields = new String[]{};
    protected String searchDateField = "regDate";

    // 선택 목록 조회 시, 정렬 필드
    protected String selectSortField = "name";
    protected Sort.Direction selectSortDirection = Sort.Direction.ASC;

    // DTO 변수 목록 중 제외할 조회 컬럼 목록
    protected String[] excludeColumn = new String[]{};

    // 이미지 소스 필드
    protected String imageSrcField;

    // 파일 저장 경로
    protected String fileSavePath;

    // Root ID
    protected Map<String, Object> root = new HashMap<>();

    private List<Object[]> refEntityList = new ArrayList<>();
    private List<String> pkList = new ArrayList<>();
    private String pkType;

    @PostConstruct
    public void init() {
        if (searchFields != null) {
            this.searchFieldSet = new HashSet<>(Arrays.asList(searchFields));
        }

        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Type cls = DataTypeUtil.getGenericType(this.repository.getClass())[0];
        Type[] jpaCls = DataTypeUtil.getGenericType(DataTypeUtil.getClass(cls));
        Class<?> pkCls = DataTypeUtil.getClass(((ParameterizedType) jpaCls[0]).getActualTypeArguments()[1]);
        this.pkType = pkCls.getName();

        if (pkCls.getName().toUpperCase().contains("PK")) {
            this.pkList = Arrays
                    .stream(pkCls.getDeclaredFields())
                    .map(field -> field.getName())
                    .collect(Collectors.toList());
        } else {
            this.pkList.add("id");
        }
    }

    /**
     * 각 서비스의 엔티티에 대한 레포지토리 주입
     */
    abstract public void setRepository(CrudRepository repository);

    /**
     * 해당 Repository 에 맞는 DTO 클래스를 반환
     *
     * @return Dto Class
     */
    abstract protected Class getClazz() throws BackendException;

    /**
     * 검색을 위한 조인 레퍼런스 엔티티 추가
     *
     * @param rootAttrName 조인 필드
     *                    (ex: User.class 의 authority)
     * @param refAttrName 조인 필드로 조인되는 엔티티의 특정 필드명으로, 검색 대상에 포함시킬 필드명
     *                    (ex: User.class 에서 authority 필드에 조인되는 Authority.class 의 name 필드)
     * @param nullable 조인 필드에 NULL 값이 포함될 수 있는 경우
     *                 (Default: false)
     */
    protected void addRefEntity(String rootAttrName, String refAttrName, boolean nullable) {
        if (rootAttrName != null && refAttrName != null) {
            this.refEntityList.add(new Object[]{rootAttrName, refAttrName, nullable});
        } else {
            log.error("검색을 위한 조인 레퍼런스 엔티티 정보가 정확하지 않습니다.");
        }
    }

    protected void addRefEntity(String rootAttrName, String refAttrName) {
        this.addRefEntity(rootAttrName, refAttrName, false);
    }

    /**
     * 검색 키워드 및 조건에 일치하는 페이지 목록 조회
     *
     * @param select 검색 기본값, selectSplitter 없는 경우 selectField Equal 조건
     * @param index 조회 시작 위치
     * @param size 조회 목록 개수
     * @param sortProperties 정렬할 필드명 리스트
     * @param keywords 검색 키워드
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND
     * @param fromDate 검색 시작 날짜
     * @param toDate 검색 마지막 날짜
     * @return 페이지 목록
     */
    public PageWrapper getListPage(String[] select, int index, int size, String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        PageRequest pageRequest = this.getPageRequest(index, size, sortProperties);
        Specification<Object> spec = this.getSpec(select, keywords, searchOp, fromDate, toDate);

        Page<Object> page = spec == null ? this.repository.findAll(pageRequest) : this.repository.findAll(spec, pageRequest);
        PageWrapper result = new PageWrapper(page);
        final AtomicInteger seq = new AtomicInteger(1);

        List<Object> list = page
                .get()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, seq.getAndIncrement() + (result.getNumber() * size))))
                .collect(Collectors.toList());

        result.setList(list);

        return result;
    }

    /**
     * 검색 키워드에 일치하는 전체 목록 조회
     *
     * @param select 검색 기본값, selectSplitter 없는 경우 selectField Equal 조건
     * @param sortProperties 정렬할 필드명 리스트
     * @param keywords 검색 키워드
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND
     * @param fromDate 검색 시작 날짜
     * @param toDate 검색 마지막 날짜
     * @return 전체 목록
     */
    public List<Object> getList(String[] select, String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        Sort sort = this.getSort(sortProperties);
        Specification<Object> spec = this.getSpec(select, keywords, searchOp, fromDate, toDate);

        List<Object> list = spec == null ? this.repository.findAll(sort) : this.repository.findAll(spec, sort);
        final AtomicInteger i = new AtomicInteger(1);

        return list
                .stream()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement())))
                .collect(Collectors.toList());
    }

    /**
     * 전체 데이터 목록 조회
     *
     * @return 전체 데이터 목록
     */
    public List<Object> getList() {
        Specification<Object> spec = null;

        if (this.root != null && MapUtils.isNotEmpty(this.root)) {
            spec = this.getNotEqSpec(this.root);
        }

        List<Object> list = spec == null ? this.repository.findAll() : this.repository.findAll(spec);
        final AtomicInteger i = new AtomicInteger(1);

        return list
                .stream()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement())))
                .collect(Collectors.toList());
    }

    /**
     * 선택 목록 조회
     *
     * @param select 검색 기본값, selectSplitter 없는 경우 selectField Equal 조건
     * @return 선택 목록
     */
    public List<Object> getListSelect(String[] select) {
        Specification<Object> spec = null;
        Sort sort = Sort.by(this.selectSortDirection, this.selectSortField);

        if (select != null && select.length > 0) {
            spec = this.getSelectSpec(select);
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

    /**
     * 선택 목록 다중 조회
     *
     * @param select 검색 기본값, selectSplitter 없는 경우 selectField Equal 조건
     * @return 선택 목록
     */
    public List<List<Object>> getListMultiSelect(String[][] select) {
        List<List<Object>> result = new ArrayList<>();

        for (String[] s : select) {
            result.add(this.getListSelect(s));
        }
        return result;
    }

    /**
     * 조회 컬럼 목록 조회
     *
     * @return 조회 컬럼 목록
     */
    public List<String> getListColumn() throws BackendException {
        return this.toColumn()
                .stream()
                .filter(field -> !Arrays.asList(this.excludeColumn).contains(field))
                .collect(Collectors.toList());
    }

    /**
     * 데이터 상세조회 (PK)
     *
     * @param pk 기본키
     * @return 데이터 DTO
     */
    public Object get(Object pk) {
        if (this.pkList.size() > 0) {
            Map<String, Object> pkMap = (Map<String, Object>) pk;
            Specification<Object> spec = this.getEqSpec(pkMap);
            Optional<Object> optEntity = this.repository.findOne(spec);
            return optEntity.map(ExceptionHandler.wrap(entity -> this.toDto(entity, 0))).orElse(null);
        }
        return null;
    }

    /**
     * 데이터 상세조회
     *
     * @param id ID
     * @return 데이터 DTO
     */
    public Object get(String id) {
        if (this.pkList.size() == 1) {
            return this.pkType.equals("java.lang.Integer") ? this.getByIntegerId(id) : this.getByStringId(id);
        } else {
            return this.getByStringId(id);
        }
    }

    /**
     * 데이터 상세조회 (Integer ID)
     *
     * @param id ID
     * @return 데이터 DTO
     */
    public Object getByIntegerId(String id) {
        try {
            int _id = Integer.parseInt(id);
            Optional<Object> optEntity = this.repository.findById(_id);
            return optEntity.map(ExceptionHandler.wrap(entity -> this.toDto(entity, 0))).orElse(null);
        } catch (NumberFormatException e) {
            // 파라미터 값(ID)이 Integer 가 아닌 경우 String 으로 처리
            return this.getByStringId(id);
        }
    }

    /**
     * 데이터 상세조회 (String ID)
     *
     * @param id ID
     * @return 데이터 DTO
     */
    private Object getByStringId(String id) {
        Optional<Object> optEntity = this.repository.findById(id);
        return optEntity.map(ExceptionHandler.wrap(entity -> this.toDto(entity, 0))).orElse(null);
    }

    /**
     * 저장 (등록 및 수정)
     *
     * @param dto 데이터 DTO
     * @return 등록 및 수정된 데이터 DTO
     */
    public Object save(Object dto) throws BackendException {
        Object entity = this.toEntity(dto);
        Object savedEntity = this.repository.saveAndFlush(entity);
        this.entityManager.refresh(savedEntity);
        return this.toDto(savedEntity, 0);
    }

    /**
     * 등록
     *
     * @param dto 데이터 DTO
     * @return 등록된 데이터 DTO
     */
    public Object add(Object dto) throws BackendException {
        Object exist = this.get(dto);
        if (exist == null) {
            return this.save(dto);
        } else {
            throw new BackendException(CommonConstants.EX_PK_VIOLATION);
        }
    }

    /**
     * 등록 (파일 포함)
     *
     * @param dto 데이터 DTO
     * @param file 파일
     * @return 등록된 데이터 DTO
     */
    @Transactional
    public Object add(Object dto, MultipartFile file) throws BackendException {
        try {
            Object savedDto = this.add(dto);

            if (file != null) {
                String filePath = this.getFilePath(dto);
                String fileName = this.getFileNameFromDto(savedDto);

                FileUtil.upload(filePath, fileName, file);
            }
            return savedDto;
        } catch (IOException e) {
            throw new BackendException("등록 중 오류발생", e);
        }
    }

    /**
     * 등록 (다중 파일 포함)
     *
     * @param dto 데이터 DTO
     * @param files 파일 목록
     * @return NULL : Override 를 통해 사용
     */
    public Object add(Object dto, MultipartFile[] files) throws BackendException {
        return null;
    }

    /**
     * 수정
     *
     * @param dto 데이터 DTO
     * @return 수정된 데이터 DTO
     */
    public Object modify(Object dto) throws BackendException {
        return this.save(dto);
    }

    /**
     * 수정 (파일 포함)
     *
     * @param dto 데이터 DTO
     * @param file 파일
     * @return 수정된 데이터 DTO
     */
    @Transactional
    public Object modify(Object dto, MultipartFile file) throws BackendException {
        try {
            Map<String, Object> dtoMap = this.mapper.convertValue(this.get(dto), Map.class);

            Object savedDto = this.modify(dto);

            if (file != null) {
                String filePath = this.getFilePath(dto);
                String fileName = this.getFileNameFromDto(savedDto);

                if (this.imageSrcField != null && dtoMap.get(this.imageSrcField) != null) {
                    String originExt = FileUtil.getExt(dtoMap.get(this.imageSrcField).toString());
                    File deleteFile = new File(FileUtil.makePath(filePath, fileName + "." + originExt));
                    if (deleteFile.exists()) {
                        deleteFile.delete();
                    }
                }

                FileUtil.upload(filePath, fileName, file);
            }
            return savedDto;
        } catch (IOException e) {
            throw new BackendException("수정 중 오류발생", e);
        }
    }

    /**
     * 수정 (다중 파일 포함)
     *
     * @param dto 데이터 DTO
     * @param files 파일 목록
     * @param delete 삭제 파일 목록
     * @return NULL : Override 를 통해 사용
     */
    public Object modify(Object dto, MultipartFile[] files, Object delete) throws BackendException {
        return null;
    }

    /**
     * 삭제 (Entity)
     *
     * @param dto 삭제할 데이터 DTO (Map: DTO, List: DTO List)
     * @return 성공/실패 여부
     */
    public boolean delete(Object dto) throws BackendException {
        Map<String, Object> dtoMap = (Map<String, Object>) dto;
        Object data = dtoMap.get("data");

        if (data instanceof Map) {
            // DTO
            Object entity = this.toEntity(data);
            this.deleteFile(data);
            this.repository.delete(entity);
        } else {
            // DTO List
            List<Object> entities = ((List<Object>) data)
                    .stream()
                    .map(ExceptionHandler.wrap(object -> this.toEntity(object)))
                    .collect(Collectors.toList());
            this.deleteFiles((List<Object>) data);
            this.repository.deleteAll(entities);
        }

        return true;
    }

    /**
     * 삭제
     *
     * @param id 삭제할 데이터 ID
     * @return 성공/실패 여부
     */
    public boolean delete(String id) {
        return this.pkType.equals("java.lang.Integer") ? this.deleteByIntegerId(id) : this.deleteByStringId(id);
    }

    /**
     * 삭제 (Integer ID)
     *
     * @param id 삭제할 데이터 ID
     * @return 성공/실패 여부
     */
    public boolean deleteByIntegerId(String id) {
        try {
            int _id = Integer.parseInt(id);
            this.deleteFileById(_id);
            this.repository.deleteById(_id);
            return true;
        } catch (NumberFormatException e) {
            // 파라미터 값(ID)이 Integer 가 아닌경우 String 으로 처리
            return this.deleteByStringId(id);
        }
    }

    /**
     * 삭제 (String ID)
     *
     * @param id 삭제할 데이터 ID
     * @return 성공/실패 여부
     */
    public boolean deleteByStringId(String id) {
        this.deleteFileById(id);
        this.repository.deleteById(id);
        return true;
    }

    /**
     * 여러 데이터 파일 삭제
     * @param dtoList 삭제할 데이터 DTO 목록
     */
    public void deleteFiles(List<Object> dtoList) {
        dtoList.forEach(this::deleteFile);
    }

    /**
     * 데이터 파일 삭제
     * @param dto 삭제할 데이터 DTO
     */
    public void deleteFile(Object dto) {
        if (this.imageSrcField == null) {
            return;
        }

        if (this.imageSrcField.contains(".")) {
            Map<String, Object> dtoMap = this.mapper.convertValue(this.get(dto), Map.class);

            String[] fieldSplit = this.imageSrcField.split("\\.");
            String refAttr = fieldSplit[0];
            String refField = fieldSplit[1];

            Object refDto = dtoMap.get(refAttr);

            if (refDto instanceof List) {
                ((List<Object>) refDto).forEach(obj -> {
                    this.deleteRefFile(dto, obj, refField);
                });
            } else {
                this.deleteRefFile(dto, refDto, refField);
            }
        } else {
            this.deleteTargetFile(dto);
        }
    }

    /**
     * 데이터 파일 삭제 (Integer ID)
     * @param id 삭제할 데이터 ID
     */
    public void deleteFileById(int id) {
        Object dto = this.getByIntegerId(Integer.toString(id));
        this.deleteFile(dto);
    }

    /**
     * 데이터 파일 삭제 (String ID)
     * @param id 삭제할 데이터 ID
     */
    public void deleteFileById(String id) {
        Object dto = this.getByStringId(id);
        this.deleteFile(dto);
    }

    /**
     * 데이터 파일 삭제
     * @param dto 데이터 DTO
     */
    public void deleteTargetFile(Object dto) {
        Map<String, Object> dtoMap = this.mapper.convertValue(dto, Map.class);

        String filePath = this.getFilePath(dto);
        String fileName = this.getFileNameFromDto(dto);

        String originExt = FileUtil.getExt(dtoMap.get(this.imageSrcField).toString());
        File deleteFile = new File(FileUtil.makePath(filePath, fileName + "." + originExt));
        if (deleteFile.exists()) {
            deleteFile.delete();
        }
    }

    /**
     * 참조 데이터 파일 삭제
     * @param dto 데이터 DTO
     * @param refDto 참조 데이터 DTO
     * @param refField 참조 데이터 파일 경로 컬럼
     */
    public void deleteRefFile(Object dto, Object refDto, String refField) {
        Map<String, Object> refDtoMap = this.mapper.convertValue(refDto, Map.class);

        String filePath = this.getFilePath(dto);
        String fileName = refDtoMap.get("id").toString();

        String originExt = FileUtil.getExt(refDtoMap.get(refField).toString());
        File deleteFile = new File(FileUtil.makePath(filePath, fileName + "." + originExt));
        if (deleteFile.exists()) {
            deleteFile.delete();
        }
    }

    /**
     * 미리보기 (PK)
     *
     * @param pk 기본키, refId (참조 ID)
     * @return 미리보기 이미지 및 모델 등의 파일
     */
    public byte[] preview(Object pk) throws BackendException {
        if (this.imageSrcField == null) {
            return null;
        }

        Map<String, Object> pkMap = (Map<String, Object>) pk;
        Object refId = pkMap.get("refId");
        return this.getImage(this.get(pk), refId);
    }

    /**
     * 미리보기
     *
     * @param id ID
     * @param refId 참조 ID
     * @return 미리보기 이미지 및 모델 등의 파일
     */
    public byte[] preview(Object id, Object refId) throws BackendException {
        if (this.imageSrcField == null) {
            return null;
        }

        if (this.pkType.equals("java.lang.Integer")) {
            return this.getImage(this.getByIntegerId(id.toString()), refId);
        } else {
            return this.getImage(this.getByStringId(id.toString()), refId);
        }
    }

    /**
     * 파일 저장경로 가져오기
     * @param dto 데이터 DTO
     * @return 파일 저장경로
     */
    public String getFilePath(Object dto) {
        Map<String, Object> dtoMap = this.mapper.convertValue(dto, Map.class);

        String path = this.fileSavePath == null ? this.fileBasePath : FileUtil.makePath(this.fileBasePath, this.fileSavePath);

        if (path.contains("{") && path.contains("}")) {
            int stIndex = path.indexOf("{") + 1;
            int endIndex = path.indexOf("}");
            String replaceTxt = path.substring(stIndex, endIndex);
            String replaceData = dtoMap.get(replaceTxt).toString();

            if (replaceData != null) {
                path = path.replace("{" + replaceTxt + "}", replaceData);
            }
        }
        return path;
    }

    /**
     * 파일 저장경로 가져오기
     * @return 파일 저장경로
     */
    public String getFilePath() {
        return this.fileSavePath == null ? this.fileBasePath : FileUtil.makePath(this.fileBasePath, this.fileSavePath);
    }

    /**
     * 파일 이름 가져오기
     * @param dto 데이터 DTO
     * @return 파일 명
     */
    public String getFileNameFromDto(Object dto) {
        Map<String, Object> dtoMap = this.mapper.convertValue(dto, Map.class);
        String fileName = null;

        for (int i = 0; i < this.pkList.size(); i++) {
            String pkVal = dtoMap.get(this.pkList.get(i)).toString();
            if (i == 0) {
                fileName = pkVal;
            } else {
                fileName = fileName + this.FILE_NAME_PK_SPLITTER + pkVal;
            }
        }
        return fileName;
    }

    /**
     * 미리보기
     *
     * @param dto 데이터 DTO
     * @param refId 참조 ID
     * @return 미리보기 이미지
     */
    public byte[] getImage(Object dto, Object refId) throws BackendException {
        if (dto != null) {
            Map<String, Object> dtoMap = this.mapper.convertValue(dto, Map.class);

            String imgPath = this.getFilePath(dto);
            String fileName;
            String refField = null;

            if (this.imageSrcField.contains(".") && refId != null) {
                fileName = refId.toString();

                String[] fieldSplit = this.imageSrcField.split("\\.");
                String refAttr = fieldSplit[0];
                refField = fieldSplit[1];

                Object refDto = dtoMap.get(refAttr);

                if (refDto instanceof List) {
                    dto = ((List<Object>) refDto)
                            .stream()
                            .filter(obj -> ((Map<String, Object>) obj).get("id").toString().equals(refId.toString()))
                            .findAny()
                            .orElse(null);
                } else {
                    dto = refDto;
                }

                if (dto == null) {
                    return null;
                }
            } else {
                fileName = this.getFileNameFromDto(dto);
            }

            dtoMap = this.mapper.convertValue(dto, Map.class);

            if (this.imageSrcField == null || dtoMap.get(this.imageSrcField) == null) {
                return null;
            }

            String imgSrc = refField == null ? dtoMap.get(this.imageSrcField).toString() : dtoMap.get(refField).toString();
            String imgName = fileName + "." + FileUtil.getExt(imgSrc);
            File imgFile = new File(FileUtil.makePath(imgPath, imgName));

            try {
                if (imgSrc.contains(".svg")) {
                    imgFile = FileUtil.svgToPng(imgFile);
                }

                if (imgFile.length() > 0) {
                    InputStream is = new FileInputStream(imgFile);
                    byte[] imgBytes = IOUtils.toByteArray(is);

                    is.close();

                    return imgBytes;

                } else {
                    return null;
                }
            } catch (Exception e) {
                throw new BackendException("이미지 읽는 중 오류발생", e);
            }
        }

        return null;
    }

    /**
     * 내보내기
     *
     * @param sortProperties 정렬할 필드명 리스트
     * @param keywords 검색 키워드
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND
     * @param fromDate 검색 시작 날짜
     * @param toDate 검색 마지막 날짜
     * @return CSV 파일
     */
    public byte[] export(String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        String csvFile = null;
        BufferedWriter bw = null;
        CSVPrinter printer = null;

        try {
            File folder = new File(this.fileBasePath);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = "temp_" + System.currentTimeMillis() + ".csv";
            csvFile = FileUtil.makePath(this.fileBasePath, fileName);
            String[] columns = this.toColumn().toArray(new String[0]);

            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "MS949"));
            printer = new CSVPrinter(bw, CSVFormat.DEFAULT.withHeader(columns));

            List<Object> dtoList = this.getList(null, sortProperties, keywords, searchOp, fromDate, toDate);
            CSVPrinter finalPrinter = printer;
            dtoList.forEach(dto -> {
                Map<String, Object> dtoMap = this.mapper.convertValue(dto, Map.class);

                ArrayList<String> values = new ArrayList<>();
                for (String col : columns) {
                    if (dtoMap.get(col) != null) {
                        values.add(String.valueOf(dtoMap.get(col)));
                    } else {
                        values.add("");
                    }
                }

                try {
                    finalPrinter.printRecord(values.toArray());
                    finalPrinter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            return FileUtil.download(csvFile);

        } catch (IOException e) {
            throw new BackendException("내보내기 중 오류발생", e);
        } finally {
            File file = new File(csvFile);

            try {
                if (bw != null) {
                    bw.close();
                    if (file.exists()) {
                        file.delete();
                    }
                }
                if (printer != null) {
                    printer.close();
                }
            } catch (Exception e) {
                throw new BackendException("내보내기 자원 해제 중 오류발생", e);
            }
        }
    }

    /**
     * 페이지 목록 조회를 위한 PageRequest 생성
     *
     * @param index 시작위치
     * @param size  조회할 목록 크기
     * @param sortProperties 정렬할 필드명 리스트
     * @return PageRequest
     */
    protected PageRequest getPageRequest(int index, int size, String[] sortProperties) {
        return PageRequest.of(index, size, getSort(sortProperties));
    }

    /**
     * 페이지 목록 조회를 위한 Sort 생성
     *
     * @param sortProperties 정렬할 필드명 리스트
     * @return Sort
     */
    protected Sort getSort(String[] sortProperties) {
        if (sortProperties != null && sortProperties.length > 0) {
            List<Sort.Order> sortPropertyList = new ArrayList<>();

            for (String sortProperty : sortProperties) {

                String[] sortSplit = sortProperty.split(SORT_FIELD_SPLITTER);

                if (sortSplit.length > 0) {
                    String prop = sortSplit[0];
                    String order = sortSplit[1];

                    // 정렬 필드가 조인객체의 필드인 경우
                    if (this.joinedSortField != null) {
                        Optional<String> optJoin = Arrays.stream(this.joinedSortField).filter(prop::startsWith).findFirst();
                        if (optJoin.isPresent()) {
                            String field = optJoin.get();

                            String joinField = prop.replaceFirst(field, "");
                            String firstChar = joinField.substring(0, 1).toLowerCase();
                            joinField = firstChar + joinField.substring(1);

                            prop = String.join(JOIN_FIELD_DELIMITER, field, joinField);
                        }
                    }
                    sortPropertyList.add(order.equals(SORT_DESC) ? Sort.Order.desc(prop) : Sort.Order.asc(prop));
                }
            }
            return Sort.by(sortPropertyList);
        } else {
            return Sort.by(Sort.Direction.ASC, this.defaultSortField);
        }
    }

    /**
     * 종합 Specification 생성
     *
     * @param select 검색 기본값, selectSplitter 없는 경우 selectField Equal 조건
     * @param keywords 검색 키워드
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND
     * @param fromDate 검색 시작 날짜
     * @param toDate 검색 마지막 날짜
     * @return 검색 Specification
     */
    protected Specification<Object> getSpec(String[] select, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        Specification<Object> spec = null;

        if (select != null && select.length > 0) {
            spec = this.getSelectSpec(select);
        }

        if (fromDate != null & toDate != null) {
            spec = spec == null ? this.getDateSpec(fromDate, toDate) : Specification.where(this.getDateSpec(fromDate, toDate)).and(spec);
        }

        if (keywords != null && keywords.length > 0) {
            spec = spec == null ? getSearchSpec(keywords, this.searchFieldSet, searchOp) : Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        if (this.root != null && MapUtils.isNotEmpty(this.root)) {
            spec = spec == null ? this.getNotEqSpec(this.root) : Specification.where(this.getNotEqSpec(this.root)).and(spec);
        }

        return spec;
    }

    /**
     * 기본 검색에 필요한 Specification 생성
     *
     * @param select 검색 기본값, selectSplitter 없는 경우 selectField Equal 조건
     * @return
     */
    protected Specification<Object> getSelectSpec(String[] select) {
        Specification<Object> spec = null;

        for (String s : select) {
            String field = this.selectField;
            String value = s;

            if (s.contains(this.SELECT_FIELD_SPLITTER)) {
                field = s.split(this.SELECT_FIELD_SPLITTER)[0];
                value = s.split(this.SELECT_FIELD_SPLITTER)[1];
            }

            Specification<Object> finalSpec = spec;
            String finalField = field;
            String finalValue = value;

            spec = spec == null ?
                    (Specification<Object>) (root, query, cb) -> cb.equal(root.get(finalField), finalValue) :
                    Specification.where((root, query, cb) -> cb.equal(root.get(finalField), finalValue)).and(finalSpec);
        }
        return spec;
    }

    /**
     * 날짜 검색에 필요한 Specification 생성
     *
     * @param fromDate 검색 시작 날짜
     * @param toDate 검색 종료 날짜
     * @return 날짜 검색 Specification
     */
    protected Specification<Object> getDateSpec(String fromDate, String toDate) {
        return (Specification<Object>) (root, query, cb) -> cb.between(root.get(this.searchDateField), DateUtil.getInitFromDate(fromDate), DateUtil.getInitToDate(toDate));
    }

    /**
     * 키워드 검색에 필요한 Specification 생성
     *
     * @param keywords 검색 키워드
     * @param fields 도메인 모델의 검색할 컬럼
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND
     * @return 키워드 검색 Specification
     */
    protected Specification<Object> getSearchSpec(String[] keywords, Set<String> fields, String searchOp) throws BackendException {
        if (searchOp.equalsIgnoreCase(AND) || searchOp.equalsIgnoreCase(OR)) {
            Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
            Collection<String> likeKeywords = new ArrayList<>();

            keywordSet.forEach(keyword -> {
                likeKeywords.add(keyword.contains("%") ? keyword : "%" + keyword + "%");
            });
            return this.getJoinSearchSpec(likeKeywords, fields, searchOp);
        } else {
            throw new BackendException("검색 조건 오류 (" + searchOp + " option is not supported)");
        }
    }

    /**
     * 키워드 검색에 필요한 각 도메인 모델의 Specification 생성
     * 추가적인 Join 컬럼의 Specification 생성
     *
     * @param keywords 검색 키워드
     * @param fields 도메인 모델의 검색할 컬럼
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND
     * @return 키워드 검색 Specification
     */
    protected Specification<Object> getJoinSearchSpec(Collection<String> keywords, Set<String> fields, String searchOp) {
        return new Specification<Object>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<>();
                List<Predicate> finalPredicates = new ArrayList<>();

                // 검색을 위한 조인 레퍼런스 엔티티 정의
                List<Join> joinList = new ArrayList<>();

                refEntityList.forEach(refEntity -> {
                    String rootAttributeName = (String) refEntity[0];
                    boolean nullable = (boolean) refEntity[2];
                    joinList.add(nullable ? root.join(rootAttributeName, JoinType.LEFT) : root.join(rootAttributeName, JoinType.INNER));
                });

                // 검색 키워드 별 키워드-어트리뷰트(루트 엔티티, 레퍼런스 엔티티) Predicate 생성
                keywords.forEach(keyword -> {
                    root.getModel().getDeclaredSingularAttributes()
                            .stream()
                            .filter(o -> fields.contains(o.getName()))
                            .forEach(o -> {
                                Predicate predicate = builder.like(root.get(o.getName()), keyword);
                                predicates.add(predicate);
                            });

                    for (int i = 0; i < joinList.size(); i++) {
                        String refAttrName = (String) refEntityList.get(i)[1];
                        Join join = joinList.get(i);
                        Predicate predicate = builder.like(join.get(refAttrName), keyword);
                        predicates.add(predicate);
                    }

                    if (searchOp.equals(OR)) {
                        finalPredicates.addAll(predicates);
                    } else {
                        Predicate compound = builder.or(predicates.toArray(new Predicate[predicates.size()]));
                        finalPredicates.add(compound);
                    }

                    predicates.clear();
                });

                Predicate[] array = finalPredicates.toArray(new Predicate[finalPredicates.size()]);

                return searchOp.equals(OR) ? builder.or(array) : builder.and(array);
            }
        };
    }

    /**
     * 데이터와 같은 Specification 생성
     * @param data 데이터
     * @return 데이터와 같은 Specification
     */
    protected Specification<Object> getEqSpec(Map<String, Object> data) {
        Specification<Object> spec = null;

        for (int i = 0; i < this.pkList.size(); i++) {
            String pk = this.pkList.get(i);
            if (i == 0) {
                spec = ((Specification<Object>) (root, query, cb) -> cb.equal(root.get(pk), data.get(pk)));
            } else {
                spec = ((Specification<Object>) (root, query, cb) -> cb.equal(root.get(pk), data.get(pk))).and(spec);
            }
        }

        return spec;
    }

    /**
     * 데이터와 같지 않은 조건 Specification 생성
     * @param data 데이터
     * @return 데이터와 같지 않은 조건 Specification
     */
    protected Specification<Object> getNotEqSpec(Map<String, Object> data) {
        Specification<Object> spec = null;

        for (int i = 0; i < this.pkList.size(); i++) {
            String pk = this.pkList.get(i);
            if (i == 0) {
                spec = ((Specification<Object>) (root, query, cb) -> cb.notEqual(root.get(pk), data.get(pk)));
            } else {
                spec = ((Specification<Object>) (root, query, cb) -> cb.notEqual(root.get(pk), data.get(pk))).and(spec);
            }
        }

        return spec;
    }

    /**
     * Root 여부 확인
     *
     * @param id ID
     * @return Root 여부
     */
    protected boolean isRootById(Object id) {
        if (this.root == null || MapUtils.isEmpty(this.root)) {
            return false;
        }
        return id == this.root.get(this.pkList.get(0));
    }

    /**
     * Root 여부 확인 (PK)
     * @param o 기본키
     * @return Root 여부
     */
    protected boolean isRoot(Object o) {
        if (this.root == null || MapUtils.isEmpty(this.root)) {
            return false;
        }

        Map<String, Object> data = this.mapper.convertValue(o, Map.class);

        for (int i = 0; i < this.pkList.size(); i++) {
            String pk = this.pkList.get(i);
            boolean checked = data.get(pk) == this.root.get(pk);
            if (!checked) {
                return false;
            }
        }
        return true;
    }

    /**
     * DTO 클래스의 선언된 변수 목록을 가져오는 메소드
     * 각 패키지의 abstract class 에서 작성 (ex: AdminService.class 참조)
     *
     * @return 변수 목록
     */
    protected List<String> toColumn() throws BackendException {
        try {
            Class clazz = this.getClazz();
            ArrayList<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

            while (clazz.getSuperclass() != null) {
                clazz = clazz.getSuperclass();
                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            }

            return fields
                    .stream()
                    .map(Field::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BackendException("변수 목록 조회 중 오류발생, " + e.getMessage());
        }
    }

    /**
     * Entity 클래스를 DTO 클래스로 생성하는 메소드
     * 각 패키지의 abstract class 에서 작성 (ex: AdminService.class 참조)
     *
     * @param o Entity
     * @param seq 번호
     * @return DTO
     */
    protected Object toDto(Object o, int seq) throws BackendException {
        Class clazz = null;

        try {
            clazz = getClazz();
            Object object = clazz.getDeclaredConstructor(o.getClass()).newInstance(o);

            if (object instanceof CrudDto) {
                CrudDto dto = (CrudDto) object;
                if (dto != null && seq > 0)
                    dto.setSeq(seq);
                return dto;
            } else if (object instanceof CrdDto) {
                CrdDto dto = (CrdDto) object;
                if (dto != null && seq > 0)
                    dto.setSeq(seq);
                return dto;
            } else if (object instanceof SeqDto) {
                SeqDto dto = (SeqDto) object;
                if (dto != null && seq > 0)
                    dto.setSeq(seq);
                return dto;
            }

            return object;
        } catch(Exception e) {
            throw new BackendException(clazz + " DTO 변환 중 오류발생, " + e.getMessage());
        }
    }

    /**
     * Dto 클래스를 Entity 클래스 생성하는 메소드
     * 각 패키지의 abstract class 에서 작성 (ex: AdminService.class 참조)
     *
     * @param o DTO
     * @return Entity
     */
    protected Object toEntity(Object o) throws BackendException {
        Class clazz = null;

        try {
            if (o instanceof Map) {
                clazz = getClazz();
                o = mapper.convertValue(o, clazz);
            }

            Method method = o.getClass().getMethod("toEntity");
            return method.invoke(o);
        } catch (Exception e) {
            throw new BackendException(clazz + " Entity 변환 중 오류발생, " + e.getMessage());
        }
    }

    /**
     * 기간별 통계 쿼리 조회
     * @param select
     * @param group
     * @param groupSub
     * @param table
     * @param standard
     * @param standardUnit
     * @param rowPeriod
     * @param rowPeriodUnit
     * @param toDate
     * @param fromDate
     * @return
     * @throws BackendException
     * @throws ParseException
     */
    public List<Object> getRowPeriod(String select, String group, String groupSub, String  table, String standard, String standardUnit, int rowPeriod, String rowPeriodUnit, String toDate, String fromDate) throws BackendException, ParseException {
        String[] bundleList = standard.split("#");
        String dataSt;
        String dataSelect;
        String dataGroup = " GROUP BY " + group;
        String dataFrom;
        String fmat = "'yyyy-MM-DD'";
        String[] listKeys;
        String parseFormat;
        if(toDate != null && fromDate != null) {
            parseFormat = "yyyy-MM-dd HH:mm";
        } else {
            parseFormat = "yyyy-MM-dd";
        }
        SimpleDateFormat df = new SimpleDateFormat(parseFormat);
        Calendar calFrom = Calendar.getInstance();
        Calendar calTo = Calendar.getInstance();
        List<Object[]> list;

        try {
            if (toDate != null && fromDate != null) { // 기간 설정이 있을 시
                calFrom.setTime(df.parse(fromDate));
                calTo.setTime(df.parse(toDate));

                if (rowPeriodUnit.toUpperCase().equals("YEAR")) {
                    fmat = "'YYYY'";
                } else if (rowPeriodUnit.toUpperCase().equals("MONTH")) {
                    fmat = "'YYYY-MM'";
                } else if (rowPeriodUnit.toUpperCase().equals("DATE")) {
                    fmat = "'YYYY-MM-DD'";
                } else if (rowPeriodUnit.toUpperCase().equals("HOUR")) {
                    fmat = "'YYYY-MM-DD HH24'";
                }
            }

            // 각 select init
            dataSelect = "TO_CHAR(reg_date, " + fmat + ") as dt";
            int unit;

            if (rowPeriodUnit.toUpperCase().equals("YEAR")) {
                unit = Calendar.YEAR;
            } else if (rowPeriodUnit.toUpperCase().equals("MONTH")) {
                unit = Calendar.MONTH;
            } else if (rowPeriodUnit.toUpperCase().equals("DATE")) {
                unit = Calendar.DATE;
            } else {
                unit = Calendar.HOUR;
            }
            calFrom.add(unit, rowPeriod);
            calTo.add(unit, rowPeriod);

            if (group == null || group.equals("null")) {
                if (bundleList.length > 1) {
                    listKeys = ("dt#" + standard).split("#");
                    for (String bundle : bundleList) {
                        dataSelect += "," + standardUnit + "(x." + bundle + ") ";
                    }
                    dataGroup = " GROUP BY TO_CHAR(reg_date, " + fmat + ")";

                    // 각 from init
                    dataFrom =
                            " SELECT " + dataSelect +
                                    " FROM " + table + " x " +
                                    " WHERE (reg_date BETWEEN to_timestamp('" + fromDate + "', 'YYYY-MM-DD HH24:MI:SS') AND to_timestamp('" + toDate + "', 'YYYY-MM-DD HH24:MI:SS'))" +
                                    dataGroup;

                } else {
                    String keyTemp = "";
                    dataSelect = "";
                    dataGroup = "";
                    while (calFrom.compareTo(calTo) == -1) {
                        String from = "";
                        String to = "";
                        if (rowPeriodUnit.toUpperCase().equals("YEAR")) {
                            from = df.format(calFrom.getTime()).split("-")[0];
                            calFrom.add(unit, rowPeriod);
                            to = df.format(calFrom.getTime()).split("-")[0];
                        } else if (rowPeriodUnit.toUpperCase().equals("MONTH")) {
                            from = df.format(calFrom.getTime()).split("-")[0] + "-" + df.format(calFrom.getTime()).split("-")[1];
                            calFrom.add(unit, rowPeriod);
                            to = df.format(calFrom.getTime()).split("-")[0] + "-" + df.format(calFrom.getTime()).split("-")[1];
                        } else if (rowPeriodUnit.toUpperCase().equals("DATE")) {
                            from = df.format(calFrom.getTime()).split(" ")[0];
                            calFrom.add(unit, rowPeriod);
                            to = df.format(calFrom.getTime()).split(" ")[0];
                        } else if (rowPeriodUnit.toUpperCase().equals("HOUR")) {
                            from = df.format(calFrom.getTime()).split(":")[0];
                            calFrom.add(unit, rowPeriod);
                            to = df.format(calFrom.getTime()).split(":")[0];
                        }
                        keyTemp += "#" + from;
                        String between = "(reg_date BETWEEN to_timestamp('" + from + "', " + fmat + ") AND to_timestamp('" + to + "', " + fmat + "))";
                        String subGroup = " GROUP BY " + group;
                        dataSelect += ",ROUND((SELECT " + standardUnit + "(" + "s." + standard + ") " +
                                "FROM " + table + " s " +
                                "WHERE " + between + "), 0)";
                    }
                    dataSelect = dataSelect.replaceFirst(",", "");
                    keyTemp = keyTemp.replaceFirst("#", "");
                    listKeys = keyTemp.split("#");

                    // 각 from init
                    dataFrom =
                            " SELECT " + dataSelect +
                                    " FROM " + table + " x " +
                                    " WHERE (reg_date BETWEEN to_timestamp('" + fromDate + "', 'YYYY-MM-DD HH24:MI:SS') AND to_timestamp('" + toDate + "', 'YYYY-MM-DD HH24:MI:SS'))";
                }
            } else {
                String keyTemp = group;
                dataSelect = "x." + group;
                while (calFrom.compareTo(calTo) == -1) {
                    String from = "";
                    String to = "";
                    if (rowPeriodUnit.toUpperCase().equals("YEAR")) {
                        from = df.format(calFrom.getTime()).split("-")[0];
                        calFrom.add(unit, rowPeriod);
                        to = df.format(calFrom.getTime()).split("-")[0];
                    } else if (rowPeriodUnit.toUpperCase().equals("MONTH")) {
                        from = df.format(calFrom.getTime()).split("-")[0] + "-" + df.format(calFrom.getTime()).split("-")[1];
                        calFrom.add(unit, rowPeriod);
                        to = df.format(calFrom.getTime()).split("-")[0] + "-" + df.format(calFrom.getTime()).split("-")[1];
                    } else if (rowPeriodUnit.toUpperCase().equals("DATE")) {
                        from = df.format(calFrom.getTime()).split(" ")[0];
                        calFrom.add(unit, rowPeriod);
                        to = df.format(calFrom.getTime()).split(" ")[0];
                    } else if (rowPeriodUnit.toUpperCase().equals("HOUR")) {
                        from = df.format(calFrom.getTime()).split(":")[0];
                        calFrom.add(unit, rowPeriod);
                        to = df.format(calFrom.getTime()).split(":")[0];
                    }
                    keyTemp += "#" + from;
                    String between = "(reg_date BETWEEN to_timestamp('" + from + "', " + fmat + ") AND to_timestamp('" + to + "', " + fmat + "))";
                    String whereSub = "s." + group + " = " + "x." + group + " " + "AND ";
                    String subGroup = " GROUP BY " + group;
                    if (group == null || group.equals("null") || group.isEmpty()) {
                        whereSub = "";
                        subGroup = "";
                    }
                    dataSelect += ",ROUND((SELECT " + standardUnit + "(" + "s." + standard + ") " +
                            "FROM " + table + " s " +
                            "WHERE " + whereSub + between +
                            subGroup + "), 0)";
                }
                listKeys = keyTemp.split("#");

                // 각 from init
                dataFrom =
                        " SELECT " + dataSelect +
                                " FROM " + table + " x " +
                                " WHERE (reg_date BETWEEN to_timestamp('" + fromDate + "', 'YYYY-MM-DD HH24:MI:SS') AND to_timestamp('" + toDate + "', 'YYYY-MM-DD HH24:MI:SS'))" +
                                dataGroup;
            }

            String jpql = dataFrom;

            Query query = entityManager.createQuery(jpql, Object[].class);

            if (dataGroup == "") {
                query.setMaxResults(1);
            }
            list = query.getResultList();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackendException("Query Period 조회 중 오류발생, " + e.getMessage());
        }

        List<Object> result = new ArrayList<>();

        list.forEach(o -> {
            Map<String, Object> temp = new HashMap<>();
            for (int i = 0; i < listKeys.length; i++) {
                if (o[i] == null) {
                    temp.put(listKeys[i], 0);
                } else {
                    temp.put(listKeys[i], o[i]);
                }
            }
            result.add(temp);
        });

        return result;
    }

    /**
     * 그룹별 통계 쿼리 조회
     * @param select
     * @param group
     * @param groupSub
     * @param table
     * @param standard
     * @param standardUnit
     * @return
     * @throws BackendException
     */
    public List<Object> getRowGroup(String select, String group, String groupSub, String  table, String standard, String standardUnit, String toDate, String fromDate) throws BackendException {
        String standardItem;
        String standardUnitItem;
        String selectItem;
        String selectItemSub = "";
        String tableItem = table;
        String groupItem = "o." + group;
        String groupItemSub = "s." + group;
        String where = "";
        String groupBy = "";
        String[] listKeys;


        if (groupSub != null) {
            String[] subList = groupSub.split("#");
            // 그룹 아이템 목록 컬럼
            for (String item : subList) {
                selectItemSub += ", ROUND(" + standardUnit + "(CASE WHEN " + group + " = '" + item + "' THEN " + groupItem + " ELSE " + null + " END), 0)";
            }
            // 그룹 아이템 목록 조건
            where = "WHERE  (";
            for (int i = 0; i < subList.length; i++) {
                subList[i] = groupItem + "='" + subList[i] + "'";
            }
            where += StringUtils.join(subList," OR ", 0, subList.length) + ")";
        }

        if (select != null) { // display item 요소들 다수 일 시
            if (select.equals("*")) { // display item 모두 조회
                selectItem = selectItemSub.replaceFirst(",", "");
                listKeys = groupSub.split("#");
            } else {
                selectItem = select + selectItemSub;
                groupBy = " GROUP BY " + select;
                listKeys = (select + '#' + groupSub).split("#|,");
            }
        } else {
            selectItem = groupItem;
            listKeys = groupItem.split("#");
        }

        if (toDate != null && fromDate != null) {
            if (where.equals("")) {
                where += "WHERE  ";
            } else {
                where += " AND  ";
            }
            where += "(reg_date BETWEEN to_timestamp('" + fromDate + "', 'YYYY-MM-DD HH24:MI:SS') AND to_timestamp('" + toDate + "', 'YYYY-MM-DD HH24:MI:SS'))";
        }

        List<Object[]> list;
        try {
            String jpql = "SELECT " + selectItem + " FROM " + tableItem + " o " + where + groupBy;
            Query query = entityManager.createQuery(jpql, Object[].class);

            list = query.getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackendException("Query 조회 중 오류발생, " + e.getMessage());
        }

        List<Object> result = new ArrayList<>();

        list.forEach(o -> {
            Map<String, Object> temp = new HashMap<>();
            for (int i = 0; i < listKeys.length; i++) {
                if (o[i] == null) {
                    temp.put(listKeys[i], 0);
                } else {
                    temp.put(listKeys[i], o[i]);
                }
            }
            result.add(temp);
        });

        return result;
    }

    /**
     * 컬럼별 통계 쿼리 조회
     * @param select
     * @param groupItem
     * @param table
     * @return
     * @throws BackendException
     */
    public List<Object> getRowColumn(String select, String groupItem, String  table, String toDate, String fromDate) throws BackendException {
        String[] selectList = select.split("#");
        String[] selectStandardList = groupItem.split("#");
        String selectItem = "";
        String where = "";

        List<Object> list = new ArrayList<>();
        try {
            for (int i = 0; i < selectStandardList.length; i++) {
                selectItem = "";
                for (int j = 0; j < selectList.length; j++) {
                    if (groupItem.equals("null")) {
                        selectItem += selectList[j];
                    } else {
                        selectItem += selectStandardList[i] + "(" + selectList[j] + ")";
                    }
                    if (j != selectList.length - 1) {
                        selectItem += ",";
                    }
                }

                if (toDate != null && fromDate != null) {
                    if (where.equals("")) {
                        where += " WHERE ";
                    } else {
                        where += " AND ";
                    }
                    where += "(reg_date BETWEEN to_timestamp('" + fromDate + "', 'YYYY-MM-DD HH24:MI:SS') AND to_timestamp('" + toDate + "', 'YYYY-MM-DD HH24:MI:SS'))";
                }

                String jpql = "SELECT " + selectItem + " FROM " + table + where;
                Query query = entityManager.createQuery(jpql, Object[].class);

                query.getResultList().forEach(d -> list.add(d));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackendException("Query 조회 중 오류발생, " + e.getMessage());
        }
        return list;
    }

    /**
     * 장르별 통계 쿼리 조회
     * @param select
     * @param groupItem
     * @param table
     * @return
     * @throws BackendException
     */
    public List<Object> getRowGenre(String select, String groupItem, String  table,String where) throws BackendException {
        String[] selectList = select.split(",");
        String[] selectStandardList = groupItem.split(",");
        String selectItem = "";

        List<Object> list = new ArrayList<>();
        try {
            for (int i = 0; i < selectStandardList.length; i++) {
                selectItem = "";
                for (int j = 0; j < selectList.length; j++) {

//                    if(selectList[j].equals("DANCE")){
//                        selectItem += "count (CASE WHEN genre='댄스' THEN 1 END) AS DANCE";
//                    }else if(selectList[j].equals("BALLADE")){
//                        selectItem += "count (CASE WHEN genre='발라드' THEN 1 END) AS BALLADE";
//                    }else if(selectList[j].equals("AGITATION")){
//                        selectItem += "count (CASE WHEN genre='동요' THEN 1 END) AS AGITATION";
//                    }

                    if(selectList[j].equals("DANCE")){
                        selectItem += "count (CASE WHEN genre='댄스' AND CONSORTIUM.role='칠로엔' THEN 1 END) AS DANCE, ";
                        selectItem += "count (CASE WHEN genre='댄스' AND CONSORTIUM.role='음실련' THEN 1 END) AS DANCE";
                    }else if(selectList[j].equals("BALLADE")){
                        selectItem += "count (CASE WHEN genre='발라드' AND CONSORTIUM.role='칠로엔' THEN 1 END) AS BALLADE, ";
                        selectItem += "count (CASE WHEN genre='발라드' AND CONSORTIUM.role='음실련' THEN 1 END) AS BALLADE";
                    }else if(selectList[j].equals("AGITATION")){
                        selectItem += "count (CASE WHEN genre='동요' AND CONSORTIUM.role='칠로엔' THEN 1 END) AS AGITATION, ";
                        selectItem += "count (CASE WHEN genre='동요' AND CONSORTIUM.role='음실련' THEN 1 END) AS AGITATION";
                    }



                    if (j != selectList.length - 1) {
                        selectItem += ",";
                    }
                }
                String jpql = "SELECT " + selectItem + " FROM " + table + " AS SONG INNER JOIN TB_ADMIN_IT_CONSORTIUM " +
                        "AS CONSORTIUM ON SONG.composerCd = CONSORTIUM.id" ;
                jpql += where != "undefined" ? where : "";
                Query query = entityManager.createQuery(jpql, Object[].class);
                query.getResultList().forEach(d -> list.add(d));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackendException("Query 조회 중 오류발생, " + e.getMessage());
        }
        return list;
    }


    public List<Object> getSelectGroup(String select, String where, String  table) throws BackendException {
        String selectItem = select.replace('#', ',');
        String groupItem = select.replace('#', ',');
        String tableItem = table;
        String whereItem = "";
        if (where != null && !where.equals("")) {
            whereItem = "WHERE " + where.replaceAll("#",  "=");
        }

        List<Object> list;
        try {
            String jpql = "SELECT " + selectItem + " FROM " + tableItem + " o " + whereItem + " GROUP BY " + groupItem;
            Query query = entityManager.createQuery(jpql, Object[].class);

            list = query.getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackendException("Select Query 조회 중 오류발생, " + e.getMessage());
        }
        return list;
    }

    /**
     * 각 서비스에서 오버라이딩 해야한다.
     * @param multipartFile
     * @param userId
     * @throws IOException
     * @throws BackendException
     */
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
            List < String > fieldNames = null;
            if (csv.hasNext()) fieldNames = new ArrayList < > (csv.next());
            List < Map < String, String >> list = new ArrayList < > ();
            while (csv.hasNext()) {
                List < String > x = csv.next();
                Map < String, String > obj = new LinkedHashMap < > ();
                for (int i = 0; i < fieldNames.size(); i++) {
                    obj.put(fieldNames.get(i), x.get(i));
                }
                list.add(obj);
            }
            log.info(list.toString());
        }
    }

    /**
     * 각 서비스에서 오버라이딩
     * @return
     */
    public Object getListFileCheck() {
        return null;
    }

    public List<List<Object>> getAgeRangeCdSelect() throws BackendException {
//        Specification<Object> spec = null;
//        spec = AdminSpecs.typeCd("ARGRANGE");
//
//        List<Code> codeList = this.codeRepository.findAll(spec);
//        String[] select = new String[codeList.size()];
//
//        int index = 0;
//        for (Code code : codeList) {
//            select[index] = code.getCd();
//            index++;
//        }

        List<Object> list = this.repository.findAll();
        final AtomicInteger num = new AtomicInteger(1);

        list = list.stream().map(ExceptionHandler.wrap(entity -> this.toDto(entity, num.getAndIncrement())))
                .collect(Collectors.toList());

        List<List<Object>> resultList = new ArrayList<>();
        List<Object> list01 = new ArrayList<>();
        List<Object> list02 = new ArrayList<>();
        List<Object> list03 = new ArrayList<>();
        List<Object> list04 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            VocalDto dto = (VocalDto) list.get(i);

            switch (dto.getAgeRangeCd()) {
                case "01":
                    list01.add(dto);
                    break;
                case "02":
                    list02.add(dto);
                    break;
                case "03":
                    list03.add(dto);
                    break;
                case "04":
                    list04.add(dto);
                    break;
            }
        }

        resultList.add(list01);
        resultList.add(list02);
        resultList.add(list03);
        resultList.add(list04);

        return resultList;
    }

}
