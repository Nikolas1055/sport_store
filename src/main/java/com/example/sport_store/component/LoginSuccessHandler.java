package com.example.sport_store.component;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    /**
     * Пользовательская логика успешного входа.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        String redirectUrl = request.getContextPath();
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            redirectUrl = "admin";
        } else if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            redirectUrl = "manager";
        } else if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CASHIER"))) {
            redirectUrl = "cashier";
        } else if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            redirectUrl = "index";
        }
        response.sendRedirect(redirectUrl);
    }
}
