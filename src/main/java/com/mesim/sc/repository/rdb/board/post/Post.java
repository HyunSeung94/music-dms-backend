package com.mesim.sc.repository.rdb.board.post;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.code.Code;
import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.*;
import java.sql.Date;

@NoArgsConstructor
@Getter
@Entity(name = "TB_BOARD_IT_POST")
public class Post extends CrudEntity {

    @Id
    @Column(name = "POST_ID")
    @SequenceGenerator(name = "COL_GEN_POST_ID_SEQ", sequenceName = "POST_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "COL_GEN_POST_ID_SEQ")
    private int id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CONTENTS")
    private String contents;

    @Column(name = "TYPE_CD")
    private String typeCd;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "TYPE_CD", referencedColumnName = "CD", insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'POSTTYPE'", referencedColumnName = "TYPE_CD"))
    })
    private Code type;

    @Builder
    public Post(int id, String title, String subject, String contents, String typeCd, String regId, String modId) {
        super(regId, modId);

        this.id = id;
        this.title = title;
        this.subject = subject;
        this.contents = contents;
        this.typeCd = typeCd;
    }

}
