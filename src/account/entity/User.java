package account.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {

    @Id @GeneratedValue
    private Long id;
    @JsonProperty("email") @NonNull
    private String username;
    private String password;
    private String name;

    @JsonProperty("lastname")
    private String lastName;

    @OneToMany(mappedBy = "user", cascade=CascadeType.ALL)
    private List<Salary> salary = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_group_members",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<UserGroup> userGroups = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userGroups.stream()
                .map(i -> new SimpleGrantedAuthority(i.getCode()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public void addGroup(UserGroup userGroup) {
        if (!userGroups.isEmpty() && userGroup.isAdministrative() != isAdministrativeAccount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user cannot combine administrative and business roles!");
        }
        userGroups.add(userGroup);
    }

    public void removeGroup(UserGroup userGroup) {
        if (!userGroups.contains(userGroup)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user does not have a role!");
        }
        if (userGroups.size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user must have at least one role!");
        }
        userGroups.remove(userGroup);
    }

    public boolean isAdministrativeAccount() {
        return userGroups.stream()
                .anyMatch(UserGroup::isAdministrative);
    }
}