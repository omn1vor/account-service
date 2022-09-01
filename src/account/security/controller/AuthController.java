package account.security.controller;

import account.security.dto.PasswordChangeRequest;
import account.security.dto.UserDto;
import account.security.entity.User;
import account.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public UserDto signup(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PostMapping("/changepass")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody PasswordChangeRequest request,
                                                              @AuthenticationPrincipal User user) {
        userService.changePassword(user, request.getNewPassword());
        return ResponseEntity.ok(Map.of(
                "email", user.getUsername(),
                "status", "The password has been updated successfully"
        ));
    }
}
