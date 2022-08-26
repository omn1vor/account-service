package account.controller;

import account.dto.SalaryRequest;
import account.dto.StatusResponse;
import account.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class BusinessController {

    @Autowired
    SalaryService salaryService;

    @PostMapping("/api/acct/payments")
    public StatusResponse addPayments(@RequestBody List<@Valid SalaryRequest> body, Principal principal) {
        salaryService.ensureThisIsNotAdministrativeUser(principal.getName());
        salaryService.addSalary(body);
        return new StatusResponse("Added successfully!");
    }

    @PutMapping("api/acct/payments")
    public StatusResponse updatePayment(@RequestBody @Valid SalaryRequest body, Principal principal) {
        salaryService.ensureThisIsNotAdministrativeUser(principal.getName());
        salaryService.updateSalary(body);
        return new StatusResponse("Updated successfully!");
    }

    @GetMapping("api/empl/payment")
    public ResponseEntity<Object> getPayment(@RequestParam @Nullable String period, Principal principal) {
        String email = principal.getName();
        salaryService.ensureThisIsNotAdministrativeUser(email);
        Object result = period == null
                ? salaryService.getSalary(email)
                : salaryService.getSalary(email, period);
        return new ResponseEntity<>(result == null ? Map.of() : result, HttpStatus.OK);
    }
}
