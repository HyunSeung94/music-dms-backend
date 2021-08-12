package com.mesim.sc.service.admin.system;

import com.mesim.sc.repository.rdb.admin.system.UserConnect;
import com.mesim.sc.service.SeqDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserConnectDto extends SeqDto {

    private int id;
    private String userId;
    private String userNm;
    private String typeCd;
    private String typeNm;
    private String connectIp;
    private String connectDate;
    private String errorCode;
    private String errorCodeNm;
    private String errorContents;
    private boolean errorYn;

    public UserConnectDto() {}

    public UserConnectDto(UserConnect entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.userNm = entity.getUserNm();
        this.typeCd = entity.getTypeCd();
        this.typeNm = entity.getType() != null ? entity.getType().getName() : null;
        this.connectIp = entity.getConnectIp();
        this.connectDate = DateUtil.toFormat(entity.getConnectDate().getTime());
        this.errorCode = entity.getErrorCode();
        this.errorCodeNm = entity.getConnError() != null ? entity.getConnError().getName() : null;
        this.errorContents = entity.getErrorContents();
        this.errorYn = entity.getErrorYn() == 1;
    }

    @Override
    public UserConnect toEntity() {
        return UserConnect.builder()
                .id(this.id)
                .userId(this.userId)
                .userNm(this.userNm)
                .typeCd(this.typeCd)
                .connectIp(this.connectIp)
                .errorCode(this.errorCode)
                .errorContents(this.errorContents)
                .errorYn(this.errorYn ? 1 : 0)
                .build();
    }

}