package com.mesim.sc.service.admin.song;

import com.mesim.sc.repository.rdb.admin.group.User;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.sql.Timestamp;


@Data
public class CreativeSongDto extends AdminDto {

    private String id;
    private String composer_cd;
    private String genre;
    private String composer_nm;
    private String composer_role;
    private String song_nm;
    private String song_length;
    private String tonality;
    private String tempo;
    private String vibe;
    private String instrument_cd;
    private String reference_song;
    private String reference_artist;
    private String create_date;


    public CreativeSongDto() {}



    public CreativeSongDto(CreativeSong entity) {
        this.id = entity.getId();
        this.composer_cd = entity.getComposer_cd();
        this.composer_role = entity.getUser() != null ? entity.getUser().getRole() : null;
        this.composer_nm = entity.getUser() != null ? entity.getUser().getName() : null;
        this.genre = entity.getGenre();
        this.song_nm = entity.getSong_nm();
        this.song_length = entity.getSong_length();
        this.tonality = entity.getTonality();
        this.tempo = entity.getTempo();
        this.vibe = entity.getVibe();
        this.instrument_cd = entity.getInstrument_cd();
        this.reference_song = entity.getReference_song();
        this.reference_artist = entity.getReference_artist();
        this.create_date = DateUtil.toFormat(entity.getCreate_date().getTime());
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public CreativeSong toEntity() {
        return CreativeSong.builder()
                .id(this.id)
                .composer_cd(this.composer_cd)
                .genre(this.genre)
                .song_nm(this.song_nm)
                .song_length(this.song_length)
                .tonality(this.tonality)
                .tempo(this.tempo)
                .vibe(this.vibe)
                .instrument_cd(this.instrument_cd)
                .reference_song(this.reference_song)
                .reference_artist(this.reference_artist)
                .create_date(this.create_date.length() < 11 ? Date.valueOf(this.create_date) : Date.valueOf(this.create_date.substring(0,10)))
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
