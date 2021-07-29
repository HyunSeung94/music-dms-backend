package com.mesim.sc.service.admin.group;

import com.mesim.sc.repository.rdb.admin.authority.Authority;
import com.mesim.sc.repository.rdb.admin.group.Group;
import com.mesim.sc.repository.rdb.admin.group.User;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import com.mesim.sc.util.FileUtil;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
public class UserDto extends AdminDto {

    private String id;
    private String name;
    private String userCd;
    private String password;
    private int groupId;
    private String groupNm;
    private int authorityId;
    private String authorityNm;
    private String role;
    private String gender;
    private String toneColor;
    private String ageRange;
    private String level;
    private Integer pwTry;
    private String pwModId;
    private String pwModDate;
    private String imgSrc;
    private String imgBase64Str;


    // 운영조직 이력 등록을 위한 값
    private Integer preAuthorityId;
    private String preAuthorityNm;

    public UserDto() {}

    public UserDto(String id, String name, String password, int groupId, int authorityId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.groupId = groupId;
        this.authorityId = authorityId;
    }

    public UserDto(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.userCd = entity.getUserCd();
        this.password = entity.getPassword();
        this.groupId = entity.getGroupId();
        this.groupNm = entity.getGroup() != null ? entity.getGroup().getName() : null;
        this.authorityId = entity.getAuthorityId();
        this.authorityNm = entity.getAuthority() != null ? entity.getAuthority().getName() : null;
        this.role = entity.getRole();
        this.gender = entity.getGender();
        this.toneColor = entity.getToneColor();
        this.ageRange = entity.getAgeRange();
        this.level = entity.getLevel();
        this.rmk = entity.getRmk();
        this.pwTry = entity.getPwTry();
        this.pwModId = entity.getPwModId();
        this.imgSrc = entity.getImgSrc();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
        this.pwModDate = entity.getPwModDate()  != null ? entity.getPwModDate() : DateUtil.toFormat(entity.getRegDate().getTime());

        if (this.imgSrc != null) {
//            String filePath = FileUtil.makePath(FileUtil.DATA_FILE_PATH, FileUtil.INFRALAYER_FILE_PATH, this.imgSrc);
//            this.imgBase64Str = FileUtil.getImgBase64Str(filePath);
        }
    }

    @Override
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .name(this.name)
                .password(this.password)
                .groupId(this.groupId)
                .authorityId(this.authorityId)
                .role(this.role)
                .gender(this.gender)
                .toneColor(this.toneColor)
                .ageRange(this.ageRange)
                .level(this.level)
                .rmk(this.rmk)
                .pwTry(this.pwTry == null ? 0 : this.pwTry)
                .pwModId(this.pwModId != null ? this.pwModId :this.regId)
                .pwModDate(this.pwModDate)
                .imgSrc(this.imgSrc)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
