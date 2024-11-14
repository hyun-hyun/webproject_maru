package com.example.webproject_maru.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HttpSessionRequestCache requestCache=new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName("continue");//continue로 사용하는게 스프링시큐리티 5에서 추가됨. 캐시효율성관련

        http
                .authorizeHttpRequests((auth) -> auth
                        //.requestMatchers("/", "/**").permitAll()

                        .requestMatchers("/","/error", "/login", "/loginProc","/join","/joinProc","/joined","/articles/anime/**","/api/comments/article/*").permitAll()
                        //h2용
                        //.requestMatchers("/h2-console/**").permitAll() 

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/write/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/user/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        //.requestMatchers("/user/**", "/articles/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/images/**","/css/**","/favicon.ico").permitAll()//이미지파일들 security filter 예외
                        .requestMatchers("/api/articles/*/create_r","/api/reviews/**", "/api/comments/**", "/api/mypage/**").authenticated() // 특정 경로에 대해 인증 요구
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .requestCache(cache -> cache.requestCache(requestCache)) // 요청 캐시 설정(저 ?continue=가야할경로 그거)

                .formLogin((auth) -> auth.loginPage("/login") // form방식 로그인 사용, 커스텀 로그인 페이지 지정
                        .loginProcessingUrl("/loginProc") // submit 받을 url
                        //.defaultSuccessUrl("/", true) // 로그인 성공 시 이동할 경로
                        .successHandler((request, response, authentication) -> {
                                // continue 파라미터 가져오기
                                String continueUrl = request.getParameter("continue");
                                if (continueUrl != null && !continueUrl.isEmpty()) {
                                    response.sendRedirect(continueUrl); // continue 파라미터가 있으면 해당 URL로 리다이렉트
                                } else {
                                    response.sendRedirect("/"); // 없으면 기본 페이지로
                                }
                            })
                        .failureUrl("/login?error=true") // 로그인 실패 시 이동할 경로
                      //  .usernameParameter("userEmail") // 아이디 파라미터 설정
                     //   .passwordParameter("password") // 패스워드 파라미터 설정
                        .permitAll() //로그인없이 접속 가능
                )

                .logout((logout) -> logout.logoutUrl("/logout")//form action url
                        //.logoutSuccessUrl("/")
                        .addLogoutHandler((request, response, authentication) -> { 
                                // 사실 굳이 내가 세션 무효화하지 않아도 됨. 
                                // LogoutFilter가 내부적으로 해줌.
                                HttpSession session = request.getSession();
                                if (session != null) {
                                    session.invalidate();
                                }
                            })  // 로그아웃 핸들러 추가
                            .logoutSuccessHandler((request, response, authentication) -> {
                                response.sendRedirect("/");
                            }) // 로그아웃 성공 핸들러
                            .deleteCookies("remember-me") // 로그아웃 후 삭제할 쿠키 지정
                )
                

                .csrf((auth) -> auth.disable())
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));//h2콘솔접속용

                

        return http.build();
        /*
        http.csrf().disable().cors().disable()
                .authorizeHttpRequests(request -> request
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .anyRequest().authenticated() 
                )
                .formLogin(login -> login //
                        .loginPage("/view/login")	
                        .loginProcessingUrl("/login-process")	
                        .usernameParameter("userid")	// [C] submit할 아이디
                        .passwordParameter("pw")	// [D] submit할 비밀번호
                        .defaultSuccessUrl("/view/dashboard", true)
                        .permitAll() 
                )
                .logout(withDefaults()); //로그아웃은 기본설정으로(/logout으로 인증해제)

        return http.build();
        */

    }
}
