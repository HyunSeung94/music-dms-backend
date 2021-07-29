package com.mesim.sc.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class HttpUtil {

  public static String getIP(HttpServletRequest request) {
      String ip = request.getHeader("X-FORWARDED-FOR");

      if (ip == null) {
          ip = request.getRemoteAddr();
      }

      if (ip.equals("0:0:0:0:0:0:0:1")) {
          ip = "127.0.0.1";
      }

      return ip;
  }
}
