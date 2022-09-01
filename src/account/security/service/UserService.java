package account.security.service;

import account.security.dto.RoleChangeRequest;
import account.security.dto.UserDto;
import account.security.entity.Action;
import account.security.entity.RoleChangeOperation;
import account.security.entity.User;
import account.security.entity.UserGroup;
import account.security.Mappers;
import account.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserService implements UserDetailsService {

    private final int MAX_FAILED_ATTEMPTS = 5;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private AuditService auditService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
    }

    public UserDto addUser(UserDto userDto) {
        if (userRepo.existsByUsernameIgnoreCase(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        authService.ensurePasswordIsOK(userDto.getPassword());

        userDto.setPassword(authService.encodePassword(userDto.getPassword()));
        User user = Mappers.fromUserDto(userDto);
        if (userRepo.count() == 0) {
            user.addGroup(userGroupService.getAdministrator());
        } else {
            user.addGroup(userGroupService.getUser());
        }

        userRepo.save(user);
        auditService.addEvent(Action.CREATE_USER, user.getUsername());
        return Mappers.fromUser(user);
    }

    public User getUserByName(String username) {
        return userRepo.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
    }

    public void changePassword(User user, String newPassword) {
        if (authService.passwordIsTheSame(user.getPassword(), newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
        authService.ensurePasswordIsOK(newPassword);

        user.setPassword(authService.encodePassword(newPassword));
        userRepo.save(user);
        auditService.addEvent(Action.CHANGE_PASSWORD, user.getUsername());
    }

    public List<UserDto> getUsers() {
        return userRepo.findAll().stream()
                .map(Mappers::fromUser)
                .sorted(Comparator.comparing(UserDto::getId))
                .toList();
    }

    public void deleteUser(String email) {
        User user = getUserByName(email);
        userRepo.delete(user);
        auditService.addEvent(Action.DELETE_USER, email);
    }

    public UserDto changeUserRole(RoleChangeRequest roleChangeRequest) {
        User user = getUserByName(roleChangeRequest.getUser());
        UserGroup userGroup = userGroupService.getGroupByCode("ROLE_" + roleChangeRequest.getRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        if (roleChangeRequest.getOperation() == RoleChangeOperation.GRANT) {
            addRole(user, userGroup);
        } else {
            removeRole(user, userGroup);
        }

        userRepo.save(user);
        return Mappers.fromUser(user);
    }

    private void addRole(User user, UserGroup userGroup) {
        user.addGroup(userGroup);
        auditService.addEvent(Action.GRANT_ROLE,
                String.format("Grant role %s to %s", userGroup.getName(), user.getUsername()));
    }

    private void removeRole(User user, UserGroup userGroup) {
        if (userGroup.equals(userGroupService.getAdministrator())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        user.removeGroup(userGroup);
        auditService.addEvent(Action.REMOVE_ROLE,
                String.format("Remove role %s from %s", userGroup.getName(), user.getUsername()));
    }

    public boolean registerFailedLoginAttempt(String email) {
        boolean needToLock = false;
        Optional<User> foundUser = userRepo.findByUsernameIgnoreCase(email);
        if (foundUser.isEmpty()) {
            return needToLock;
        }
        User user = foundUser.get();
        if (user.addFailedLoginAttempt() >= MAX_FAILED_ATTEMPTS) {
            user.setLocked(true);
            needToLock = true;
        }
        userRepo.save(user);
        return needToLock;
    }

    public void resetFailedLoginAttempts(User user) {
        userRepo.resetFailedLoginAttempts(user.getId());
    }

    public void lockUser(String email) {
        User user = getUserByName(email);
        lockUser(user);
    }

    public void lockUser(User user) {
        if (user.isAdministrativeAccount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can't lock the ADMINISTRATOR!");
        } else if (user.isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User is already locked!");
        }
        user.setLocked(true);
        auditService.addEvent(Action.LOCK_USER, "Lock user " + user.getUsername());
    }

    public void unlockUser(String email) {
        User user = getUserByName(email);
        if (!user.isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User is already unlocked!");
        }
        user.setLocked(false);
        auditService.addEvent(Action.UNLOCK_USER, "Unlock user " + email);
    }
}