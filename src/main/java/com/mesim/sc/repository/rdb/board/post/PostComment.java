package com.mesim.sc.repository.rdb.board.post;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.code.Code;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity(name = "TB_BOARD_IT_POSTCOMMENT")
public class PostComment extends CrudEntity {

    @Id
    @Column(name = "SEQ")
    @SequenceGenerator(name = "COL_GEN_POST_COMMENT_ID_SEQ", sequenceName = "POST_COMMENT_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_POST_COMMENT_ID_SEQ")
    private int id;

    @Column(name = "COMMENT_ID")
    private Integer commentId;

    @Column(name = "POST_ID")
    private Integer postId;

    @Column(name = "COMMENT")
    private String comment;

    @Builder
    public PostComment(int id, Integer commentId, Integer postId, String comment, String regId, String modId) {
        super(regId, modId);

        this.id = id;
        this.commentId = commentId;
        this.postId = postId;
        this.comment = comment;
    }

}
