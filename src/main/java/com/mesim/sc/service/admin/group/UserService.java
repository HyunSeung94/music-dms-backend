package com.mesim.sc.service.admin.group;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.group.User;
import com.mesim.sc.repository.rdb.admin.group.UserRepository;

import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("userService")
public class UserService extends AdminService {

    private static final Cache<String, String> imgCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    @Value("${file.data.base.path}")
    private String fileBasePath;

    @Value("${file.data.infralayer.path}")
    private String userProfilePath;

    @Autowired
    private ApplicationContext context;

    private PasswordEncoder encoder;

    @Autowired
    @Qualifier("userRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.encoder = this.context.getBean(PasswordEncoder.class);

        this.searchFields = new String[]{"id", "name", "mobile"};
//        this.joinedSortField = new String[]{"authority", "group"};
//
//        this.addRefEntity("authority", "name");
//        this.addRefEntity("group", "name");
//
//        this.excludeColumn = new String[]{"password", "preAuthorityId", "preAuthorityNm"};

        super.init();
    }

    public List<String> getIds() {
        return ((UserRepository) this.repository).findAll().stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    public Object add(Object o) throws BackendException {
        UserDto userDto = this.mapper.convertValue(o, UserDto.class);
        Optional<User> optUser = this.repository.findById(userDto.getId());

        if (optUser.isPresent()) {
            throw new BackendException(CommonConstants.EX_PK_VIOLATION);
        }

        userDto.setPassword(this.encoder.encode(userDto.getPassword()));

        UserDto saveUser = (UserDto) super.save(userDto);

        return saveUser;
    }

    public Object modify(Object o) throws BackendException {
        UserDto userDto = this.mapper.convertValue(o, UserDto.class);
        Optional<User> optUser = ((UserRepository) this.repository).findById(userDto.getId());

        if (optUser.isPresent()) {
            User user = optUser.get();
            userDto.setPassword(user.getPassword());

            UserDto saveUser = (UserDto) super.save(userDto);

            return saveUser;
        } else {
            throw new BackendException("존재하지 않는 사용자입니다.");
        }
    }

    public Object changePassword(Object o) throws BackendException {
        PasswordChangeDto passwordChangeDto = this.mapper.convertValue(o, PasswordChangeDto.class);

        Optional<User> optUser = ((UserRepository) this.repository).findById(passwordChangeDto.getUserId());

        if (optUser.isPresent()) {
            UserDto userDto = (UserDto) this.toDto(optUser.get(), 0);
            if (!this.encoder.matches(passwordChangeDto.getChangePassword(), userDto.getPassword())) {
                userDto.setPassword(this.encoder.encode(passwordChangeDto.getChangePassword()));
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

            return super.save(userDto);
        } else {
            throw new BackendException("존재하지 않는 사용자입니다.");
        }
    }

    public boolean confirmPassword(Object o) throws BackendException {
        PasswordChangeDto passwordChangeDto = this.mapper.convertValue(o, PasswordChangeDto.class);

        Optional<User> optUser = ((UserRepository) this.repository).findById(passwordChangeDto.getUserId());

        if (optUser.isPresent()) {
            User user = optUser.get();
            return this.encoder.matches(passwordChangeDto.getPassword(), user.getPassword());
        } else {
            throw new BackendException("존재하지 않는 사용자입니다.");
        }
    }


    public List<UserDto> getListByGroupId(int groupId) {
        List<UserDto> userDtoList = new ArrayList<>();
        ((UserRepository) this.repository).findAllByGroupId(groupId).forEach(user -> {
            userDtoList.add(new UserDto(user));
        });
        return userDtoList;
    }

    public UserDto getUserByUserId(String userId) {
        UserDto userDto = new UserDto();
        Optional<User> userOptional = ((UserRepository) this.repository).findById(userId);
        userDto.setId(userOptional.get().getId());
        userDto.setName(userOptional.get().getName());
        userDto.setGroupId(userOptional.get().getGroupId());
        userDto.setGroupNm(userOptional.get().getGroup().getName());
        return userDto;
    }
}
