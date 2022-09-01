package account.security.repository;

import account.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    @Modifying
    @Query("UPDATE User AS u SET u.failedLoginAttempts = 0 WHERE u.id = ?1")
    void resetFailedLoginAttempts(Long id);
}