package account.repository;

import account.entity.Salary;
import account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Optional<Salary> findByUserAndPeriod(User user, LocalDate period);
}
