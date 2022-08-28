package account.business;

import account.business.dto.SalaryRequest;
import account.business.dto.SalaryResponse;
import account.business.entity.Salary;
import account.auth.entity.User;
import account.util.DateUtils;
import account.util.NumberUtils;

public class Mappers {
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
