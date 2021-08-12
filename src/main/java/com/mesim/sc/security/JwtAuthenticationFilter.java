package com.mesim.sc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mesim.sc.api.ApiResponseDto;
import com.mesim.sc.constants.CodeConstants;
import com.mesim.sc.constants.SecurityConstants;
import com.mesim.sc.exception.BackendException;
import com.mesim.sc.service.admin.system.UserConnectService;
import com.mesim.sc.service.admin.user.UserDto;
import com.mesim.sc.service.admin.user.UserService;
import com.mesim.sc.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserConnectService userConnectService;
    private final UserService userService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext ctx) {
        this.authenticationManager = authenticationManager;
        this.userConnectService = ctx.getBean(UserConnectService.class);
        this.userService = ctx.getBean(UserService.class);
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            UserPrincipal userPrincipal = new ObjectMapper().readValue(request.getInputStream(), UserPrincipal.class);
            String userId = userPrincipal.getUsername();
            String password = userPrincipal.getPassword();
            int attemptCount = -1;

            UserDto userInfo = (UserDto) this.userService.get(userId);
            if (userInfo != null) {
                attemptCount = userInfo.getPwTry();
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

            try {
                if (attemptCount >= 5) {
                    this.userConnectService.save(userId, CodeConstants.CONN_LOGIN, HttpUtil.getIP(request), CodeConstants.CONN_ERR_INFO);
                    response.setStatus(HttpStatus.SC_NOT_ACCEPTABLE);
                    return null;
                }

                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                boolean isAuthenticated;

                isAuthenticated = authentication.isAuthenticated();

                log.info("username : {}, isAuthenticated : {}", userId, isAuthenticated);

                this.userConnectService.save(userId, CodeConstants.CONN_LOGIN, HttpUtil.getIP(request));

                userInfo.setPassword(password);
                userInfo.setPwTry(0);
                this.userService.save(userInfo);

                return authentication;
            } catch (BadCredentialsException e) {
                this.userConnectService.save(userId, CodeConstants.CONN_LOGIN, HttpUtil.getIP(request), CodeConstants.CONN_ERR_INFO);
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                if (attemptCount > -1) {
                    attemptCount++;
                    userInfo.setPwTry(attemptCount);
                    this.userService.save(userInfo);
                }
            }

            return null;

        } catch (IOException | BackendException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        String token = JwtTokenProvider.createToken(user);
        response.setHeader("access-control-expose-headers", SecurityConstants.TOKEN_HEADER);
        response.setHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);

        try {
            ApiResponseDto responseDto = new ApiResponseDto(authentication.isAuthenticated(), authentication.isAuthenticated() ? user : null);
            new ObjectMapper().writeValue(response.getWriter(), responseDto);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
