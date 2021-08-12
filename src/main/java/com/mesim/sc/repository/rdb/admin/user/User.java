package com.mesim.sc.repository.rdb.admin.user;

import com.mesim.sc.repository.rdb.CrudEntity;
import com.mesim.sc.repository.rdb.admin.authority.Authority;
import com.mesim.sc.repository.rdb.admin.group.Group;
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

    @Column(name = "AUTHORITY_ID")
    private int authorityId;

    @Column(name = "USER_NM")
    private String name;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "MOBILE")
    private String mobile;

    @Column(name = "ZIP_CODE")
    private Integer zipCode;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "RMK")
    private String rmk;

    @Column(name = "PW_TRY")
    private Integer pwTry;

    @Column(name = "PW_MOD_ID")
    private String pwModId;

    @Column(name = "PW_MOD_DATE")
    private String pwModDate;

    @Column(name = "IMG_SRC")
    private String imgSrc;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="GROUP_ID", referencedColumnName="GROUP_ID", insertable = false, updatable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="AUTHORITY_ID", referencedColumnName="AUTHORITY_ID", insertable = false, updatable = false)
    private Authority authority;

    @Builder
    public User(
            String id,
            int groupId,
            int authorityId,
            String name,
            String password,
            String email,
            String phone,
            String mobile,
            Integer zipCode,
            String address,
            String rmk,
            Integer pwTry,
            String pwModId,
            String pwModDate,
            String imgSrc,
            String regId,
            String modId
    ) {
        super(regId, modId);

        this.id = id;
        this.groupId = groupId;
        this.authorityId = authorityId;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.mobile = mobile;
        this.zipCode = zipCode;
        this.address = address;
        this.rmk = rmk;
        this.pwTry = pwTry;
        this.pwModId = pwModId;
        this.pwModDate = pwModDate;
        this.imgSrc = imgSrc;
    }

}
