package taras.artcake.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:password}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).authenticated() // Krever innlogging for admin
                .anyRequest().permitAll() // Alt annet er åpent
            )
            .formLogin((form) -> form
                .defaultSuccessUrl("/admin", true) // Gå til admin etter login
                .permitAll()
            )
            .logout((logout) -> logout.logoutSuccessUrl("/"))
            .csrf(csrf -> csrf.disable()); // Slår av CSRF for enkelhetens skyld i denne prototypen

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Her setter du brukernavn og passord for konditoren
        // I en ekte app bør passordet være kryptert og ikke hardkodet her
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username(adminUsername)
            .password(adminPassword)
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
