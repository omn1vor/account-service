package account.auth.controller;

import account.auth.dto.RoleChangeRequest;
import account.auth.dto.UserDeletionResponse;
import account.auth.dto.UserDto;
import account.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

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
    public UserDeletionResponse deleteUser(@PathVariable String email, Principal principal) {
        if (principal.getName().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userService.deleteUser(email);
        return new UserDeletionResponse(email, "Deleted successfully!");
    }

    @PutMapping("/user/role")
    public UserDto changeRole(@Valid @RequestBody RoleChangeRequest body) {
        return userService.changeUserRole(body);
    }
}