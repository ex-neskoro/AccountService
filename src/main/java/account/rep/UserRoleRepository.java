package account.rep;

import account.model.user.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

}
