package com.mesim.sc.repository.rdb.admin.vocal;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.code.Code;
import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import com.mesim.sc.repository.rdb.admin.group.Group;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.sql.Date;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_VOCAL")
@ToString
public class Vocal extends CrudEntity {

    @Id
    @Column(name = "CONTENTS_CD")
    private String id;

    @Column(name = "SONG_CD")
    private String songCd;

    @Column(name = "SINGER_CD")
    private String singerCd;

    @Column(name = "RECORD_LENGTH")
    private String recordLength;

    @Column(name = "RECORD_DATE")
    private Date recordDate;

    @Column(name = "VIBE")
    private String vibe;

    @Column(name = "STUDIO_CD")
    private String studioCd;

    @Column(name = "MIC_NM")
    private String micNm;

    @Column(name = "AUDIO_IF_NM")
    private String audioIfNm;

    @Column(name = "IMPORT_YN")
    private String importYn;

    @Column(name = "STATUS")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "SONG_CD", referencedColumnName = "SONG_CD", insertable = false, updatable = false)
    private CreativeSong song;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "SINGER_CD", referencedColumnName = "CONSORTIUM_ID", insertable = false, updatable = false)
    private Consortium singer;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "STUDIO_CD", referencedColumnName = "CD", insertable = false, updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'STUDIO'", referencedColumnName = "TYPE_CD"))
    })
    private Code studio;

    @Builder
    public Vocal(String id, String songCd, String singerCd, String recordLength, Date recordDate, String vibe, String studioCd, String micNm, String audioIfNm, String importYn,String status, String regId, String modId) {
        super(regId, modId);

        this.id = id;
        this.songCd = songCd;
        this.singerCd = singerCd;
        this.recordLength = recordLength;
        this.recordDate = recordDate;
        this.vibe = vibe;
        this.studioCd = studioCd;
        this.micNm = micNm;
        this.audioIfNm = audioIfNm;
        this.importYn = importYn;
        this.status = status;
    }

}
