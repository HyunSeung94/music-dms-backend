package com.mesim.sc.repository.rdb.board.notice;

import com.mesim.sc.repository.rdb.board.BoardRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends BoardRepository<Notice, Integer> {

}
