package kr.ac.syu.capstone.security;
import kr.ac.syu.capstone.api.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.time.Clock;
@Component
public class CurrentUser {
    private final Clock clock;
    public CurrentUser(Clock clock) { this.clock = clock; }
    public long id(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CampusPrincipal p) || p.userId() <= 0)
            throw new ApiException(ErrorCode.AUTHENTICATION_REQUIRED, clock.instant());
        return p.userId();
    }
}
