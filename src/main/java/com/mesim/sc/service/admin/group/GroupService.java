package com.mesim.sc.service.admin.group;

import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.group.GroupRepository;
import com.mesim.sc.repository.rdb.admin.group.User;
import com.mesim.sc.repository.rdb.admin.group.UserRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@Qualifier("groupService")
public class GroupService extends AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("groupRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"name"};
        this.defaultSortField = "name";
        this.joinedSortField = new String[]{"pgroup"};

        this.addRefEntity("pgroup","name");

        this.excludeColumn = new String[]{"pid", "pName"};

        super.init();
    }

    public ApiResponseDto checkDelete(String id) {
        int _id = Integer.valueOf(id);

        User user = userRepository.findByGroupId(_id);
        if (user == null) {
            this.repository.deleteById(_id);
            return new ApiResponseDto(true);
        } else {
            return new ApiResponseDto(false, null, CommonConstants.EX_FK_VIOLATION);
        }
    }

    public Object getListSelect() {
        return ((GroupRepository) this.repository).findAllByIdNotOrderByName(CommonConstants.GROUP_ROOT_ID);
    }
}
