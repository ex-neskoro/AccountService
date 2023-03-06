package account.service;

import account.exception.*;
import account.model.StatusDTO;
import account.model.user.*;
import account.rep.UserRepository;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleFabric userRoleFabric;
    private final PasswordEncoder encoder;
    private final SecurityEventService securityEventService;

    private final List<String> HACKER_DB = new ArrayList<>(Arrays.asList("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch",
            "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));

    @Autowired
    public UserService(UserRepository userRepository, UserRoleFabric userRoleFabric, PasswordEncoder passwordEncoder, SecurityEventService securityEventService) {
        this.userRepository = userRepository;
        this.userRoleFabric = userRoleFabric;
        this.encoder = passwordEncoder;
        this.securityEventService = securityEventService;
    }

    public User save(User toSave) {
        return userRepository.save(toSave);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase())
                .orElseThrow(UserNotFoundException::new);
    }

    public boolean isUserExist(String email) {
        return userRepository.findUserByEmail(email.toLowerCase()).isPresent();
    }

    public List<User> findAll() {
        return (ArrayList<User>) userRepository.findAll();
    }

    public long usersCount() {
        return userRepository.count();
    }

    public List<User> findAllOrderById() {
        return userRepository.findAllOrderByIdAsc();
    }

    public boolean isUserPassWeak(String password) {
        return HACKER_DB.contains(password);
    }

    private boolean userHasRoleGroup(User user, UserRole userRole) {
        return user.getRoles().stream()
                .allMatch(role -> role.getGroup().equals(userRole.getGroup()));
    }

    public User operateOnRole(ChangeUserRoleDTO dto) {
        UserRoleOperation userRoleOperation = dto.getOperation();
        UserRole userRole = userRoleFabric.instanceOf(dto.getUserRoleName());
        User user = findUserByEmail(dto.getUserEmail());
        return switch (userRoleOperation) {
            case GRANT -> grantRole(user, userRole);
            case REMOVE -> removeRole(user, userRole);
        };
    }

    private User grantRole(User user, UserRole userRole) {
        if (!userHasRoleGroup(user, userRole)) {
            throw new WrongUserRoleGroupException();
        }
        user.addRole(userRole);
        User updatedUser = save(user);
        securityEventService.addGrantRoleEvent(updatedUser, userRole);
        return updatedUser;
    }

    private User removeRole(User user, UserRole userRole) {
        Set<UserRole> userRoles = user.getRoles();
        if (!userRoles.contains(userRole)) {
            throw new WrongUserRoleException();
        }
        if (userRoles.size() == 1) {
            throw new LastUserRoleException();
        }
        if (userRole.equals(userRoleFabric.instanceOf("ADMINISTRATOR"))) {
            throw new AdministratorUserRoleDeletionException();
        }
        user.removeRole(userRole);
        User updatedUser = save(user);
        securityEventService.addRemoveRoleEvent(updatedUser, userRole);
        return updatedUser;
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
        securityEventService.addDeleteUserEvent(user);
    }

    public StatusDTO changeUserAccess(ChangeUserAccessDTO userOperation) {
        User user = findUserByEmail(userOperation.getUserEmail());
        switch (userOperation.getOperation()) {
            case LOCK -> lockUser(user);
            case UNLOCK -> unlockUser(user);
        }
        return new StatusDTO("User %s %s".formatted(user.getEmail(), userOperation.getOperation().name().toLowerCase()) + "ed!");
    }

    public void lockUser(User user) {
        if (!user.getRoles().contains(userRoleFabric.instanceOf("ADMINISTRATOR"))) {
            user.setAccountNonLocked(false);
            user = userRepository.save(user);
            securityEventService.addLockUserEvent(user);
        } else {
            throw new AdministratorUserBlockException();
        }
    }

    public void userSelfLock(User user) {
        user.setAccountNonLocked(false);
        user = userRepository.save(user);
        securityEventService.addSelfLockUserEvent(user);
    }

    public void unlockUser(User user) {
        user.setAccountNonLocked(true);
        user = userRepository.save(user);
        securityEventService.addUnlockUserEvent(user);
    }

    public User registerUser(User user) {
        user.setEmail(user.getEmail().toLowerCase());

        if (isUserExist(user.getEmail())) {
            throw new UserExistException();
        }

        if (user.getPassword().length() < 12) {
            throw new LengthPasswordException();
        }

        if (isUserPassWeak(user.getPassword())) {
            throw new WeakPasswordException();
        }

        if (usersCount() == 0) {
            user.addRole(userRoleFabric.instanceOf("ADMINISTRATOR"));
        } else {
            user.addRole(userRoleFabric.instanceOf("USER"));
        }

        user.setPassword(encoder.encode(user.getPassword()));

        user = save(user);
        securityEventService.addCreateUserEvent(user);

        return user;
    }

    public void changeUserPass(String newPassword, User user) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map;
        try {
            map = mapper.readValue(newPassword, Map.class);
        } catch (JacksonException e) {
            throw new PasswordFormatException();
        }

        String newPass = map.get("new_password");

        if (encoder.matches(newPass, user.getPassword())) {
            throw new SamePasswordException();
        }

        if (newPass.length() < 12) {
            throw new LengthPasswordException();
        }

        if (isUserPassWeak(newPass)) {
            throw new WeakPasswordException();
        }

        user.setPassword(encoder.encode(newPass));
        User updatedUser = save(user);
        securityEventService.addChangePasswordEvent(updatedUser);
    }
}
