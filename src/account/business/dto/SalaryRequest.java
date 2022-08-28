package account.business.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Getter @Setter
public class SalaryRequest {
    @NotBlank(message = "email shouldn't be empty")
    private String employee; // email as an ID
    @Pattern(regexp = "\\d{1,2}-\\d{4}", message = "period should be of 'MM-yyyy' format")
    private String period;
    @Min(value = 1, message = "salary should be greater than 0")
    private Long salary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryRequest salaryRequest = (SalaryRequest) o;
        return employee.equals(salaryRequest.employee) && period.equals(salaryRequest.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }
}
