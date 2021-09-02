package com.mesim.sc.service.board.post;

import com.mesim.sc.repository.rdb.board.notice.Notice;
import com.mesim.sc.repository.rdb.board.post.Post;
import com.mesim.sc.service.board.BoardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostDto extends BoardDto {

    private int id;
    private String title;
    private String subject;
    private String contents;
    private String typeCd;
    private String typeNm;

    public PostDto() {}

    public PostDto(Post entity) {
        super(entity);

        this.id = entity.getId();
        this.title = entity.getTitle();
        this.subject = entity.getSubject();
        this.contents = entity.getContents();
        this.typeCd = entity.getTypeCd();
        this.typeNm = entity.getType() != null ? entity.getType().getName() : null;
    }

    @Override
    public Post toEntity() {
        return Post.builder()
                .id(this.id)
                .title(this.title)
                .subject(this.subject)
                .contents(this.contents)
                .typeCd(this.typeCd)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
