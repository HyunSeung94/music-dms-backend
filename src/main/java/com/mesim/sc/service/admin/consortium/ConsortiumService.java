package com.mesim.sc.service.admin.consortium;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import com.mesim.sc.service.admin.AdminService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Qualifier("consortiumService")
public class ConsortiumService extends AdminService {

    @Autowired
    @Qualifier("consortiumRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.selectField = "ageRange";
        this.selectSortField = "consortiumNm";

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

}
