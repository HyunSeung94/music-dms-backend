package com.mesim.sc.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PwEncoder {

    private static ApplicationContext context;

    private static PasswordEncoder encoder;

    @Autowired
    public PwEncoder(ApplicationContext context) {
        PwEncoder.context = context;
    }

    @PostConstruct
    public void init() {
        PwEncoder.encoder = PwEncoder.context.getBean(PasswordEncoder.class);
    }

    public static String encode(String str) {
        return PwEncoder.encoder.encode(str);
    }

    public static boolean match(String str1, String str2) {
        return PwEncoder.encoder.matches(str1, str2);
    }

    public static boolean notMatch(String str1, String str2) {
        return !PwEncoder.encoder.matches(str1, str2);
    }

}
