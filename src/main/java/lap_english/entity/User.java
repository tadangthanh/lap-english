package lap_english.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends BaseEntity implements UserDetails {
    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "password",nullable = true)
    private String password;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    private String avatar;
    private String name;
    @Column(name = "json", columnDefinition = "TEXT")
    private String json;
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;
    @OneToOne
    @JoinColumn(name = "accumulate_id")
    private Accumulate accumulate;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    private CumulativePoint cumulativePoint;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        return true;
    }
}
