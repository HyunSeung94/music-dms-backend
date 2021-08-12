package com.mesim.sc.service.admin.system;

import com.mesim.sc.constants.CodeConstants;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.system.UserConnect;
import com.mesim.sc.repository.rdb.admin.user.User;
import com.mesim.sc.repository.rdb.admin.user.UserRepository;
import com.mesim.sc.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Service
@Qualifier("userConnectService")
public class UserConnectService extends AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("userConnectRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.defaultSortField = "connectDate";
        this.joinedSortField = new String[]{"type", "connError"};
        this.searchFields = new String[]{"userId", "userNm", "typeName", "connectIp"};
        this.searchDateField = "connectDate";

        this.addRefEntity("type", "name");

        super.init();
    }

    public Object save(Authentication authentication, String type, String ip) {
        String userId = (String) authentication.getPrincipal();
        return this.save(userId, type, ip);
    }

    public Object save(Authentication authentication, String type, String ip, String errorCode) {
        String userId = (String) authentication.getPrincipal();
        return this.save(userId, type, ip, errorCode);
    }

    public Object save(Authentication authentication, String typeCd, String ip, Exception e) {
        String userId = (String) authentication.getPrincipal();
        return this.save(userId, typeCd, ip, e);
    }

    public Object save(String userId, String typeCd, String ip) {
        Optional<User> user = this.userRepository.findById(userId);

        String userNm = user.map(User::getName).orElse(null);

        UserConnect userConnect = UserConnect.builder()
                .userId(userId)
                .userNm(userNm)
                .typeCd(typeCd)
                .connectIp(ip)
                .errorYn(0)
                .build();

        return this.repository.saveAndFlush(userConnect);
    }

    public Object save(String userId, String typeCd, String ip, String errorCode) {
        Optional<User> user = this.userRepository.findById(userId);

        String userNm = user.map(User::getName).orElse(null);

        UserConnect userConnect = UserConnect.builder()
                .userId(userId)
                .userNm(userNm)
                .typeCd(typeCd)
                .connectIp(ip)
                .errorCode(errorCode)
                .errorYn(1)
                .build();

        return this.repository.saveAndFlush(userConnect);
    }

    public Object save(String userId, String typeCd, String ip, Exception e) {
        Optional<User> user = this.userRepository.findById(userId);

        String userNm = user.map(User::getName).orElse(null);

        UserConnect userConnect = UserConnect.builder()
                .userId(userId)
                .userNm(userNm)
                .typeCd(typeCd)
                .connectIp(ip)
                .errorCode(CodeConstants.CONN_ERR_EXCEPTION)
                .errorContents(e.getMessage())
                .errorYn(1)
                .build();

        return this.repository.saveAndFlush(userConnect);
    }

}
