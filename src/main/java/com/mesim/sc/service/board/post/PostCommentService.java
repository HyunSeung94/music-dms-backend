package com.mesim.sc.service.board.post;

import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.service.board.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@Qualifier("postCommentService")
public class PostCommentService extends BoardService {

    @Autowired
    @Qualifier("postCommentRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"title", "regUserName"};

        this.addRefEntity("regUser", "name");

        super.init();
    }

}
