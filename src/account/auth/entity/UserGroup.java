package account.auth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity()
@Table(name = "user_groups")
@Getter @Setter
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    private String name;
    private boolean administrative;

    @ManyToMany(mappedBy = "userGroups")
    private Set<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGroup userGroup = (UserGroup) o;
        return code.equals(userGroup.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
