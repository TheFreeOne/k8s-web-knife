package org.freeone.k8s.web.knife.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 * spring security的配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 当使用postman请求时，会由于存在缓存，比如header 中修改了参数，但是仍第一次请求的值，比如下方的【authenticationEntryPoint】的request的请求头就是如此
        http.requestCache().disable();

        http.formLogin()
                // 登陆页面设置,如果加了authenticationEntryPoint就可以屏蔽下面这句话了
                .loginPage("/login.htm")
                // 登陆访问路径
                .loginProcessingUrl("/login")
                .successForwardUrl("/")
                .defaultSuccessUrl("/")
                .failureForwardUrl("/login.htm")
                .and()
                // 设置退出动作URL路径，如果CSRF设置enabled，则只接受POST请求，否则所有请求方式都接受
                .logout()
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/logout", "GET"),
                        new AntPathRequestMatcher("/logout", "POST"),
                        new AntPathRequestMatcher("/logout", "PUT"),
                        new AntPathRequestMatcher("/logout", "DELETE")
                ))
                .logoutSuccessUrl("/login.htm")
                .deleteCookies("S_AUTH", "S_IFS")
                // 清除当前用户认证信息
                .clearAuthentication(true)
                // 使当前用户SESSION失效
                .invalidateHttpSession(true);
        // 请求转发

        http.csrf().disable().cors().and().headers().frameOptions().disable();

        // 设置路径不需要验证
        http.authorizeRequests()
                .antMatchers("/wsexec/**","/pod/newlogs/**","/download/**","/lib/**", "/css/**", "/js/**", "/api/**", "/page/**", "/images/**", "/login", "/favicon.ico", "/login.htm", "/login/**").permitAll()
                // 除此之外都必须验证
                .anyRequest().authenticated();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
