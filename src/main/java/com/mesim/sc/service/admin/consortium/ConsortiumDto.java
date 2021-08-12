package com.mesim.sc.service.admin.consortium;

import com.mesim.sc.repository.rdb.admin.consortium.Consortium;
import com.mesim.sc.service.admin.AdminDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConsortiumDto extends AdminDto {

    private String id;
    private String groupId;
    private String consortiumNm;
    private Integer ballade;
    private Integer dance;
    private Integer agitation;
    private String role;
    protected String argRange;
    protected String toneColor;
    protected String gender;
    protected String level;

    public ConsortiumDto() {}

    public ConsortiumDto(Consortium entity) {
        this.id = entity.getId();
        this.groupId = entity.getGroupId();
        this.consortiumNm = entity.getConsortiumNm();
        this.ballade = entity.getBallade();
        this.dance = entity.getDance();
        this.agitation = entity.getAgitation();
        this.role = entity.getRole();
        this.argRange = entity.getArgRange();
        this.toneColor = entity.getToneColor();
        this.gender = entity.getGender();
        this.level = entity.getLevel();
    }

    @Override
    public Consortium toEntity() {
        return Consortium.builder()
                .id(this.id)
                .groupId(this.groupId)
                .consortiumNm(this.consortiumNm)
                .ballade(this.ballade)
                .dance(this.dance)
                .agitation(this.agitation)
                .role(this.role)
                .argRange(this.argRange)
                .toneColor(this.toneColor)
                .gender(this.gender)
                .level(this.level)
                .build();
    }

}
