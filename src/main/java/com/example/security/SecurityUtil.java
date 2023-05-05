package com.example.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

//EMAIL 조회하는 CLASS
public class SecurityUtil {
  public static String getCurrentEmail() {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || authentication.getName() == null) {
          throw new RuntimeException("No authentication information.");
      }
      return authentication.getName();
  }
}
