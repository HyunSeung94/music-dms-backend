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
public class VocalInfo{




    // 가창곡 녹음 정보
    private Vocal recordinginfo;
    private String singer_code;
    private String singer_name;
    private String singer_gender;
    private String singer_tone_color;
    private String singer_age_range;
    private String singer_level;
    private String singer_role;
    private String lyric_length;
    private String studio;
    private String mic;
    private String audio_interface;
    private String record_date;
    private String record_checker;


    public VocalInfo() {}

    public VocalInfo(Vocal vocal) {
        this.singer_code = vocal.getSingerCd() ;
        if (vocal.getSinger() != null) {
            this.singer_name = vocal.getSinger().getConsortiumNm();
            this.singer_role = vocal.getSinger().getRole();
            this.singer_level = vocal.getSinger().getLevel();
            this.singer_tone_color = vocal.getSinger().getToneColor();
            this.singer_gender = vocal.getSinger().getGender();
            this.singer_age_range = vocal.getSinger().getAgeRange();
        }
        this.lyric_length = vocal.getRecordLength();
        this.studio = vocal.getStudioCd();
        this.mic = vocal.getMicNm();
        this.audio_interface = vocal.getAudioIfNm();
        this.record_date = DateUtil.toFormat_yyyyMMdd(vocal.getRecordDate().getTime());
        this.record_checker = vocal.getModId();
    }

}
