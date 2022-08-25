package account.controller;

import account.dto.SalaryRequest;
import account.dto.StatusResponse;
import account.service.SalaryService;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class BusinessController {

    @Autowired
    SalaryService salaryService;

    @PostMapping("/api/acct/payments")
    public StatusResponse addPayments(@RequestBody List<SalaryRequest> body) {
        salaryService.addSalary(body);
        return new StatusResponse("Added successfully!");
    }

    @PutMapping("api/acct/payments")
    public StatusResponse updatePayment(@RequestBody SalaryRequest body) {
        salaryService.updateSalary(body);
        return new StatusResponse("Updated successfully!");
    }

    @GetMapping("api/empl/payment")
    public ResponseEntity<Object> getPayment(@RequestParam @Nullable String period, Principal principal) {
        String email = principal.getName();
        Object result = period == null
                ? salaryService.getSalary(email)
                : salaryService.getSalary(email, period);
        return new ResponseEntity<>(result == null ? Map.of() : result, HttpStatus.OK);
    }
}
