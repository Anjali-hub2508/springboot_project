package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/login.html").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/books/2").permitAll()
                        .anyRequest().authenticated()
                )

                // 👇 GOOGLE OAUTH2 LOGIN
                .oauth2Login(oauth -> oauth
                        .loginPage("/login.html")   // your custom login page
                        .defaultSuccessUrl("/", true) // redirect after success
                )
                // disable csrf for simplicity (important for beginners)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

}
