package account.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRole implements GrantedAuthority, Comparable<UserRole> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Long id;

    @JsonValue
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "role_group")
    private String group;

    public UserRole(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public UserRole() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public int compareTo(UserRole anotherRole) {
        return (int) (this.id - anotherRole.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRole userRole = (UserRole) o;

        return id.equals(userRole.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
