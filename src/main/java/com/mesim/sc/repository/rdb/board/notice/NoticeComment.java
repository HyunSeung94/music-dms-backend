package com.mesim.sc.repository.rdb.board.notice;

import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity(name = "TB_BOARD_IT_NOTICECOMMENT")
public class NoticeComment extends CrudEntity {

    @Id
    @Column(name = "SEQ")
    @SequenceGenerator(name = "COL_GEN_NOTICE_COMMENT_ID_SEQ", sequenceName = "NOTICE_COMMENT_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_NOTICE_COMMENT_ID_SEQ")
    private int id;

    @Column(name = "COMMENT_ID")
    private Integer commentId;

    @Column(name = "NOTICE_ID")
    private Integer noticeId;

    @Column(name = "COMMENT")
    private String comment;

    @Builder
    public NoticeComment(int id, Integer commentId, Integer noticeId, String comment, String regId, String modId) {
        super(regId, modId);

        this.id = id;
        this.commentId = commentId;
        this.noticeId = noticeId;
        this.comment = comment;
    }

}
