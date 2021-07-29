package com.mesim.sc.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.util.CsvGeneratorUtil;
import com.mesim.sc.util.DataTypeUtil;
import com.mesim.sc.util.DateUtil;
import com.mesim.sc.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
    private String csvPath;

    @Value("${csv.temp.file}")
    private String csvFileName;

    protected static final String JOIN_FIELD_DELIMITER = ".";
    protected static final String SORT_FIELD_SPLITTER = ";";
    protected static final String SORT_DESC = "desc";
    protected static final String AND = "AND";
    protected static final String OR = "OR";

    private final List<Object[]> refEntityList = new ArrayList<>();

    // 정렬 기본 필드
    protected String defaultSortField = "regDate";

    // 정렬 조인 필드
    protected String[] joinedSortField;

    // 검색 필드
    protected Set<String> searchFieldSet;
    protected String[] searchFields = new String[]{};

    // 날짜 검색 필드
    protected String dateField = "regDate";

    // 조회 컬럼 제외 목록
    protected String[] excludeColumn = new String[]{};

    protected ObjectMapper mapper = new ObjectMapper();

    protected CrudRepository repository;

    private Class<?> pkType;

    @PostConstruct
    public void init() {
        if (searchFields != null) {
            this.searchFieldSet = new HashSet<>(Arrays.asList(searchFields));
        }

        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Type cls = DataTypeUtil.getGenericType(this.repository.getClass())[0];
        Type[] jpaCls = DataTypeUtil.getGenericType(DataTypeUtil.getClass(cls));
        this.pkType = DataTypeUtil.getClass(((ParameterizedType) jpaCls[0]).getActualTypeArguments()[1]);
    }

    /**
     * 각 서비스의 엔티티에 대한 레포지토리 주입
     */
    abstract public void setRepository(CrudRepository repository);

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
     * @param index 조회 시작 위치
     * @param size 조회 목록 개수
     * @param sortProperties 정렬할 필드명 리스트
     * @param keywords 검색 키워드
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND
     * @param fromDate 검색 시작 날짜
     * @param toDate 검색 마지막 날짜
     * @return 페이지 목록
     */
    public PageWrapper getListPage(int index, int size, String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        Specification<Object> spec = null;
        PageRequest pageRequest = this.getPageRequest(index, size, sortProperties);

        if (fromDate != null && toDate != null) {
            spec = this.getDateSpec(fromDate, toDate);
        }

        if (keywords != null && keywords.length > 0) {
            spec = spec == null ?
                    Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)) :
                    Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        Page<Object> page = spec == null ? this.repository.findAll(pageRequest) : this.repository.findAll(spec, pageRequest);
        PageWrapper result = new PageWrapper(page);

        final AtomicInteger i = new AtomicInteger(1);

        result.setList(page.get()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement() + (result.getNumber() * size))))
                .collect(Collectors.toList())
        );

        return result;
    }

    /**
     * 검색 키워드에 일치하는 전체 목록 조회
     *
     * @param sortProperties 정렬할 필드명 리스트
     * @param keywords 검색 키워드
     * @param searchOp 키워드 간 검색 조건, OR 또는 AND
     * @param fromDate 검색 시작 날짜
     * @param toDate 검색 마지막 날짜
     * @return 전체 목록
     */
    public List<Object> getList(String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        Specification<Object> spec = null;
        Sort sort = this.getSort(sortProperties);

        if (fromDate != null & toDate != null) {
            spec = this.getDateSpec(fromDate, toDate);
        }

        if (keywords != null && keywords.length > 0) {
            spec = spec == null ?
                    Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)) :
                    Specification.where(getSearchSpec(keywords, this.searchFieldSet, searchOp)).and(spec);
        }

        List<Object> list = spec == null ? this.repository.findAll(sort) : this.repository.findAll(spec, sort);

        final AtomicInteger i = new AtomicInteger(1);

        return list.stream()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, i.getAndIncrement())))
                .collect(Collectors.toList());
    }

    /**
     * 전체 데이터 목록 조회
     *
     * @return 전체 데이터 목록
     */
    public List<Object> getList() {
        return this.repository.findAll();
    }

    /**
     * 전체 목록 개수 반환
     *
     * @return 전체 목록 개수
     */
    public long getCount() {
        return this.repository.count();
    }

    /**
     * 조회 컬럼 목록 조회
     *
     * @return 조회 컬럼 목록
     */
    public Object getListColumn() throws BackendException {
        ArrayList<String> fields = (ArrayList<String>) this.toColumn();

        for (String col : this.excludeColumn) {
            fields.remove(col);
        }

        return fields;
    }

    /**
     * 데이터 상세조회
     *
     * @param id ID
     * @return 데이터 Dto
     */
    public Object get(String id) {
        if (this.pkType != null) {
            return this.pkType.getName().equals("java.lang.Integer") ? this.getByIntegerId(id) : this.getByStringId(id);
        }
        return this.getByStringId(id);
    }

    /**
     * 데이터 상세조회 (Integer ID)
     *
     * @param id ID
     * @return 데이터 Dto
     */
    public Object getByIntegerId(String id) {
        try {
            int _id = Integer.parseInt(id);
            Optional<Object> optEntity = this.repository.findById(_id);
            return optEntity.map(o -> {
                try {
                    return this.toDto(o, 0);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).orElse(null);
        } catch (NumberFormatException e) {
            // 파라미터 값(ID)이 Integer 가 아닌경우 String 으로 처리
            return this.getByStringId(id);
        }
    }

    /**
     * 데이터 상세조회 (String ID)
     * 복합키(2개 이상의 기본키)인 경우는 별도 작성
     *
     * @param id ID
     * @return 데이터 Dto
     */
    private Object getByStringId(String id) {
        Optional<Object> optEntity = this.repository.findById(id);
        return optEntity.map(o -> {
            try {
                return this.toDto(o, 0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
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

    /**
     * 삭제 (PK)
     *
     * @param id 삭제할 데이터 PK
     * @return 성공/실패 여부
     */
    public boolean delete(String id) throws BackendException {
        if (this.pkType != null) {
            return this.pkType.getName().equals("java.lang.Integer") ? this.deleteByIntegerId(id) : this.deleteByStringId(id);
        }
        return this.deleteByStringId(id);
    }

    /**
     * 삭제 (Integer ID)
     *
     * @param id 삭제할 데이터 PK
     * @return 성공/실패 여부
     */
    public boolean deleteByIntegerId(String id) {
        try {
            int _id = Integer.parseInt(id);
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
     * @param id 삭제할 데이터 PK
     * @return 성공/실패 여부
     */
    public boolean deleteByStringId(String id) {
        this.repository.deleteById(id);
        return true;
    }

    /**
     * 삭제 (Entity)
     *
     * @param o 삭제할 데이터 Entity
     * @return 성공/실패 여부
     */
    public boolean delete(Object o) throws BackendException {
        Map<String, Object> map = (Map<String, Object>) o;
        List<Object> deleteObjects =((List<Object>) map.get("ids"))
                .stream()
                .map(ExceptionHandler.wrap(object -> this.toEntity(object)))
                .collect(Collectors.toList());
        this.repository.deleteAll(deleteObjects);
        return true;
    }

    /**
     * 페이지 목록 조회를 위한 PageRequest 생성
     *
     * @param index 시작위치
     * @param size  조회할 목록 크기
     * @param sortProperties 정렬할 필드명 리스트
     * @return PageRequest
     */
    public PageRequest getPageRequest(int index, int size, String[] sortProperties) {
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
                            String joinField = optJoin.get();
                            prop = String.join(JOIN_FIELD_DELIMITER, joinField, prop.replace(joinField, "").toLowerCase());
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
     * 날짜 검색에 필요한 Specification 생성
     *
     * @param fromDate 검색 시작 날짜
     * @param toDate 검색 종료 날짜
     * @return 날짜 검색 Specification
     */
    protected Specification<Object> getDateSpec(String fromDate, String toDate) {
        return (Specification<Object>) (root, query, cb) -> cb.between(root.get(dateField), DateUtil.getInitFromDate(fromDate), DateUtil.getInitToDate(toDate));
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

            for (String keyword : keywordSet) {
                likeKeywords.add(keyword.contains("%") ? keyword : "%" + keyword + "%");
            }
            return this.getSpec(likeKeywords, fields, searchOp);
        } else {
            throw new BackendException("검색 조건 오류 (searchOP=" + searchOp + ")");
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
    protected Specification<Object> getSpec(Collection<String> keywords, Set<String> fields, String searchOp) {
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
                for (String keyword : keywords) {
                    root.getModel().getDeclaredSingularAttributes().stream()
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
                }

                Predicate[] array = finalPredicates.toArray(new Predicate[finalPredicates.size()]);

                return searchOp.equals(OR) ? builder.or(array) : builder.and(array);
            }
        };
    }


    /**
     * 해당 Repository 에 맞는 Dto 클래스를 반환
     *
     * @return Dto Class
     */
    abstract protected Class getClazz() throws BackendException;


    /**
     * Dto 클래스의 선언된 변수 목록을 가져오는 메소드
     * 각 패키지의 abstract class 에서 작성 (ex: AdminService.class 참조)
     *
     * @return 변수 목록
     */
    protected Object toColumn() throws BackendException {
        try {
            Class clazz = this.getClazz();
            ArrayList<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

            while (clazz.getSuperclass() != null) {
                clazz = clazz.getSuperclass();
                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            }

            return fields.stream()
                    .map(Field::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BackendException("변수 목록 조회 중 오류발생, " + e.getMessage());
        }
    }

    /**
     * Entity 클래스를 Dto 클래스로 생성하는 메소드
     * 각 패키지의 abstract class 에서 작성 (ex: AdminService.class 참조)
     *
     * @param o Entity
     * @param seq 번호
     * @return Dto
     */
    protected Object toDto(Object o, int seq) throws BackendException {
        Class clazz = null;
        Object object = null;
        try {
            clazz = getClazz();
            object = clazz.getDeclaredConstructor(o.getClass()).newInstance(o);

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
            log.error(e.getMessage(), e);
            throw new BackendException("Dto 변환 중 오류발생, Class : " + clazz  + ", Object : " + object + e.getMessage());
        }
    }

    /**
     * Dto 클래스를 Entity 클래스 생성하는 메소드
     * 각 패키지의 abstract class 에서 작성 (ex: AdminService.class 참조)
     *
     * @param o Dto
     * @return Entity
     */
    protected Object toEntity(Object o) throws BackendException {
        try {
            Class clazz = null;
            if (o instanceof Map) {
                clazz = getClazz();
                o = mapper.convertValue(o, clazz);
            }
            return this.toEntity(o, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackendException("Entity 변환 중 오류발생, " + e.getMessage());
        }
    }

    protected Object toEntity(Object o, Class clazz) throws BackendException {
        try {
            Class cls = o.getClass();
            Method method = o.getClass().getMethod("toEntity");
            return method.invoke(o);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackendException("Entity 변환 중 오류발생, " + e.getMessage());
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

    public void generateCSV(List<Object> mapList) throws BackendException {
        ArrayList<String> columnList = (ArrayList<String>) toColumn();

//        String[] keyList = new String[]{"id", "userId", "userNm", "typeCd", "typeNm", "apiPath", "accessIp", "accessDate", "errorCode", "errorCodeNm", "errorContents", "errorYn"};
        String[] keyList = columnList.stream().toArray(String[]::new);

        CsvGeneratorUtil cgu = new CsvGeneratorUtil(FileUtil.makePath(fileBasePath, csvPath), csvFileName, keyList);

        mapList.forEach(dto -> {
            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(dto, Map.class);

            try {
                cgu.generateCSV((Map<String, String>) map);
            } catch (BackendException e) {
                throw new RuntimeException(e);
            }
        });

        cgu.close();
    }
}
