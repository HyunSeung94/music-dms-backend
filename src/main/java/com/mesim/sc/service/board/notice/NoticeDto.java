package com.mesim.sc.service.board.notice;

import com.mesim.sc.repository.rdb.board.notice.Notice;
import com.mesim.sc.repository.rdb.board.notice.NoticeComment;
import com.mesim.sc.service.board.BoardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoticeDto extends BoardDto {

    private int id;
    private String title;
    private String subject;
    private String contents;

    public NoticeDto() {}

    public NoticeDto(Notice entity) {
        super(entity);

        this.id = entity.getId();
        this.title = entity.getTitle();
        this.subject = entity.getSubject();
        this.contents = entity.getContents();
    }

    @Override
    public Notice toEntity() {
        return Notice.builder()
                .id(this.id)
                .title(this.title)
                .subject(this.subject)
                .contents(this.contents)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
