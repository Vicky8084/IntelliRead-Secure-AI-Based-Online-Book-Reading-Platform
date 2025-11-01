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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                // 🔒 Step 1: Disable CSRF (since we are using JWT, not cookies)
                .csrf(csrf -> csrf.disable())

                // 🌐 Step 2: Enable CORS with configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ⚡ Step 3: Set session management to stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🚪 Step 4: Define authorization rules
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no authentication required
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/password/**").permitAll()
                        .requestMatchers("/user/apies/save").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/admin/approve/**").permitAll()
                        .requestMatchers("/admin/reject/**").permitAll()

                        // Admin only endpoints
                        .requestMatchers("/user/apies/delete/**").hasRole("ADMIN")
                        .requestMatchers("/user/apies/getAll").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Authenticated users (both USER and ADMIN)
                        .requestMatchers("/user/apies/get/**").authenticated()
                        .requestMatchers("/user/apies/Update/**").authenticated()
                        .requestMatchers("/book/apies/**").authenticated()
                        .requestMatchers("/category/apies/**").authenticated()
                        .requestMatchers("/review/apies/**").authenticated()
                        .requestMatchers("/suggestion/apis/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // 🔐 Step 5: Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}