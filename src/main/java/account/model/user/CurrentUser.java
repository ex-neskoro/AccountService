package account.model.user;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public UserDetailsImpl getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            return new UserDetailsImpl(auth);
        } else {
            return (UserDetailsImpl) auth.getPrincipal();
        }
    }
}
