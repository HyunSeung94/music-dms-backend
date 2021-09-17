package com.mesim.sc.service.admin.user;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.exception.ExceptionHandler;
import com.mesim.sc.repository.PageWrapper;
import com.mesim.sc.repository.rdb.CrudRepository;
import com.mesim.sc.repository.rdb.admin.AdminSpecs;
import com.mesim.sc.repository.rdb.admin.user.User;
import com.mesim.sc.repository.rdb.admin.user.UserRepository;

import com.mesim.sc.security.JwtTokenProvider;
import com.mesim.sc.service.admin.AdminService;
import com.mesim.sc.util.DateUtil;
import com.mesim.sc.util.PwEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("userService")
public class UserService extends AdminService {

    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

    @Autowired
    @Qualifier("userRepository")
    public void setRepository(CrudRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init () {
        this.searchFields = new String[]{"id", "name", "authorityName", "groupName", "status"};
        this.joinedSortField = new String[]{"authority", "group"};
        this.excludeColumn = new String[]{"password", "imgBase64Str"};
        this.root.put("id", CommonConstants.USER_ROOT_ID);

        this.addRefEntity("authority", "name");
        this.addRefEntity("group", "name");

        super.init();
    }

    // 전체 목록 조회
    public PageWrapper getListPage(String regId, String regGroupId, String[] select, int index, int size, String[] sortProperties, String[] keywords, String searchOp, String fromDate, String toDate) throws BackendException {
        Specification<Object> spec = null;

        if (!regGroupId.equals(Integer.toString(CommonConstants.GROUP_ROOT_ID))) {
            if (regGroupId.equals(Integer.toString(CommonConstants.GROUP_CHILL_ROOT_ID))) {
                spec = AdminSpecs.regGroupId(CommonConstants.GROUP_CHILL_ID).or(AdminSpecs.regGroupId(CommonConstants.GROUP_CHILL_ROOT_ID));
            } else if (regGroupId.equals(Integer.toString(CommonConstants.GROUP_FKMP_ROOT_ID))) {
                spec = AdminSpecs.regGroupId(CommonConstants.GROUP_FKMP_ID).or(AdminSpecs.regGroupId(CommonConstants.GROUP_FKMP_ROOT_ID));
            } else {
                spec = AdminSpecs.regId(regId);
            }
        }

        PageRequest pageRequest = this.getPageRequest(index, size, sortProperties);
        Specification<Object> pageSpec = this.getSpec(select, keywords, searchOp, fromDate, toDate);

        if (pageSpec != null) {
            spec = spec == null ? pageSpec : spec.and(pageSpec);
        }

        Page<Object> page = this.repository.findAll(spec, pageRequest);
        PageWrapper result = new PageWrapper(page);
        final AtomicInteger seq = new AtomicInteger(1);

        List<Object> list = page
                .get()
                .map(ExceptionHandler.wrap(entity -> this.toDto(entity, seq.getAndIncrement() + (result.getNumber() * size))))
                .collect(Collectors.toList());

        result.setList(list);

        return result;
    }

    // 사용자 추가
    public Object add(Object data) throws BackendException {
        return this.save(data);
    }

    // 중복 조회
    public boolean isDuplicated(String userId) throws BackendException {
        Optional<User> optUser = ((UserRepository) this.repository).findById(userId);
        if (optUser.isPresent()) return true;
        return false;
    }

    // 사용자 삭제
    @Override
    public boolean delete(Object o) {
        Map<String, Object> map = (Map<String, Object>) o;
        List<Object> deleteObjects = ((List<Object>) map.get("data"))
                .stream()
                .map(ExceptionHandler.wrap(object -> this.toEntity(object)))
                .collect(Collectors.toList());

        this.repository.deleteAll(deleteObjects);

        return true;
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
                userDto.setPwModDate(DateUtil.toFormat(Long.parseLong(userDto.getModDate())));
            } else {
                userDto.setPwModId(passwordChangeDto.getChangeUserId());
                userDto.setPwModDate(DateUtil.toFormat(Long.parseLong(userDto.getRegDate())));
            }

            return this.save(userDto);
        } else {
            throw new BackendException("존재하지 않는 사용자입니다.");
        }
    }

//    public boolean confirmPassword(Object o) throws BackendException {
//        PasswordChangeDto passwordChangeDto = this.mapper.convertValue(o, PasswordChangeDto.class);
//
//        Optional<User> optUser = ((UserRepository) this.repository).findById(passwordChangeDto.getUserId());
//
//        if (optUser.isPresent()) {
//            UserDto userDto = this.mapper.convertValue(optUser.get(), UserDto.class);
//
//            String originPw = userDto.getPassword();
//            String changePw = passwordChangeDto.getChangePassword();
//
//            return PwEncoder.notMatch(originPw, PwEncoder.encode(changePw));
//        } else {
//            throw new BackendException("존재하지 않는 사용자입니다.");
//        }
//    }

    public Object grantUser(String userId) throws BackendException {
        Optional<User> optUser = ((UserRepository) this.repository).findById(userId);

        if (optUser.isPresent()) {
            UserDto userDto = this.mapper.convertValue(optUser.get(), UserDto.class);
            userDto.setStatus("Y");
            userDto.setPasswordEncoded(true);
            return this.save(userDto);
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
