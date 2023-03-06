package account.service;

import account.exception.UserLockedException;
import account.model.user.UserDetailsImpl;
import account.rep.UserRepository;
import account.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        User user = userRepository.findUserByEmail(name.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Not found:" + name));

        return new UserDetailsImpl(user);
    }
}
