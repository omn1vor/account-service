package account.security.controller;

import account.security.dto.RoleChangeRequest;
import account.security.dto.UserDto;
import account.security.dto.UserLockUnlockRequest;
import account.security.entity.LockUnlock;
import account.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    UserService userService;
    @GetMapping("/user")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String email, Principal principal) {
        if (principal.getName().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userService.deleteUser(email);
        return ResponseEntity.ok(Map.of(
                "user", email,
                "status", "Deleted successfully!"
        ));
    }

    @PutMapping("/user/role")
    public UserDto changeRole(@Valid @RequestBody RoleChangeRequest body) {
        return userService.changeUserRole(body);
    }

    @PutMapping("/user/access")
    public ResponseEntity<Map<String, String>> lockUnlockUser(@Valid @RequestBody UserLockUnlockRequest body) {
        String result;
        String email = body.getUser();
        if (body.getOperation() == LockUnlock.LOCK) {
            userService.lockUser(email);
            result = "locked";
        } else {
            userService.unlockUser(email);
            result = "unlocked";
        }
        return ResponseEntity.ok(Map.of(
                "status", String.format("User %s %s!", email.toLowerCase(), result)
        ));

    }
}
