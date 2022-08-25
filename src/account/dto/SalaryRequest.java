package account.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Getter @Setter
public class SalaryRequest {
    @NotBlank
    private String employee; // email as an ID
    @NotBlank
    private String period;
    @Min(1)
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
