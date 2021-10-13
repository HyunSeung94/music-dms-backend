package com.mesim.sc.service.admin.arrange;

import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;
import java.util.List;


@Data
public class CompletedArrangeDto implements Comparable<CompletedArrangeDto> {

    private String id;
    private String groupId;
    private String consortiumNm;
    private Integer ballade;
    private Integer dance;
    private Integer agitation;
    private String role;
    protected String ageRange;
    protected String toneColor;
    protected String gender;
    protected String level;
    private Integer goal;
    private Integer completed;

    public CompletedArrangeDto() {
    }

    public CompletedArrangeDto(Arrange entity) {
        if (entity.getVocal().getSinger() != null) {
            this.id = entity.getVocal().getSinger().getId();
            this.groupId = entity.getVocal().getSinger().getGroupId();
            this.ageRange = entity.getVocal().getSinger().getAgeRange();
            this.consortiumNm = entity.getVocal().getSinger().getConsortiumNm();
            this.ballade = entity.getVocal().getSinger().getBallade();
            this.dance = entity.getVocal().getSinger().getDance();
            this.agitation = entity.getVocal().getSinger().getAgitation();
            this.role = entity.getVocal().getSinger().getRole();
            this.ageRange = entity.getVocal().getSinger().getCodeArgRange() != null ? entity.getVocal().getSinger().getCodeArgRange().getName() : null;
            this.toneColor = entity.getVocal().getSinger().getToneColor();
            this.gender = entity.getVocal().getSinger().getGender();
            this.level = entity.getVocal().getSinger().getLevel();
            if (entity.getVocal().getSinger().getBallade() != null) {
                this.goal = entity.getVocal().getSinger().getBallade();
            } else if (entity.getVocal().getSinger().getDance() != null) {
                this.goal = entity.getVocal().getSinger().getDance();
            } else if (entity.getVocal().getSinger().getAgitation() != null) {
                this.goal = entity.getVocal().getSinger().getAgitation();
            }
        }

    }

    @Override
    public int compareTo(CompletedArrangeDto o) {
        int id = Integer.parseInt(this.id.replace("S", ""));
        int getId = Integer.parseInt(o.getId().replace("S", ""));
        if (id < getId) {
            return -1;
        } else if (id > getId) {
            return 1;
        }
        return 0;
    }
}
