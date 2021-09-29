package com.mesim.sc.service.admin.metadata;

import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.repository.rdb.admin.code.Code;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.service.admin.code.CodeDto;
import com.mesim.sc.util.CSV;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SongInfo {


    // 창작곡
    private CreativeSong songinfo;
    private Code code;
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

    public SongInfo() {
    }

    public SongInfo(CreativeSong song, List<List<Object>> codeList) {

        this.song_create_code = song.getId();
        this.song_title = song.getSongNm();
        this.composer_code = song.getComposerCd();
        this.composer_name = song.getComposer() != null ? song.getComposer().getInitial() : null;
        this.create_date = DateUtil.toFormat_yyyyMMdd(song.getCreateDate().getTime());
        this.tempo = song.getTempo();
        this.tonality = song.getTonality();
        this.genre = song.getGenre();
        this.songLength = song.getSongLength();
        this.vibe = getVibe(song.getVibe(), codeList);
        this.reference_artist = song.getReferenceArtist();
        this.reference_song = song.getReferenceSong();
        if (song.getInstrumentCd() != null) {
            String[] cds = song.getInstrumentCd().split(",");
            this.melody_instrument = "false";
            this.drum_instrument = "false";
            this.bass_instrument = "false";
            for (String cd : cds) {
                if (cd.equals("MI")) {
                    this.melody_instrument = "true";
                } else if (cd.equals("P")) {
                    this.drum_instrument = "true";
                } else if (cd.equals("D")) {
                    this.bass_instrument = "true";
                }
            }
        }
        this.song_create_checker = song.getModUser().getInitial();
    }

    public String getVibe(String cd, List<List<Object>> codeList) {
        String[] cds = cd.split(",");
        String result = "";
        for (int i=0; i<cds.length; i++) {
            String id = cds[i];
            for (int j = 0; j < codeList.get(0).size(); j++) {
                CodeDto codeDto = (CodeDto) codeList.get(0).get(j);
                if (id.equals(codeDto.getCd())) {
                    result += codeDto.getName();
                    if (i != cds.length - 1) {
                        result += ",";
                    }
                }
            }
        }
        return result;
    }



}
