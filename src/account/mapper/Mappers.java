package account.mapper;

import account.dto.SalaryRequest;
import account.dto.SalaryResponse;
import account.dto.UserDto;
import account.entity.Salary;
import account.entity.User;
import account.entity.UserGroup;
import account.util.DateUtils;
import account.util.NumberUtils;

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

    public static SalaryResponse fromSalary(Salary salary, User user) {
        SalaryResponse salaryResponse = new SalaryResponse();
        salaryResponse.setName(user.getName());
        salaryResponse.setLastname(user.getLastName());
        salaryResponse.setPeriod(DateUtils.formatDateAsMonthYear(salary.getPeriod()));
        salaryResponse.setSalary(NumberUtils.formatAsMoney(salary.getSalary()));
        return salaryResponse;
    }

    public static Salary fromSalaryRequest(SalaryRequest salaryRequest, User user) {
        Salary salary = new Salary();
        salary.setUser(user);
        salary.setPeriod(DateUtils.parseDateFromMonthYear(salaryRequest.getPeriod()));
        salary.setSalary(salaryRequest.getSalary());
        return salary;
    }
}
