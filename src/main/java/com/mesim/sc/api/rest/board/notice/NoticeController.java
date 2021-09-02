package com.mesim.sc.api.rest.board.notice;

import com.mesim.sc.api.rest.board.BoardRestController;
import com.mesim.sc.service.CrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/board/notice")
public class NoticeController extends BoardRestController {

    @Override
    @Autowired
    @Qualifier("noticeService")
    public void setService(CrudService service) {
        this.name = "공지사항";
        this.service = service;
    }

}
