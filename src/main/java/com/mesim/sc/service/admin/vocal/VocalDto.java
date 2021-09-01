package com.mesim.sc.service.admin.vocal;

import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class VocalDto extends AdminDto {

    private String id;
    private String songCd;
    private String songNm;
    private String composerCd;
    private String singerCd;
    private String singerNm;
    private String singerRole;
    private String level;
    private String toneColor;
    private String gender;
    private String ageRange;
    private String recordLength;
    private String recordDate;
    private String recordInspection;
    private String vibe;
    private String studioCd;
    private String studio;
    private String micNm;
    private String audioIfNm;
    private String regNm;
    private Integer regGroupId;
    private String regGroupNm;
    private String importYn;
    private String status;
    private List fileList;

    public VocalDto() {}

    public VocalDto(Vocal entity) {
        this.id = entity.getId();
        this.songCd = entity.getSongCd();
        if (entity.getSong() != null) {
            this.songNm = entity.getSong().getSongNm();
            this.composerCd = entity.getSong().getComposerCd();
        }
        this.singerCd = entity.getSingerCd();
        if (entity.getSinger() != null) {
            this.singerNm = entity.getSinger().getConsortiumNm();
            this.singerRole = entity.getSinger().getRole();
            this.level = entity.getSinger().getLevel();
            this.toneColor = entity.getSinger().getToneColor();
            this.gender = entity.getSinger().getGender();
            this.ageRange = entity.getSinger().getAgeRange();
        }
        this.recordLength = entity.getRecordLength();
        this.recordDate = DateUtil.toFormat_yyyyMMdd(entity.getRecordDate().getTime());
        this.vibe = entity.getVibe();
        this.studioCd = entity.getStudioCd();
        this.studio = entity.getStudio() != null ? entity.getStudio().getName() : null;
        this.micNm = entity.getMicNm();
        this.audioIfNm = entity.getAudioIfNm();
        this.importYn = entity.getImportYn();
        this.status = entity.getStatus();
        this.regId = entity.getRegId();
        if (entity.getRegUser() != null) {
            this.regNm = entity.getRegUser().getName();
            this.regGroupId = entity.getRegUser().getGroup() != null ? entity.getRegUser().getGroup().getId() : null;
            this.regGroupNm = entity.getRegUser().getGroup() != null ? entity.getRegUser().getGroup().getName() : null;
        }
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public Vocal toEntity() {
        return Vocal.builder()
                .id(this.id)
                .songCd(this.songCd)
                .singerCd(this.singerCd)
                .recordLength(this.recordLength)
                .recordDate(this.recordDate.length() < 11 ? Date.valueOf(this.recordDate) : Date.valueOf(this.recordDate.substring(0,10)))
                .vibe(this.vibe)
                .studioCd(this.studioCd)
                .micNm(this.micNm)
                .audioIfNm(this.audioIfNm)
                .importYn(this.importYn != null ? this.importYn : "N")
                .status(this.status)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
