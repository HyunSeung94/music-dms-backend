package com.mesim.sc.service.admin.metadata;

import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import com.mesim.sc.repository.rdb.admin.vocal.Vocal;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.CSV;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ArrangeInfo{




    private String labeller_code;
    private String labeller_name;
    private String labelling_date;
    private String labelling_checker;

    public ArrangeInfo() {}

    public ArrangeInfo(Arrange arrange) {

        this.labeller_code = arrange.getArrangerCd();
        this.labeller_name =  CSV.translate(arrange.getArranger().getConsortiumNm());
        this.labelling_date = DateUtil.toFormat_yyyyMMdd(arrange.getArrangeDate().getTime());
        this.labelling_checker =  CSV.translate(arrange.getModUser().getName());
    }

}
