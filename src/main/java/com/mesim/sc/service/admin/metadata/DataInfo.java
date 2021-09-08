package com.mesim.sc.service.admin.metadata;

import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.service.admin.arrange.ArrangeDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DataInfo  {



    //데이터셋정보
    private String song_code;
    private String wav_filename;
    private String mid_filename;
    private String csv_filename;
    private String json_filename;



    public DataInfo() {}

    public DataInfo(Vocal vocal, List<String> fileList) {
        this.song_code=vocal.getSongCd();
        if(fileList != null){
            fileList.forEach(fileName -> {
                if(fileName.contains("adata.mid")){
                    this.mid_filename = fileName;
                }else if(fileName.contains("adata.wav")){
                    this.wav_filename = fileName;
                }else if(fileName.contains("adata.json")){
                    this.json_filename = fileName;
                }else if(fileName.contains("adata.csv")){
                    this.csv_filename = fileName;
                }
            });
        }
    }

}
