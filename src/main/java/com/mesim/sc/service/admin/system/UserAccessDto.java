package com.mesim.sc.service.admin.system;

import com.mesim.sc.repository.rdb.admin.system.UserAccess;
import com.mesim.sc.service.SeqDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserAccessDto extends SeqDto {

    private int id;
    private String userId;
    private String userNm;
    private String typeCd;
    private String typeNm;
    private String apiPath;
    private String accessIp;
    private String accessDate;
    private String errorCode;
    private String errorCodeNm;
    private String errorContents;
    private boolean errorYn;

    public UserAccessDto() {}

    public UserAccessDto(UserAccess entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.userNm = entity.getUserNm();
        this.apiPath = entity.getApiPath();
        this.typeCd = entity.getTypeCd();
        this.typeNm = entity.getType() != null ? entity.getType().getName() : null;
        this.accessIp = entity.getAccessIp();
        this.accessDate = DateUtil.toFormat(entity.getAccessDate().getTime());
        this.errorCode = entity.getErrorCode();
        this.errorCodeNm = entity.getAccessError() != null ? entity.getAccessError().getName() : null;
        this.errorContents = entity.getErrorContents();
        this.errorYn = entity.getErrorYn() == 1;
    }

    @Override
    public UserAccess toEntity() {
        return UserAccess.builder()
                .id(this.id)
                .userId(this.userId)
                .userNm(this.userNm)
                .typeCd(this.typeCd)
                .apiPath(this.apiPath)
                .accessIp(this.accessIp)
                .errorCode(this.errorCode)
                .errorContents(this.errorContents)
                .errorYn(this.errorYn ? 1 : 0)
                .build();
    }

}