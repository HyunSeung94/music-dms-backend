package com.mesim.sc.repository.rdb.admin.consortium;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_CONSORTIUM")
public class Consortium {

    @Id
    @Column(name = "CONSORTIUM_ID")
    private String id;

    @Column(name = "GROUP_ID")
    private String groupId;

    @Column(name = "CONSORTIUM_NM")
    private String consortiumNm;

    @Column(name = "BALLADE")
    private Integer ballade;

    @Column(name = "DANCE")
    private Integer dance;

    @Column(name = "AGITATION")
    private Integer agitation;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "ARG_RANGE")
    protected String argRange;

    @Column(name = "TONE_COLOR")
    protected String toneColor;

    @Column(name = "GENDER")
    protected String gender;

    @Column(name = "LEVEL")
    protected String level;

    @Builder
    public Consortium(
            String id,
            String groupId,
            String consortiumNm,
            Integer ballade,
            Integer dance,
            Integer agitation,
            String role,
            String argRange,
            String toneColor,
            String gender,
            String level
    ) {
        this.id = id;
        this.groupId = groupId;
        this.consortiumNm = consortiumNm;
        this.ballade = ballade;
        this.dance = dance;
        this.agitation = agitation;
        this.role = role;
        this.argRange = argRange;
        this.toneColor = toneColor;
        this.gender = gender;
        this.level = level;
    }

}
