package com.mesim.sc.service.board.post;

import com.mesim.sc.repository.rdb.board.post.PostComment;
import com.mesim.sc.service.board.BoardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentDto extends BoardDto {

    private int id;
    private Integer commentId;
    private Integer postId;
    private String comment;

    public PostCommentDto() {}

    public PostCommentDto(PostComment entity) {
        super(entity);

        this.id = entity.getId();
        this.commentId = entity.getCommentId();
        this.postId = entity.getPostId();
        this.comment = entity.getComment();
    }

    @Override
    public PostComment toEntity() {
        return PostComment.builder()
                .id(this.id)
                .commentId(this.commentId)
                .postId(this.postId)
                .comment(this.comment)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
