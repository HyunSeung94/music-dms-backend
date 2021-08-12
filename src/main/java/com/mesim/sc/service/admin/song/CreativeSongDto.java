package com.mesim.sc.service.admin.song;

import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;

import lombok.Data;

import java.sql.Date;
import java.util.List;


@Data

public class CreativeSongDto extends AdminDto {

    private String songCd;
    private String composerCd;
    private String genre;
    private String composerNm;
    private String composerRole;
    private String songNm;
    private String songLength;
    private String tonality;
    private String tempo;
    private String vibe;
    private String instrumentCd;
    private String referenceSong;
    private String referenceArtist;
    private String createDate;
    private List fileList;

    public CreativeSongDto() {}



    public CreativeSongDto(CreativeSong entity) {
        this.songCd = entity.getSongCd();
        this.composerCd = entity.getComposerCd();
        this.composerRole = entity.getConsortium() != null ? entity.getConsortium().getRole() : null;
        this.composerNm = entity.getConsortium() != null ? entity.getConsortium().getConsortiumNm() : null;
        this.genre = entity.getGenre();
        this.songNm = entity.getSongNm();
        this.songLength = entity.getSongLength();
        this.tonality = entity.getTonality();
        this.tempo = entity.getTempo();
        this.vibe = entity.getVibe();
        this.instrumentCd = entity.getInstrumentCd();
        this.referenceSong = entity.getReferenceSong();
        this.referenceArtist = entity.getReferenceArtist();
        this.createDate = DateUtil.toFormat(entity.getCreateDate().getTime());
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public CreativeSong toEntity() {
        return CreativeSong.builder()
                .songCd(this.songCd)
                .composerCd(this.composerCd)
                .genre(this.genre)
                .songNm(this.songNm)
                .songLength(this.songLength)
                .tonality(this.tonality)
                .tempo(this.tempo)
                .vibe(this.vibe)
                .instrumentCd(this.instrumentCd)
                .referenceSong(this.referenceSong)
                .referenceArtist(this.referenceArtist)
                .createDate(this.createDate.length() < 11 ? Date.valueOf(this.createDate) : Date.valueOf(this.createDate.substring(0,10)))
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
