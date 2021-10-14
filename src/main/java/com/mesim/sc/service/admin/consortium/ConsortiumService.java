package com.mesim.sc.service.admin.consortium;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.repository.rdb.admin.arrange.ArrangeRepository;
import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import com.mesim.sc.repository.rdb.admin.song.CreativeSongRepository;
import com.mesim.sc.service.admin.AdminService;

import com.mesim.sc.service.admin.arrange.CompletedArrangeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Qualifier("consortiumService")
public class ConsortiumService extends AdminService {


    @Autowired
    private ArrangeRepository arrangeRepository;

    @Autowired
    @Qualifier("consortiumRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.selectField = "ageRange";
        this.selectSortField = "consortiumNm";
        this.searchFields = new String[]{"id", "consortiumNm", "toneColor", "ageRange", "toneColor","gender" +
                ""};
        this.joinedSortField = new String[]{"codeArgRange"};
        super.init();
    }


    /**
     * 장르별 통계 쿼리 조회
     * @param select
     * @param groupItem
     * @param table
     * @return
     * @throws BackendException
     */
    public List<Object> getRowGenreSum(String select, String groupItem, String table) throws BackendException {
        String[] selectList = select.split(",");
        String[] selectStandardList = groupItem.split(",");
        String selectItem = "";

        List<Object> list = new ArrayList<>();
        try {
            for (int i = 0; i < selectStandardList.length; i++) {
                selectItem = "";
                for (int j = 0; j < selectList.length; j++) {

                    selectItem += "sum("+selectList[j].toLowerCase()+")";

                    if (j != selectList.length - 1) {
                        selectItem += ", ";
                    }
                }

                String where = " where group_id= "+"'"+selectStandardList[i]+"'";
                String jpql = "SELECT " + selectItem + " FROM " + table + where;
                Query query = entityManager.createQuery(jpql);
                ConsortiumDto dto = new ConsortiumDto();

                query.getResultList().forEach(data ->{list.add(data);}
                );

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BackendException("Query 조회 중 오류발생, " + e.getMessage());
        }
        return list;
    }
    public PageWrapper getSingerListPage(String[] select, int index, int size, String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {


//        Specification<Object> spec = null;

        Specification<Object> spec = (root, query, cb) -> {
            Stream<String> result = Arrays.stream(sortProperties).filter(s -> Arrays.stream(this.joinedSortField).anyMatch(s::startsWith));
            if (result.count() == 0) {
                query.distinct(true);
            }
            return null;
        };

        String[] sortTemp = sortProperties[0].split(";");
        if(sortTemp[0].contains("genre")){
            sortProperties[0] = "ballade;"+sortTemp[1];
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

        List<CompletedSingerDto> dtoList = new ArrayList<>();

        list.forEach(obj -> {
            CompletedSingerDto dto = new CompletedSingerDto((ConsortiumDto) obj);
            dtoList.add(dto);
        });

        List<Object> resultList = (List<Object>) getCompletedSinger(dtoList);
        result.setList(resultList);

        return result;
    }

    public Object getCompletedSinger(List<CompletedSingerDto> dtoList) throws BackendException {


        List<Arrange> list =  this.arrangeRepository.findAllByStatus("2");

        List<CompletedArrangeDto> dtoList2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CompletedArrangeDto dto = new CompletedArrangeDto(list.get(i));
            dtoList2.add(i,dto);
        }



        for(int i=0; i<dtoList.size(); i++){
            String id = dtoList.get(i).getId();
            int index = 0;
            for (int j=0; j<dtoList2.size(); j++){
                if(id.equals(dtoList2.get(j).getId())){
                    index++;
                };
            }
            dtoList.get(i).setCompleted(index);
        }

//        Collections.sort(dtoList);
        return dtoList;
    }

}
