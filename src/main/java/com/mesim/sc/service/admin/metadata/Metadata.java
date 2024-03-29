package com.mesim.sc.service.admin.metadata;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Metadata {



    private  DataInfo datainfo;
    // 창작곡
    private SongInfo songinfo;
    // 가창곡 녹음 정보
    private VocalInfo recordinginfo;
    // 가공정보
    private ArrangeInfo labellinginfo;


    public Metadata() {}

    public Metadata(CreativeSong song, Vocal vocal, Arrange arrange, List<String> fileList,List<List<Object>> codeList) {
        this.datainfo = new DataInfo(vocal,fileList);
        this.songinfo = new SongInfo(song,codeList);
        this.recordinginfo = new VocalInfo(vocal,codeList);
        this.labellinginfo = new ArrangeInfo(arrange);
    }



}
