package com.mesim.sc.security;

import com.mesim.sc.constants.CodeConstants;
import com.mesim.sc.constants.SecurityConstants;
import com.mesim.sc.service.admin.user.UserDto;
import com.mesim.sc.service.admin.user.UserService;
import com.mesim.sc.service.admin.system.UserConnectService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static boolean USE_DEV_TOKEN;
    private static String DEV_TOKEN = "DEV_TOKEN";
    private static String DEV_TOKEN_USERNAME;
    private static String DEV_TOKEN_ROLE;

    private static UserConnectService userConnectService;

    @Value("${security.use-dev-token}")
    private void setValue1(boolean value) {
        USE_DEV_TOKEN = value;
    }

    @Value("${security.use-dev-token-username}")
    private void setValue2(String value) {
        DEV_TOKEN_USERNAME = value;
    }

    @Value("${security.use-dev-token-authority}")
    private void setValue3(String value) {
        DEV_TOKEN_ROLE = value;
    }

    @Autowired
    private void setService(UserConnectService service) {
        userConnectService = service;
    }

    private static ConcurrentMap<String, String> tokenMap = new ConcurrentHashMap<>();

    public static String createToken(User user) {
        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

        String token = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                .setIssuer(SecurityConstants.TOKEN_ISSUER)
                .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .claim("rol", roles)
                .compact();

        log.info("Create token : {}, {}", user.getUsername(), token);

        tokenMap.put(user.getUsername(), token);

        return token;
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {

        if (StringUtils.isNotEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {

            token = token.replace("Bearer ", "");

            if (USE_DEV_TOKEN && token.equals(DEV_TOKEN)) {
                return getDevAuthentication();
            }

            try {
                byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

                Jws<Claims> parsedToken = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token);

                String userId = parsedToken
                        .getBody()
                        .getSubject();

                Collection<GrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
                        .get("rol"))
                        .stream()
                        .map(authority -> new SimpleGrantedAuthority((String) authority))
                        .collect(Collectors.toList());

                if (StringUtils.isNotEmpty(userId)) {

                    String userToken = tokenMap.get(userId);

                    if (userToken != null && userToken.equals(token)) {
                        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    } else {
                        log.warn("Invalid JWT : {}, {}", userId, token);
                        expireToken(userId);
                    }
                }
            } catch (ExpiredJwtException exception) {
                log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
                expireTokenByVal(token);
            } catch (UnsupportedJwtException exception) {
                log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            } catch (MalformedJwtException exception) {
                log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            } catch (IllegalArgumentException exception) {
                log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            } catch (Exception exception) {
                log.error(exception.getMessage(), exception);
            }
        }

        return null;
    }

    public static String checkToken(String userId) {
        if (tokenMap.containsKey(userId)) {

            String token = tokenMap.get(userId);

            try {
                // 토큰 만료 여부 확인
                byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

                Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token.replace("Bearer ", ""));

                return token;

            } catch (ExpiredJwtException exception) {
                // 토큰 만료
                log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
                expireToken(userId);
            }
        }

        return null;
    }

    public static void invalidateToken(String userId) {
        tokenMap.remove(userId);
    }

    public static Set<String> getTokenIds() {
        return JwtTokenProvider.tokenMap.keySet();
    }

    private static void expireToken(String userId) {
        tokenMap.remove(userId);
        userConnectService.save(userId, CodeConstants.CONN_LOGOUT_TOKEN, null);
    }

    private static void expireTokenByVal(String token) {
        Optional<String> userId = tokenMap.keySet()
                .stream()
                .filter(key -> tokenMap.get(key).equals(token))
                .findFirst();

        userId.ifPresent(JwtTokenProvider::expireToken);
    }

    private static UsernamePasswordAuthenticationToken getDevAuthentication() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(DEV_TOKEN_ROLE));

        return new UsernamePasswordAuthenticationToken(DEV_TOKEN_USERNAME, null, authorities);
    }

    @Scheduled(fixedDelay = 60 * 1000)
    private void schedule() {
        for (String userId : tokenMap.keySet()) {
            checkToken(userId);
        }
    }

}
