package account.service;

import account.model.event.SecurityEvent;
import account.model.event.SecurityEventType;
import account.model.user.CurrentUser;
import account.model.user.User;
import account.model.user.UserRole;
import account.rep.SecurityEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class SecurityEventService {
    private SecurityEventRepository eventRepository;
    private CurrentUser currentUser;
    private static final String ANON = "Anonymous";

    public List<SecurityEvent> getAllEvents() {
        return eventRepository.findAllByOrderByIdAsc();
    }

    private String getCurrentPath() {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().getPath();
    }

    public void addCreateUserEvent(User user) {
        String subject = currentUser.getCurrentUser().getUsername();
        SecurityEvent event = new SecurityEvent(SecurityEventType.CREATE_USER,
                subject.equals("anonymousUser") ? ANON : subject,
                user.getEmail(),
                getCurrentPath());
        eventRepository.save(event);
    }

    public void addChangePasswordEvent(User user) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.CHANGE_PASSWORD,
                user.getEmail(),
                currentUser.getCurrentUser().getUsername(),
                getCurrentPath());
        eventRepository.save(event);
    }

    public void addAccessDeniedEvent(HttpServletRequest request) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.ACCESS_DENIED,
                request.getUserPrincipal().getName(),
                getCurrentPath(),
                getCurrentPath());
        eventRepository.save(event);
    }

    public void addLoginFailedEvent(String subjectUser) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.LOGIN_FAILED,
                subjectUser,
                getCurrentPath(),
                getCurrentPath());
        eventRepository.save(event);
    }

    public void addGrantRoleEvent(User objectUser, UserRole role) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.GRANT_ROLE,
                currentUser.getCurrentUser().getUsername(),
                "Grant role %s to %s".formatted(role.getName().substring(5), objectUser.getEmail()),
                getCurrentPath());
        eventRepository.save(event);
    }

    public void addRemoveRoleEvent(User objectUser, UserRole role) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.REMOVE_ROLE,
                currentUser.getCurrentUser().getUsername(),
                "Remove role %s from %s".formatted(role.getName().substring(5), objectUser.getEmail()),
                getCurrentPath());
        eventRepository.save(event);
    }

    public void addLockUserEvent(User objectUser) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.LOCK_USER,
                currentUser.getCurrentUser().getUsername(),
                "Lock user " + objectUser.getEmail(),
                getCurrentPath());

        eventRepository.save(event);
    }

    public void addSelfLockUserEvent(User objectUser) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.LOCK_USER,
                objectUser.getEmail(),
                "Lock user " + objectUser.getEmail(),
                getCurrentPath());

        eventRepository.save(event);
    }


    public void addUnlockUserEvent(User objectUser) {
        String eventObject;
        if (getCurrentPath().equals("/api/admin/user/access")) {
            eventObject = objectUser.getEmail();
        } else {
            eventObject = currentUser.getCurrentUser().getUsername();
        }

        SecurityEvent event = new SecurityEvent(SecurityEventType.UNLOCK_USER,
                currentUser.getCurrentUser().getUsername(),
                "Unlock user " + eventObject,
                getCurrentPath());

        eventRepository.save(event);
    }

    public void addDeleteUserEvent(User user) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.DELETE_USER,
                currentUser.getCurrentUser().getUsername(),
                user.getEmail(),
                getCurrentPath());
        eventRepository.save(event);
    }

    public void addBruteForceEvent(User user) {
        SecurityEvent event = new SecurityEvent(SecurityEventType.BRUTE_FORCE,
                user.getEmail(),
                getCurrentPath(),
                getCurrentPath());
        eventRepository.save(event);
    }

    public void deleteAll() {
        eventRepository.deleteAll();
    }
}
