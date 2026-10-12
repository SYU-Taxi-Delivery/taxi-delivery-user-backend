package kr.ac.syu.capstone.demo;

import kr.ac.syu.capstone.domain.*;
import kr.ac.syu.capstone.domain.Role;
import kr.ac.syu.capstone.repository.*;
import kr.ac.syu.capstone.security.*;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Configuration @Profile("demo")
public class DemoConfiguration {
    @Bean UserDetailsService demoUsers(AppUserRepository users, PostRepository posts, Clock clock) {
        Instant now = clock.instant();
        Map<String, CampusUser> accounts = new HashMap<>();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        AppUser author = null;
        for (String name : List.of("author", "student1", "student2", "student3", "admin")) {
            Role role = name.equals("admin") ? Role.ADMIN : Role.STUDENT;
            AppUser user = users.save(new AppUser(name, role, now));
            accounts.put(name, new CampusUser(user.getId(), name, "{bcrypt}" + encoder.encode("demo-pass"), role));
            if (name.equals("author")) author = user;
        }
        posts.save(Post.taxi(author, 4, now.plus(Duration.ofHours(2)).truncatedTo(ChronoUnit.MINUTES), now));
        posts.save(Post.delivery(author, 10, now.plus(Duration.ofHours(3)).truncatedTo(ChronoUnit.MINUTES), now));
        return username -> {
            CampusUser user = accounts.get(username);
            if (user == null) throw new UsernameNotFoundException("Unknown demo account");
            return user;
        };
    }
}
