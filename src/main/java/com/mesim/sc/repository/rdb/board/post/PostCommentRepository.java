package com.mesim.sc.repository.rdb.board.post;

import com.mesim.sc.repository.rdb.board.BoardRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository extends BoardRepository<PostComment, Integer> {

}
