package com.mesim.sc.api.rest.board.post;

import com.mesim.sc.api.rest.board.BoardRestController;
import com.mesim.sc.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/board/postcomment")
public class PostCommentController extends BoardRestController {

    @Override
    @Autowired
    @Qualifier("postCommentService")
    public void setService(CrudService service) {
        this.name = "게시글 댓글";
        this.service = service;
    }

}
