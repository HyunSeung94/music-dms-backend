package com.mesim.sc.service.admin.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.user.User;
import com.mesim.sc.repository.rdb.admin.user.UserRepository;

import com.mesim.sc.security.JwtTokenProvider;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.util.PwEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("userService")
public class UserService extends AdminService {

    @Autowired
    @Qualifier("userRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"id", "name", "authorityName", "groupName"};
        this.joinedSortField = new String[]{"authority", "group"};
        this.excludeColumn = new String[]{"password", "imgBase64Str"};
        this.root.put("id", CommonConstants.USER_ROOT_ID);

        this.addRefEntity("authority", "name");
        this.addRefEntity("group", "name");

        super.init();
    }

    public Object changePassword(Object o) throws BackendException {
        PasswordChangeDto passwordChangeDto = this.mapper.convertValue(o, PasswordChangeDto.class);

        Optional<User> optUser = ((UserRepository) this.repository).findById(passwordChangeDto.getUserId());

        if (optUser.isPresent()) {
            UserDto userDto = this.mapper.convertValue(optUser.get(), UserDto.class);

            String originPw = userDto.getPassword();
            String changePw = passwordChangeDto.getChangePassword();

            if (PwEncoder.notMatch(originPw, PwEncoder.encode(changePw))) {
                userDto.setPassword(changePw);
            } else {
                throw new BackendException("이전 비밀번호와 신규 비밀번호가 동일합니다.");
            }

            if (passwordChangeDto.getUserId().equals(passwordChangeDto.getChangeUserId())) {
                userDto.setPwModId(passwordChangeDto.getUserId());
                userDto.setPwModDate(userDto.getModDate());
            } else {
                userDto.setPwModId(passwordChangeDto.getChangeUserId());
                userDto.setPwModDate(userDto.getRegDate());
            }

            return this.save(userDto);
        } else {
            throw new BackendException("존재하지 않는 사용자입니다.");
        }
    }

    public boolean confirmPassword(Object o) throws BackendException {
        PasswordChangeDto passwordChangeDto = this.mapper.convertValue(o, PasswordChangeDto.class);

        Optional<User> optUser = ((UserRepository) this.repository).findById(passwordChangeDto.getUserId());

        if (optUser.isPresent()) {
            UserDto userDto = this.mapper.convertValue(optUser.get(), UserDto.class);

            String originPw = userDto.getPassword();
            String changePw = passwordChangeDto.getChangePassword();

            return PwEncoder.notMatch(originPw, PwEncoder.encode(changePw));
        } else {
            throw new BackendException("존재하지 않는 사용자입니다.");
        }
    }

    public List<UserDto> connectUser() {
        return ((UserRepository) this.repository).findAllById(JwtTokenProvider.getTokenIds())
                .stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

}
