package kr.ac.syu.capstone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import kr.ac.syu.capstone.api.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import java.time.Clock;
import java.io.IOException;
import java.util.Map;

@Configuration
public class SecurityConfiguration {
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, Environment env, ObjectMapper mapper, Clock clock) throws Exception {
        http.authorizeHttpRequests(a -> a.requestMatchers("/error").permitAll().anyRequest().authenticated());
        // Keep CSRF protection for browser/session integration; clients fetch /api/csrf before mutations.
        http.csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        http.exceptionHandling(e -> e
            .authenticationEntryPoint((req,res,ex) -> write(mapper, clock, ErrorCode.AUTHENTICATION_REQUIRED, req, res))
            .accessDeniedHandler((req,res,ex) -> write(mapper, clock, ex instanceof CsrfException ? ErrorCode.CSRF_INVALID : ErrorCode.FORBIDDEN_OPERATION, req, res)));
        http.requestCache(c -> c.disable());
        if (env.acceptsProfiles(Profiles.of("demo"))) http.httpBasic(Customizer.withDefaults());
        // Without demo, only an authenticated CampusPrincipal installed by a trusted auth adapter is accepted.
        return http.build();
    }
    private static void write(ObjectMapper mapper, Clock clock, ErrorCode code, HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setStatus(code.status().value()); res.setContentType("application/json"); res.setCharacterEncoding("UTF-8");
        mapper.writeValue(res.getOutputStream(), ApiResponses.failure(code, clock.instant(), Map.of(), req));
    }
}
