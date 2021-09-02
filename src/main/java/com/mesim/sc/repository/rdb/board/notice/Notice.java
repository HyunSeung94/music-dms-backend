package com.mesim.sc.repository.rdb.board.notice;

import com.mesim.sc.repository.rdb.CrudEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity(name = "TB_BOARD_IT_NOTICE")
public class Notice extends CrudEntity {

    @Id
    @Column(name = "NOTICE_ID")
    @SequenceGenerator(name = "COL_GEN_NOTICE_ID_SEQ", sequenceName = "NOTICE_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_NOTICE_ID_SEQ")
    private int id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CONTENTS")
    private String contents;

    @Builder
    public Notice(int id, String title, String subject, String contents, String regId, String modId) {
        super(regId, modId);

        this.id = id;
        this.title = title;
        this.subject = subject;
        this.contents = contents;
    }

}
