package account.auth.controller;

import account.auth.dto.PasswordChangeRequest;
import account.auth.dto.PasswordChangeResponse;
import account.auth.dto.UserDto;
import account.auth.entity.User;
import account.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    public PasswordChangeResponse changePassword(@RequestBody PasswordChangeRequest request,
                                                 @AuthenticationPrincipal User user) {
        userService.changePassword(user, request.getNewPassword());
        return new PasswordChangeResponse(user.getUsername(), "The password has been updated successfully");
    }
}
