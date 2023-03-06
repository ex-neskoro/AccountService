package account.rep;

import account.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByName(String name);

    Optional<User> findUserByEmail(String email);

    @Query("select u from User u order by u.id")
    List<User> findAllOrderByIdAsc();

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String s);

}
