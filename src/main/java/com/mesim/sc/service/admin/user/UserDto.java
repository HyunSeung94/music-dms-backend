package com.mesim.sc.service.admin.user;

import com.mesim.sc.repository.rdb.admin.user.User;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import com.mesim.sc.util.PwEncoder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends AdminDto {

    private String id;
    private int groupId;
    private String groupNm;
    private int authorityId;
    private String authorityNm;
    private String name;
    private String password;
    private String status;
    private String email;
    private String phone;
    private String mobile;
    private Integer zipCode;
    private String address;
    private Integer pwTry;
    private String pwModId;
    private String pwModDate;
    private String imgSrc;
    private String imgBase64Str;
    private String initial;

    private boolean isPasswordEncoded;

    public UserDto() {}

    public UserDto(User entity) {
        this.id = entity.getId();
        this.groupId = entity.getGroupId();
        this.groupNm = entity.getGroup() != null ? entity.getGroup().getName() : null;
        this.authorityId = entity.getAuthorityId();
        this.authorityNm = entity.getAuthority() != null ? entity.getAuthority().getName() : null;
        this.name = entity.getName();
        this.initial = entity.getInitial();
        this.password = entity.getPassword();
        this.status = entity.getStatus();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();
        this.mobile = entity.getMobile();
        this.zipCode = entity.getZipCode();
        this.address = entity.getAddress();
        this.rmk = entity.getRmk();
        this.pwTry = entity.getPwTry();
        this.pwModId = entity.getPwModId();
        this.pwModDate = entity.getPwModDate() != null ? entity.getPwModDate() : DateUtil.toFormat(entity.getRegDate().getTime());
        this.imgSrc = entity.getImgSrc();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());

//        if (this.imgSrc != null) {
//            String filePath = FileUtil.makePath(FileUtil.DATA_FILE_PATH, FileUtil.INFRALAYER_FILE_PATH, this.imgSrc);
//            this.imgBase64Str = FileUtil.getImgBase64Str(filePath);
//        }

        this.isPasswordEncoded = false;
    }

    @Override
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .groupId(this.groupId)
                .authorityId(this.authorityId)
                .name(this.name)
                .initial(this.initial)
                .password(this.password == null ? null : this.isPasswordEncoded ? this.password : PwEncoder.encode(this.password))
                .status(this.status)
                .email(this.email)
                .phone(this.phone)
                .mobile(this.mobile)
                .zipCode(this.zipCode)
                .address(this.address)
                .rmk(this.rmk)
                .pwTry(this.pwTry == null ? 0 : this.pwTry)
                .pwModId(this.pwModId == null ? this.regId : this.pwModId)
                .pwModDate(this.pwModDate)
                .imgSrc(this.imgSrc)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }

}
