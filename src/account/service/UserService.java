package account.service;

import account.dto.RoleChangeRequest;
import account.dto.UserDto;
import account.entity.RoleChangeOperation;
import account.entity.User;
import account.entity.UserGroup;
import account.mapper.Mappers;
import account.repository.UserRepository;
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


@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserGroupService userGroupService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
    }

    synchronized public UserDto addUser(UserDto userDto) {
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
        return Mappers.fromUser(user);
    }

    public User getUserByName(String username) {
        return userRepo.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
    }

    public UserDto getUserDtoByName(String username) {
        return Mappers.fromUser(getUserByName(username));
    }

    synchronized public void changePassword(User user, String newPassword) {
        if (authService.passwordIsTheSame(user.getPassword(), newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
        authService.ensurePasswordIsOK(newPassword);

        user.setPassword(authService.encodePassword(newPassword));
        userRepo.save(user);
    }

    public List<UserDto> getUsers() {
        return userRepo.findAll().stream()
                .map(Mappers::fromUser)
                .sorted(Comparator.comparing(UserDto::getId))
                .toList();
    }

    public void deleteUser(String email) {
        User user = userRepo.findByUsernameIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        userRepo.delete(user);
    }

    public UserDto changeUserRole(RoleChangeRequest roleChangeRequest) {
        User user = userRepo.findByUsernameIgnoreCase(roleChangeRequest.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        UserGroup userGroup = userGroupService.getGroupByCode("ROLE_" + roleChangeRequest.getRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        if (roleChangeRequest.getOperation() == RoleChangeOperation.GRANT) {
            user.addGroup(userGroup);
        } else {
            if (userGroup.equals(userGroupService.getAdministrator())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            }
            user.removeGroup(userGroup);
        }
        userRepo.save(user);
        return Mappers.fromUser(user);
    }

}