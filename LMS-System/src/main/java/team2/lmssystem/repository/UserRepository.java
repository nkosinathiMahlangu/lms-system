package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
