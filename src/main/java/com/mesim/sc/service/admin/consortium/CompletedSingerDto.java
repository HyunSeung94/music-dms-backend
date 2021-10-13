package com.mesim.sc.service.admin.consortium;

import com.mesim.sc.repository.rdb.admin.arrange.Arrange;
import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import lombok.Data;


@Data
public class CompletedSingerDto implements Comparable<CompletedSingerDto> {

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

    public CompletedSingerDto() {
    }

    public CompletedSingerDto(ConsortiumDto entity) {

            this.id = entity.getId();
            this.groupId = entity.getGroupId();
            this.ageRange = entity.getAgeRange();
            this.consortiumNm = entity.getConsortiumNm();
            this.ballade = entity.getBallade();
            this.dance = entity.getDance();
            this.agitation = entity.getAgitation();
            this.role = entity.getRole();
            this.toneColor = entity.getToneColor();
            this.gender = entity.getGender();
            this.level = entity.getLevel();
            if (entity.getBallade() != null) {
                this.goal = entity.getBallade();
            } else if (entity.getDance() != null) {
                this.goal = entity.getDance();
            } else if (entity.getAgitation() != null) {
                this.goal = entity.getAgitation();
            }

    }

    @Override
    public int compareTo(CompletedSingerDto o) {
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
