package account.model.user;

import account.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final User user;
    private final Set<? extends GrantedAuthority> rolesAndAuthorities;
    boolean isAccountNonLocked;

    public UserDetailsImpl(User user) {
        username = user.getEmail();
        password = user.getPassword();
        rolesAndAuthorities = user.getRoles();
        this.user = user;
        this.isAccountNonLocked = user.isAccountNonLocked();
    }

    public UserDetailsImpl(Authentication authentication) {
        username = authentication.getPrincipal().toString();
        password = "";
        user = new User();
        rolesAndAuthorities = Set.of();
    }

    public User getUserEntity() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
