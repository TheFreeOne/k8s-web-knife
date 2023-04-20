package org.freeone.k8s.web.knife.controller;

import org.freeone.k8s.web.knife.utils.ResultKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController {

    @Value("${spring.security.user.name}")
    private String user;

    @Value("${spring.security.user.password}")
    private String password;

    @Autowired
    AuthenticationManager authenticationManager;

    @RequestMapping("/login.htm")
    public ModelAndView go(String user, String password, ModelAndView mv) {
        mv.setViewName("/login");
        return mv;

    }
//
//    @RequestMapping("/login")
//    public ResultKit login(HttpServletRequest request, String username, String password) {
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
//        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        // 执行登录认证过程
//        Authentication authentication = authenticationManager.authenticate(token);
//        // 认证成功存储认证信息到上下文
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        // 生成令牌并返回给客户端
//        return ResultKit.ok();
//    }


}
