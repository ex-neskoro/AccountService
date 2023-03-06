package account.model.user;

import account.exception.RoleNotFoundException;
import account.rep.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class UserRoleFabric {
    private List<UserRole> roles;

    @Autowired
    public UserRoleFabric(UserRoleRepository userRoleRepository) {

        long count = userRoleRepository.count();

        if (count == 0) {
            roles = Arrays.asList(
                    new UserRole("ROLE_ADMINISTRATOR", "ADMINISTRATIVE"),
                    new UserRole("ROLE_ACCOUNTANT", "BUSINESS"),
                    new UserRole("ROLE_AUDITOR", "BUSINESS"),
                    new UserRole("ROLE_USER", "BUSINESS")
            );
            roles = (ArrayList<UserRole>) userRoleRepository.saveAll(roles);
        } else {
            roles = (ArrayList<UserRole>) userRoleRepository.findAll();
        }
        roles.sort(UserRole::compareTo);
    }

    public UserRole instanceOf(String role) throws RoleNotFoundException {
        return switch (role) {
            case "ADMINISTRATOR" -> roles.get(0);
            case "ACCOUNTANT" -> roles.get(1);
            case "AUDITOR" -> roles.get(2);
            case "USER" -> roles.get(3);
            default -> throw new RoleNotFoundException();
        };
    }
}
