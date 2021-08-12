package com.mesim.sc.api;

import com.mesim.sc.constants.CodeConstants;
import com.mesim.sc.service.admin.system.UserAccessService;
import com.mesim.sc.util.HttpUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
public class ApiAspect {

    @Autowired
    private UserAccessService userAccessService;

    @AfterReturning(
            value="!@annotation(com.mesim.sc.annotation.Annotation.NoAspect) && " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.getList(..)) || " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.get(..)) || " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.add(..)) || " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.modify(..)) || " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.delete(..))",
            returning="response"
    )
    public void onAfterReturningHandler(JoinPoint joinPoint, ApiResponseDto response) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String userId = request.getRemoteUser();
        String typeCd = methodToType(joinPoint.getSignature().getName());
        String apiPath = request.getServletPath();
        String ip = HttpUtil.getIP(request);

        if (response.isSuccess()) {
            userAccessService.save(userId, typeCd, apiPath, ip);
        } else {
            userAccessService.save(userId, typeCd, apiPath, ip, CodeConstants.ACCESS_ERR_FAIL);
        }
    }

    @AfterThrowing(
            value="!@annotation(com.mesim.sc.annotation.Annotation.NoAspect) && " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.getList(..)) || " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.get(..)) || " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.add(..)) || " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.modify(..)) || " +
                    "execution(com.mesim.sc.api.ApiResponseDto com.mesim.sc.api.rest..*.delete(..))",
            throwing="exception"
    )
    public void onAfterThHandler(JoinPoint joinPoint, Throwable exception) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String userId = request.getRemoteUser();
        String typeCd = methodToType(joinPoint.getSignature().getName());
        String apiPath = request.getServletPath();
        String ip = HttpUtil.getIP(request);

        userAccessService.save(userId, typeCd, apiPath, ip, (Exception) exception);
    }

    private String methodToType(String methodNm) {
        switch (methodNm) {
            case "getList":
                return CodeConstants.ACCESS_LIST;
            case "get":
                return CodeConstants.ACCESS_GET;
            case "add":
                return CodeConstants.ACCESS_ADD;
            case "modify":
                return CodeConstants.ACCESS_MODIFY;
            case "delete":
                return CodeConstants.ACCESS_DELETE;
            default:
                return CodeConstants.ACCESS_ETC;
        }
    }
}
