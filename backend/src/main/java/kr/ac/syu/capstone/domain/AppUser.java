package kr.ac.syu.capstone.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;

/** Minimal USERS integration mapping; registration and school authentication are outside this module. */
@Entity @Table(name = "users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String displayName;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR) @Column(nullable = false, length = 20) private Role role;
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    protected AppUser() {}
    public AppUser(String displayName, Role role, Instant now) {
        this.displayName = displayName; this.role = role; this.createdAt = now; this.updatedAt = now;
    }
    public Long getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Role getRole() { return role; }
}
