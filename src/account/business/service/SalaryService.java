package account.business.service;

import account.business.dto.SalaryRequest;
import account.business.dto.SalaryResponse;
import account.business.entity.Salary;
import account.security.entity.User;
import account.business.Mappers;
import account.business.repository.SalaryRepository;
import account.security.repository.UserRepository;
import account.security.service.UserService;
import account.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SalaryService {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepo;

    @Autowired
    SalaryRepository salaryRepo;


    public void addSalary(List<SalaryRequest> list) {
        if (!salaryListIsUnique(list)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee-period pairs must be unique!");
        }

        Map<String, List<SalaryRequest>> salaryByEmails = list.stream()
                .collect(Collectors.groupingBy(SalaryRequest::getEmployee, Collectors.toList()));
        for (var email : salaryByEmails.keySet()) {
            User user = userService.getUserByName(email);
            for (var salaryRequest : salaryByEmails.get(email)) {
                LocalDate period = DateUtils.parseDateFromMonthYear(salaryRequest.getPeriod());
                Salary salary = salaryRepo.findByUserAndPeriod(user, period)
                        .orElse(Mappers.fromSalaryRequest(salaryRequest, user));
                salaryRepo.save(salary);
            }
        }
    }

    public void updateSalary(SalaryRequest salaryRequest) {
        User user = userService.getUserByName(salaryRequest.getEmployee());
        LocalDate period = DateUtils.parseDateFromMonthYear(salaryRequest.getPeriod());

        Optional<Salary> existing = user.getSalary().stream()
                .filter(i -> i.getPeriod().equals(period))
                .findAny();
        Salary salary;
        if (existing.isPresent()) {
            salary = existing.get();
            salary.setSalary(salaryRequest.getSalary());
        } else {
            salary = new Salary();
            salary.setPeriod(period);
            salary.setSalary(salaryRequest.getSalary());
            user.getSalary().add(salary);
        }
        userRepo.save(user);
    }

    public List<SalaryResponse> getSalary(String email) {
        User user = userService.getUserByName(email);
        return user.getSalary().stream()
                .sorted(Comparator.reverseOrder())
                .map(i -> Mappers.fromSalary(i, user))
                .toList();
    }

    public SalaryResponse getSalary(String email, String rawDate) {
        User user = userService.getUserByName(email);
        LocalDate period = DateUtils.parseDateFromMonthYear(rawDate);

        Optional<Salary> salary = user.getSalary().stream()
                .filter(i -> i.getPeriod().equals(period))
                .findAny();
        if (salary.isEmpty()) {
            return null;
        }
        return Mappers.fromSalary(salary.get(), user);
    }

    public void ensureThisIsNotAdministrativeUser(String email) {
        User user = userService.getUserByName(email);
        if (user.isAdministrativeAccount()) {
            throw new AccessDeniedException("Trying to access business endpoint with administrative user");
        }
    }

    private boolean salaryListIsUnique(List<SalaryRequest> list) {
        return list.stream().allMatch(new HashSet<>()::add);
    }
}
