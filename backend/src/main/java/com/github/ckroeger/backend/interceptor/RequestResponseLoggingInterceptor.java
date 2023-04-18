package com.github.ckroeger.backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

@Component
public class RequestResponseLoggingInterceptor implements HandlerInterceptor {

   private final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);

   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
      logger.info("[preHandle][{}}] [{}] {} {}", request, request.getMethod(),
            request.getRequestURI(), getParameters(request));
      return true;
   }

   @Override
   public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                          ModelAndView modelAndView) {
      logger.info("[postHandle][{}]", request);
   }

   @Override
   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                               Exception ex) {
      logger.info("[afterCompletion][{}][exception: {}}]", request, ex);
   }

   private String getParameters(HttpServletRequest request) {
      StringBuilder posted = new StringBuilder();
      Enumeration<?> e = request.getParameterNames();
      if (e != null) {
         posted.append("?");
         while (e.hasMoreElements()) {
            if (posted.length() > 1) {
               posted.append("&");
            }
            String curr = (String) e.nextElement();
            posted.append(curr).append("=");
            if (curr.contains("password")
                  || curr.contains("answer")
                  || curr.contains("pwd")) {
               posted.append("*****");
            } else {
               posted.append(request.getParameter(curr));
            }
         }
      }
      String ip = request.getHeader("X-FORWARDED-FOR");
      String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
      if (!isEmpty(ipAddr)) {
         posted.append("&_psip=" + ipAddr);
      }
      return posted.toString();
   }

   private boolean isEmpty(String str) {
      return str == null || "".equals(str) || "".equals(str.trim());
   }

   private String getRemoteAddr(HttpServletRequest request) {
      String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
      if (ipFromHeader != null && ipFromHeader.length() > 0) {
         return ipFromHeader;
      }
      return request.getRemoteAddr();
   }
}