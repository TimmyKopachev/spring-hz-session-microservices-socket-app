package org.hz.session.integration.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(registry -> {
                    registry.anyRequest().authenticated();
                })
                .sessionManagement(sessionManagerConfigurer -> {
                    sessionManagerConfigurer
                            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
                })
//                .securityContext((securityContext) -> securityContext
//                        .securityContextRepository(new RequestAttributeSecurityContextRepository())
//                        .requireExplicitSave(true)
//                )
                .authenticationProvider(authenticationProvider)
                .formLogin(withDefaults());

        return http.build();
    }

}
