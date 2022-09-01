package account.business.entity;

import account.security.entity.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter @Setter
public class Salary implements Comparable<Salary> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalDate period;
    private long salary;

    @ManyToOne()
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Override
    public int compareTo(Salary o) {
        return period.compareTo(o.getPeriod());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salary salary = (Salary) o;
        return period.equals(salary.period) && user.equals(salary.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period, user);
    }
}
