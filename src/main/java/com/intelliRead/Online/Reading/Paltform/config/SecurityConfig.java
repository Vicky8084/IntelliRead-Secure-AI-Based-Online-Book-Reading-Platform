package com.intelliRead.Online.Reading.Paltform.config;

import com.intelliRead.Online.Reading.Paltform.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil){
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ CORS FIRST
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // ✅ SABHI FRONTEND PAGES KO PUBLIC KARO
                        .requestMatchers(
                                "/",
                                "/Home",
                                "/Home.html",
                                "/login",
                                "/Login",
                                "/Login.html",
                                "/signup",
                                "/SignUp",
                                "/SignUp.html",
                                "/forgotpassword",
                                "/ForgotPass",
                                "/ForgotPass.html",
                                "/admin",
                                "/Admin",
                                "/Admin.html",
                                "/books",
                                "/bookscreen",
                                "/publisher-dashboard",
                                "/admin-dashboard"
                        ).permitAll()

                        // ✅ STATIC RESOURCES - PUBLIC ACCESS
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // ✅ SABHI AUTH ENDPOINTS PUBLIC KARO
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/password/**").permitAll()
                        .requestMatchers("/admin/approve/**").permitAll()
                        .requestMatchers("/admin/reject/**").permitAll()

                        // ✅ NEW: READER ACCESS PUBLIC
                        .requestMatchers("/reader/**").permitAll()

                        // ✅ PUBLIC BOOK & CATEGORY ENDPOINTS
                        .requestMatchers("/book/apies/findAll").permitAll()
                        .requestMatchers("/book/apies/findById/**").permitAll()
                        .requestMatchers("/book/apies/category/**").permitAll()
                        .requestMatchers("/category/apies/findAll").permitAll()
                        .requestMatchers("/category/apies/main").permitAll()
                        .requestMatchers("/category/apies/popular").permitAll()

                        // ✅ TEST ENDPOINTS PUBLIC
                        .requestMatchers("/auth/test-connection").permitAll()
                        .requestMatchers("/auth/create-test-user").permitAll()

                        // ✅ ADMIN ENDPOINTS - ROLE BASED ACCESS
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/apies/delete/**").hasRole("ADMIN")
                        .requestMatchers("/user/apies/getAll").hasRole("ADMIN")
                        .requestMatchers("/book/apies/upload").hasRole("ADMIN")
                        .requestMatchers("/book/apies/save").hasRole("ADMIN")
                        .requestMatchers("/book/apies/Update/**").hasRole("ADMIN")
                        .requestMatchers("/book/apies/delete/**").hasRole("ADMIN")

                        // ✅ USER ENDPOINTS (Authenticated users)
                        .requestMatchers("/book/apies/user/**").authenticated()
                        .requestMatchers("/review/apies/**").authenticated()
                        .requestMatchers("/suggestion/apis/**").authenticated()
                        .requestMatchers("/user/apies/Update/**").authenticated()

                        // ✅ DEFAULT - AUTHENTICATED ACCESS
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ✅ ALLOW ALL FRONTEND ORIGINS
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ✅ ADD THIS METHOD FOR GLOBAL CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}