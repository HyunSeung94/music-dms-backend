package com.mesim.sc.service.admin.system;

import com.mesim.sc.constants.CodeConstants;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.system.UserAccess;
import com.mesim.sc.repository.rdb.admin.user.User;
import com.mesim.sc.repository.rdb.admin.user.UserRepository;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Service
@Qualifier("userAccessService")
public class UserAccessService extends AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("userAccessRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.defaultSortField = "accessDate";
        this.joinedSortField = new String[]{"type", "accessError"};
        this.searchFields = new String[]{"userId", "userNm", "typeName", "apiPath", "accessIp"};
        this.searchDateField = "accessDate";

        this.addRefEntity("type", "name");

        super.init();
    }

    public Object save(HttpServletRequest request, String typeCd, String apiPath) {
        String userId = request.getRemoteUser();

        Optional<User> user = this.userRepository.findById(userId);
        String userNm = user.map(User::getName).orElse(null);

        String ip = HttpUtil.getIP(request);

        UserAccess userAccess = UserAccess.builder()
                .userId(userId)
                .userNm(userNm)
                .typeCd(typeCd)
                .apiPath(apiPath)
                .accessIp(ip)
                .errorYn(0)
                .build();

        return this.repository.saveAndFlush(userAccess);
    }

    public Object save(HttpServletRequest request, String typeCd, String apiPath, String errorCode, String errorContents) {
        String userId = request.getRemoteUser();

        Optional<User> user = this.userRepository.findById(userId);
        String userNm = user.map(User::getName).orElse(null);

        String ip = HttpUtil.getIP(request);

        UserAccess userAccess = UserAccess.builder()
                .userId(userId)
                .userNm(userNm)
                .typeCd(typeCd)
                .apiPath(apiPath)
                .accessIp(ip)
                .errorYn(1)
                .errorContents(errorContents)
                .build();

        return this.repository.saveAndFlush(userAccess);
    }

    public Object save(String userId, String typeCd, String apiPath, String ip) {
        Optional<User> user = this.userRepository.findById(userId);

        String userNm = user.map(User::getName).orElse(null);

        UserAccess userAccess = UserAccess.builder()
                .userId(userId)
                .userNm(userNm)
                .typeCd(typeCd)
                .apiPath(apiPath)
                .accessIp(ip)
                .errorYn(0)
                .build();

        return this.repository.saveAndFlush(userAccess);
    }

    public Object save(String userId, String typeCd,  String apiPath, String ip, String errorCode) {
        Optional<User> user = this.userRepository.findById(userId);

        String userNm = user.map(User::getName).orElse(null);

        UserAccess userAccess = UserAccess.builder()
                .userId(userId)
                .userNm(userNm)
                .typeCd(typeCd)
                .apiPath(apiPath)
                .accessIp(ip)
                .errorCode(errorCode)
                .errorYn(1)
                .build();

        return this.repository.saveAndFlush(userAccess);
    }

    public Object save(String userId, String typeCd, String apiPath, String ip, Exception exception) {
        Optional<User> user = this.userRepository.findById(userId);

        String userNm = user.map(User::getName).orElse(null);

        UserAccess userAccess = UserAccess.builder()
                .userId(userId)
                .userNm(userNm)
                .typeCd(typeCd)
                .apiPath(apiPath)
                .accessIp(ip)
                .errorCode(CodeConstants.ACCESS_ERR_EXCEPTION)
                .errorContents(exception.getMessage())
                .errorYn(1)
                .build();

        return this.repository.saveAndFlush(userAccess);
    }

}
