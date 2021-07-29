package com.mesim.sc.api.rest.auth;

import com.mesim.sc.api.ApiResponseDto;

import com.mesim.sc.service.auth.AuthService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthService authService;


    /**
     * 토근발행여부 조회
     *
     * @param userId
     * @return
     */
    @RequestMapping(value="/checkExist", method = RequestMethod.GET)
    public ApiResponseDto checkExist(@RequestParam(value="userId") String userId) {
        try {
            return new ApiResponseDto(authService.checkExistLogin(userId));
        } catch(Exception e) {
            log.error("로그인 처리 중 오류발생", e);
            return new ApiResponseDto(false, null);
        }
    }

    /**
     *
     * 사용자 인증여부에 따라
     * 사용자 정보 및 매핑된 권한에 따른 메뉴 조회
     *
     * @param authentication
     * @return
     */
    @RequestMapping(value="/validate", method = RequestMethod.GET)
    public ApiResponseDto validateLogin(@RequestParam(value="translate", required = false) String translate, Authentication authentication) {
        HashMap<String, Object> resDataObj = new HashMap<>();
        try {
            resDataObj.put("user", authService.getUser(authentication));
//            resDataObj.put("menu", authService.getMenuByAuthentication(translate, authentication));
            return new ApiResponseDto(authentication.isAuthenticated(), resDataObj);
        } catch(Exception e) {
            log.error("로그인 확인 처리 중 오류발생", e);
            return new ApiResponseDto(false, null);
        }
    }

    /**
     * 로그아웃
     *
     * @param authentication
     * @return
     */
    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public ApiResponseDto logout(HttpServletRequest request, Authentication authentication) {
        try {
            this.authService.logout(authentication);
//            this.userConnectService.save(authentication, CodeConstants.CONN_LOGOUT, HttpUtil.getIP(request));
            return new ApiResponseDto(true);
        } catch(Exception e) {
            log.error("로그아웃 처리 중 오류발생", e);
//            this.userConnectService.save(authentication, CodeConstants.CONN_LOGOUT, HttpUtil.getIP(request), e);
            return new ApiResponseDto(false, null);
        }
    }

}
