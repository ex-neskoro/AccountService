package account.service;

import account.model.user.User;
import account.model.user.UserRoleFabric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPT = 5;
    private Map<String, Integer> attemptsCache = new HashMap<>();
    private HttpServletRequest request;
    private UserService userService;
    private SecurityEventService securityEventService;

    private UserRoleFabric userRoleFabric;

    @Autowired
    public LoginAttemptService(HttpServletRequest request, UserService userService, SecurityEventService securityEventService, UserRoleFabric userRoleFabric) {
        this.request = request;
        this.userService = userService;
        this.securityEventService = securityEventService;
        this.userRoleFabric = userRoleFabric;
    }

    public void addFailedLogin() {
        String key = getUserEmail();
//        User user = userService.findUserByEmail(key);
        int attempts = Objects.requireNonNullElse(attemptsCache.get(key), 0);
        attempts++;
        attemptsCache.put(key, attempts);
        securityEventService.addLoginFailedEvent(key);
        if (isFailedAttemptsMoreThanMax()) {
            blockUser();
        }
    }

    public void clearFailedLogins(String key) {
        attemptsCache.put(key, 0);
    }

    public void clearCurrentUserFailedLogins() {
        clearFailedLogins(getUserEmail());
    }

    public boolean isFailedAttemptsMoreThanMax() {
        return attemptsCache.get(getUserEmail()) >= MAX_ATTEMPT;
    }

    public boolean isFailedAttemptsMoreThanMax(String email) {
        return attemptsCache.get(email) >= MAX_ATTEMPT;
    }

    public void blockUser() {
        User currentUser = userService.findUserByEmail(getUserEmail());
        if (!currentUser.getRoles().contains(userRoleFabric.instanceOf("ADMINISTRATOR"))) {
            securityEventService.addBruteForceEvent(currentUser);
            userService.userSelfLock(currentUser);
        }
    }

    private String getUserEmail() {
        String email = new String(Base64.getDecoder().decode(request.getHeader("Authorization").split(" ")[1])).split(":")[0];
        return email;
    }
}
