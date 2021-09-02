package com.mesim.sc.service.board;

import com.mesim.sc.exception.BackendException;
import com.mesim.sc.repository.rdb.board.notice.NoticeCommentRepository;
import com.mesim.sc.repository.rdb.board.notice.NoticeRepository;
import com.mesim.sc.repository.rdb.board.post.PostCommentRepository;
import com.mesim.sc.repository.rdb.board.post.PostRepository;
import com.mesim.sc.service.CrudService;
import com.mesim.sc.service.board.notice.NoticeCommentDto;
import com.mesim.sc.service.board.notice.NoticeDto;
import com.mesim.sc.service.board.post.PostCommentDto;
import com.mesim.sc.service.board.post.PostDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BoardService extends CrudService {

    /**
     * 해당 Repository 에 맞는 Dto 클래스를 반환
     *
     * @return Dto Class
     */
    public Class getClazz() throws BackendException {

        Class clazz = null;

        if (this.repository instanceof NoticeRepository) {
            clazz = NoticeDto.class;
        } else if (this.repository instanceof NoticeCommentRepository) {
            clazz = NoticeCommentDto.class;
        } else if (this.repository instanceof PostRepository) {
            clazz = PostDto.class;
        } else if (this.repository instanceof PostCommentRepository) {
            clazz = PostCommentDto.class;
        }

        if (clazz == null) {
            throw new BackendException("Dto Class 를 찾을 수 없음");
        }

        return clazz;
    }

}
