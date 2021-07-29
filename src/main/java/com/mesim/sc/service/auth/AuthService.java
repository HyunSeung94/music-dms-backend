package com.mesim.sc.service.auth;

import com.mesim.sc.constants.CommonConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.repository.rdb.admin.authority.Authority;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityMenuMapper;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityMenuMapperRepository;
import com.mesim.sc.repository.rdb.admin.authority.AuthorityRepository;
import com.mesim.sc.repository.rdb.admin.group.User;
import com.mesim.sc.repository.rdb.admin.group.UserRepository;

import com.mesim.sc.security.JwtTokenProvider;
import com.mesim.sc.service.admin.group.UserDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityMenuMapperRepository rmMapperRepository;

    @Autowired
    AuthorityRepository authorityRepository;

//    @Autowired
//    MenuTranslateRepository menuTranslateRepository;

    /**
     * 인증 사용자 정보 조회
     *
     * @param authentication 인증정보
     * @return 사용자 정보
     */
    public UserDto getUser(Authentication authentication) {
        Optional<User> optUser = userRepository.findById(authentication.getName());
        return optUser.map(UserDto::new).orElse(null);
    }

    /**
     * 토근발행 여부 조회
     *
     * @param userId 사용자 ID
     * @return 토큰발행 여부
     */
    public boolean checkExistLogin(String userId) {
        return JwtTokenProvider.checkToken(userId) == null;
    }

    /**
     * 로그아웃 처리(토큰 삭제)
     *
     * @param authentication 인증정보
     */
    public void logout(Authentication authentication){
        String userId = (String) authentication.getPrincipal();
        JwtTokenProvider.invalidateToken(userId);
    }


    /**
     * 인증된 사용자의 메뉴 목록 조회
     *
     * @param translate 다국어코드
     * @param authentication 인증정보
     * @return 트리구조 메뉴 목록
     * @throws BackendException
     */
//    public List<MenuDto> getMenuByAuthentication(String translate, Authentication authentication) throws BackendException {
//        Object[] authorities = authentication.getAuthorities().toArray();
//        if (authorities.length > 0) {
//            SimpleGrantedAuthority auth = (SimpleGrantedAuthority) authorities[0];
//            int authorityId = Integer.parseInt(auth.getAuthority().substring("ROLE_".length()));
//            return getMenuListByAuthorityId(authorityId, translate);
//        } else {
//            throw new BackendException("사용자 권한정보가 없습니다.");
//        }
//
//    }

    /**
     * 권한 ID에 매핑된 메뉴 목록 조회
     *
     * @param authorityId 권한 ID
     * @return 메뉴 목록
     */
//    private List<MenuDto> getMenuListByAuthorityId (int authorityId, String translate) {
//        try {
//            List<MenuDto> result = new ArrayList<>();
//
//            Optional<Authority> optAuthority = authorityRepository.findById(authorityId);
//
//            if (optAuthority.isPresent()) {
//                Authority authority = optAuthority.get();
//                List<AuthorityMenuMapper> rmMapperList = authority.getAuthorityMenuMappers();
//
//                rmMapperList.forEach(rmMapper -> {
//                    MenuDto menuDto = new MenuDto((rmMapper.getMenu()));
//
//                    if (!result.contains(menuDto)) {
//                        if (translate != null && !translate.equals("0")) {
//                            MenuTranslate menuTranslate = menuTranslateRepository.findByIdAndTranslateCd(menuDto.getId(), translate);
//                            if (menuTranslate != null) {
//                                menuDto.setName(menuTranslate.getName());
//                            }
//                        }
//                        result.add(menuDto);
//                    }
//                });
//            }
//
//            result.add(0, new MenuDto(CommonConstants.MENU_ROOT_ID, CommonConstants.MENU_ROOT_NAME, -1, "/"));
//
//            return getMenuTree(CommonConstants.MENU_ROOT_ID, result);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    /**
//     * 메뉴 목록을 트리구조로 변환
//     *
//     * @param parentId 상위 메뉴 ID
//     * @param originMenuList 원본 메뉴 목록
//     * @return 트리구조 메뉴 목록
//     */
//    private List<MenuDto> getMenuTree(int parentId, List<MenuDto> originMenuList) {
//
//        List<MenuDto> menuList = new ArrayList<>();
//
//        for (MenuDto menu : originMenuList) {
//            try {
//                if (menu.getId() != parentId && menu.getPid() == parentId) {
//                    List<MenuDto> children = getMenuTree(menu.getId(), originMenuList);
//
//                    if (children.size() > 0) {
//                        menu.setChildren(children);
//                    }
//                    menuList.add(menu);
//                }
//            } catch(NullPointerException e) {
//                log.error(menu.toString());
//            }
//        }
//
//        return menuList.stream()
//            .sorted(Comparator.comparing(MenuDto::getSort))
//            .collect(Collectors.toList());
//    }
}
