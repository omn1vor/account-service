package account.security;

import account.security.dto.UserDto;
import account.security.entity.User;
import account.security.entity.UserGroup;

public class Mappers {

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getUsername());
        userDto.setRoles(user.getUserGroups().stream()
                .map(UserGroup::getCode)
                .sorted()
                .toList()
        );
        return userDto;
    }

    public static User fromUserDto(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getEmail().toLowerCase());
        user.setPassword(userDto.getPassword());
        user.setName(userDto.getName());
        user.setLastName(userDto.getLastName());
        return user;
    }
}
