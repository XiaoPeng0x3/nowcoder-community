package com.zxp.nowcodercommunity.config;

import com.zxp.nowcodercommunity.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.zxp.nowcodercommunity.constant.LoginConstant.*;
import static org.springframework.security.config.Customizer.withDefaults;

/**
 *  这个类是SpringSecurity的配置类
 *  在接受到来自前端的登录请求之后，首先会将信息封装在UsernamePasswordAuthenticationToken里面
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationTokenFilter;
    private final UserDetailsService userDetailsService;
    private final LogoutSuccessHandler logoutSuccessHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService, LogoutSuccessHandler logoutSuccessHandler) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;

        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    // 配置AuthenticationManger
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 配置SerurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 设置权限
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow",
                        "/share/**") // 将这些路径进行保护和限权
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR) // 允许部分用户访问
                // 继续将访问路径分隔开来
                .requestMatchers(
                        "/discuss/top",
                        "/visit/**",
                        "/actuator/**") // 对这些路径进行保护
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                ) // 只有管理员拥有这些权限
                .requestMatchers(
                        "/discuss/wonderful",
                        "/discuss/delete"
                ) // 删帖、加精
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                ) // 只有帖子的版主和管理员拥有这些权限
                        .requestMatchers("/setting").authenticated()
                .anyRequest().permitAll() // 剩下的接口所有用户(包括未登录的)也可以访问
        )
                .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF 保护，使用JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态（JWT）
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // 允许 iframe
        // 将jwt的过滤器注入过滤器链
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.cors(withDefaults());
        http.userDetailsService(userDetailsService);
        // 退出过滤器
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(logoutSuccessHandler));
        return http.build();
    }

    /**
     * 密码配置
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 侦听器配置，使 Spring Security 更新有关会话生命周期事件的信息
     * 并发会话控制：<a href="https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html">...</a>
     * @return {@link HttpSessionEventPublisher}
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    /**
     * 配置跨源访问(CORS)
     * 官方文档：<a href="https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html">...</a>
     * @return {@link CorsConfigurationSource}
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080/","http://localhost:5173/"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
