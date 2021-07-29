package com.mesim.sc.repository.rdb.admin.song;

import com.mesim.sc.repository.rdb.CrudEntity;

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
    private String composer_cd;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="COMPOSER_CD", referencedColumnName="USER_CD", insertable = false, updatable = false)
    private User user;

    @Column(name = "GENRE")
    private String genre;

    @Column(name = "SONG_NM")
    private String song_nm;

    @Column(name = "SONG_LENGTH")
    private String song_length;

    @Column(name = "TONALITY")
    private String tonality;

    @Column(name = "TEMPO")
    private String tempo;

    @Column(name = "VIBE")
    private String vibe;

    @Column(name = "INSTRUMENT_CD")
    private String instrument_cd;

    @Column(name = "CREATE_DATE")
    private Date create_date;

    @Column(name = "REFERENCE_SONG")
    private String reference_song;

    @Column(name = "REFERENCE_ARTIST")
    private String reference_artist;


    @Builder
    public CreativeSong(String id,User user, String composer_cd, String genre, String song_nm, String song_length,String tonality, String tempo, String vibe,  String instrument_cd, String reference_song, String reference_artist, Date create_date,String regId,String modId) {
        super(regId, modId);
        this.id = id;
        this.user=user;
        this.composer_cd = composer_cd;
        this.genre = genre;
        this.song_nm = song_nm;
        this.song_length = song_length;
        this.tonality = tonality;
        this.tempo = tempo;
        this.vibe = vibe;
        this.instrument_cd = instrument_cd;
        this.create_date = create_date;
        this.reference_song = reference_song;
        this.reference_artist = reference_artist;
    }


}
