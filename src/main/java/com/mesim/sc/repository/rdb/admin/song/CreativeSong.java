package com.mesim.sc.repository.rdb.admin.song;

import com.mesim.sc.repository.rdb.CrudEntity;

import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import com.mesim.sc.repository.rdb.admin.group.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Date;


@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_CREATIVESONG")
@ToString
public class CreativeSong extends CrudEntity {

    @Id
    @Column(name = "MUSIC_CD")
    private String id;


    @Column(name = "COMPOSER_CD")
    private String composerCd;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="COMPOSER_CD", referencedColumnName="CONSORTIUM_ID", insertable = false, updatable = false)
    private Consortium consortium;

    @Column(name = "GENRE")
    private String genre;

    @Column(name = "SONG_NM")
    private String songNm;

    @Column(name = "SONG_LENGTH")
    private String songLength;

    @Column(name = "TONALITY")
    private String tonality;

    @Column(name = "TEMPO")
    private String tempo;

    @Column(name = "VIBE")
    private String vibe;

    @Column(name = "INSTRUMENT_CD")
    private String instrumentCd;

    @Column(name = "CREATE_DATE")
    private Date createDate;

    @Column(name = "REFERENCE_SONG")
    private String referenceSong;

    @Column(name = "REFERENCE_ARTIST")
    private String referenceArtist;


    @Builder
    public CreativeSong(String id,Consortium consortium, String composerCd, String genre, String songNm, String songLength,String tonality, String tempo, String vibe,  String instrumentCd, String referenceSong, String referenceArtist, Date createDate,String regId,String modId) {
        super(regId, modId);
        this.id = id;
        this.consortium=consortium;
        this.composerCd = composerCd;
        this.genre = genre;
        this.songNm = songNm;
        this.songLength = songLength;
        this.tonality = tonality;
        this.tempo = tempo;
        this.vibe = vibe;
        this.instrumentCd = instrumentCd;
        this.createDate = createDate;
        this.referenceSong = referenceSong;
        this.referenceArtist = referenceArtist;
    }


}
