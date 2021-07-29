package com.mesim.sc.service.admin.group;

import lombok.Data;

@Data
public class PasswordChangeDto {

    private String userId;
    private String password;
    private String changeUserId;
    private String changePassword;

}
