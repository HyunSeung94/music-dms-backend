package com.mesim.sc.service.admin.group;

import com.mesim.sc.repository.rdb.admin.group.Group;
import com.mesim.sc.service.admin.AdminDto;
import com.mesim.sc.util.DateUtil;
import lombok.Data;

@Data
public class GroupDto extends AdminDto {

    private int id;
    private String name;
    private int pid;
    private String pName;
    private String typeCd;
    private String typeNm;
    private String phone;
    private Integer zipCode;
    private String address;

    public GroupDto() {}

    public GroupDto(Group entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.pid = entity.getPid();
        this.pName = entity.getPgroup() != null ? entity.getPgroup().getName() : null;
        this.typeCd = entity.getTypeCd();
        this.typeNm = entity.getType() != null ? entity.getType().getName() : null;
        this.phone = entity.getPhone();
        this.zipCode = entity.getZipCode();
        this.address = entity.getAddress();
        this.rmk = entity.getRmk();
        this.regId = entity.getRegId();
        this.regDate = DateUtil.toFormat(entity.getRegDate().getTime());
        this.modId = entity.getModId();
        this.modDate = DateUtil.toFormat(entity.getModDate().getTime());
    }

    @Override
    public Group toEntity(){
        return Group.builder()
                .id(this.id)
                .pid(this.pid)
                .name(this.name)
                .typeCd(this.typeCd)
                .phone(this.phone)
                .zipCode(this.zipCode)
                .address(this.address)
                .rmk(this.rmk)
                .regId(this.regId)
                .modId(this.modId == null ? this.regId : this.modId)
                .build();
    }
}
