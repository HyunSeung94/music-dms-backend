package com.mesim.sc.repository.rdb.admin.group;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.authority.Authority;
import com.mesim.sc.repository.rdb.admin.song.CreativeSong;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity(name = "TB_ADMIN_IT_USER")
public class User extends CrudEntity {

    @Id
    @Column(name = "USER_ID")
    private String id;

    @Column(name = "GROUP_ID")
    private int groupId;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="GROUP_ID", referencedColumnName="GROUP_ID", insertable = false, updatable = false)
    private Group group;

    @Column(name = "AUTHORITY_ID")
    private int authorityId;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="AUTHORITY_ID", referencedColumnName="ROLE_ID", insertable = false, updatable = false)
    private Authority authority;

    @Column(name = "USER_NM")
    private String name;

    @Column(name = "USER_CD")
    private String userCd;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "TONE_COLOR")
    private String toneColor;

    @Column(name = "AGE_RANGE")
    private String ageRange;

    @Column(name = "LEVEL")
    private String level;

    @Column(name = "RMK")
    private String rmk;

    /* Profile */
    @Column(name = "PW_TRY")
    private Integer pwTry;

    @Column(name = "PW_MOD_ID")
    private String pwModId;

    @Column(name = "PW_MOD_DATE")
    private String pwModDate;

    @Column(name = "IMG_SRC")
    private String imgSrc;

    @Builder
    public User(String id, int groupId, Group group, int authorityId, Authority authority, String name, String userCd, String password, String role, String gender, String toneColor, String ageRange, String level, String rmk, Integer pwTry, String pwModId, String pwModDate, String imgSrc,String regId, String modId) {
        super(regId, modId);
        this.id = id;
        this.groupId = groupId;
        this.group = group;
        this.authorityId = authorityId;
        this.authority = authority;
        this.name = name;
        this.userCd = userCd;
        this.password = password;
        this.role = role;
        this.gender = gender;
        this.toneColor = toneColor;
        this.ageRange = ageRange;
        this.level = level;
        this.rmk = rmk;
        this.pwTry = pwTry;
        this.pwModId = pwModId;
        this.pwModDate = pwModDate;
        this.imgSrc = imgSrc;
    }

}
