package com.mesim.sc.repository.rdb.board.notice;

import com.mesim.sc.repository.rdb.board.BoardRepository;
import com.mesim.sc.repository.rdb.board.post.PostComment;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeCommentRepository extends BoardRepository<NoticeComment, Integer> {

}
