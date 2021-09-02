package com.mesim.sc.service.board.notice;

import com.mesim.sc.repository.rdb.board.notice.NoticeComment;
import com.mesim.sc.repository.rdb.board.post.PostComment;
import com.mesim.sc.service.board.BoardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoticeCommentDto extends BoardDto {

    private int id;
    private Integer commentId;
    private Integer noticeId;
    private String comment;

    public NoticeCommentDto() {}

    public NoticeCommentDto(NoticeComment entity) {
        super(entity);

        this.id = entity.getId();
        this.commentId = entity.getCommentId();
        this.noticeId = entity.getNoticeId();
        this.comment = entity.getComment();
    }

    @Override
    public NoticeComment toEntity() {
        return NoticeComment.builder()
                .id(this.id)
                .commentId(this.commentId)
                .noticeId(this.noticeId)
                .comment(this.comment)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
