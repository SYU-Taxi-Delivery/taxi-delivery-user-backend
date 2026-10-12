package kr.ac.syu.capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import java.time.Clock;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ParticipationApplication {
    public static void main(String[] args) { SpringApplication.run(ParticipationApplication.class, args); }
    @Bean Clock applicationClock() { return Clock.systemUTC(); }
}
