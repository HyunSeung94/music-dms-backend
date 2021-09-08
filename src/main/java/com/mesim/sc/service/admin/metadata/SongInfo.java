package com.mesim.sc.service.admin.metadata;

import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SongInfo{


    // 창작곡
    private CreativeSong songinfo;
    private String song_create_code;
    private String song_title;
    private String composer_code;
    private String composer_name;
    private String create_date;
    private String tempo;
    private String tonality;
    private String genre;
    private String songLength;
    private String vibe;
    private String reference_artist;
    private String reference_song;
    private String melody_instrument;
    private String drum_instrument;
    private String bass_instrument;
    private String song_create_checker;

    public SongInfo() {}

    public SongInfo(CreativeSong song) {

        this.song_create_code = song.getId();
        this.song_title = song.getSongNm();
        this.composer_code = song.getComposerCd();
        this.composer_name = song.getComposer() != null ? song.getComposer().getConsortiumNm() : null;
        this.create_date = DateUtil.toFormat_yyyyMMdd(song.getCreateDate().getTime());
        this.tempo = song.getTempo();
        this.tonality = song.getTonality();
        this.genre = song.getGenre();
        this.songLength = song.getSongLength();
        this.vibe = song.getVibe();
        this.reference_artist = song.getReferenceArtist();
        this.reference_song = song.getReferenceSong();
        if(song.getInstrumentCd() != null){
            String[] cds= song.getInstrumentCd().split(",");
            for(String cd : cds){
                this.melody_instrument = cd.equals("MI") ? song.getInstrumentCd() : null;
                this.drum_instrument = cd.equals("P") ? song.getInstrumentCd() : null;
                this.bass_instrument = cd.equals("D") ? song.getInstrumentCd() : null;
            }
        }
        this.song_create_checker = song.getModId();
    }


}
