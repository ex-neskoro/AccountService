package account.api;

import account.LocalPaths;
import account.exception.*;
import account.model.StatusDTO;
import account.model.event.SecurityEvent;
import account.model.user.*;
import account.service.LoginAttemptService;
import account.service.SecurityEventService;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class APIController {
    private final UserService userService;
    private final SecurityEventService securityEventService;
    private final LoginAttemptService loginAttemptService;
    private final UserRoleFabric userRoleFabric;

    @Autowired
    public APIController(UserService userService, SecurityEventService securityEventService, LoginAttemptService loginAttemptService, UserRoleFabric userRoleFabric) {
        this.userService = userService;
        this.securityEventService = securityEventService;
        this.loginAttemptService = loginAttemptService;
        this.userRoleFabric = userRoleFabric;
    }

    @PostMapping("api/auth/signup")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody User user) {
        user = userService.registerUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("api/admin/user")
    public List<User> getAllUsers() {
        return userService.findAllOrderById();
    }

    @PutMapping("api/admin/user/role")
    public User changeUserRole(@RequestBody ChangeUserRoleDTO dto) {
        return userService.operateOnRole(dto);
    }

    @DeleteMapping(value = {"api/admin/user/{userEmail}"})
    public ResponseEntity<?> deleteUser(@PathVariable String userEmail) {
        User user = userService.findUserByEmail(userEmail);

        if (user.getRoles().contains(userRoleFabric.instanceOf("ADMINISTRATOR"))) {
            throw new AdministratorUserRoleDeletionException();
        }

        userService.deleteUser(user);
        return new ResponseEntity<>(Map.of("user", userEmail,
                "status", "Deleted successfully!"), HttpStatus.OK);
    }

    @PostMapping("api/auth/changepass")
    public ResponseEntity<?> changeUserPass(@RequestBody String newPassword, @AuthenticationPrincipal UserDetails details) {
        userService.changeUserPass(newPassword, userService.findUserByEmail(details.getUsername()));

        return new ResponseEntity<>(Map.of("email", details.getUsername(),
                "status", "The password has been updated successfully"), HttpStatus.OK);
    }

    @PutMapping("api/admin/user/access")
    public StatusDTO changeUserAccess(@RequestBody ChangeUserAccessDTO accessDTO) {
        if (accessDTO.getOperation() == UserOperation.UNLOCK) {
            loginAttemptService.clearFailedLogins(accessDTO.getUserEmail().toLowerCase());
        }
        return userService.changeUserAccess(accessDTO);
    }

    @GetMapping("api/security/events")
    public List<SecurityEvent> getAllSecurityEvents() {
        return securityEventService.getAllEvents();
    }

    @DeleteMapping("api/security/events")
    public StatusDTO deleteAllEvents() {
        securityEventService.deleteAll();
        return new StatusDTO("All events was cleared)");
    }

    @GetMapping(value = "api/doc", produces = "text/html")
    public ResponseEntity<String> getOpenApiDoc() {
        Path htmlFilePath = Path.of(LocalPaths.OPEN_API_DOC);
        String htmlContent;
        try {
            htmlContent = Files.readString(htmlFilePath);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(htmlContent, HttpStatus.OK);
    }
}
