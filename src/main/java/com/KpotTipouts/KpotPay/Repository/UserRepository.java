package com.KpotTipouts.KpotPay.Repository;

import com.KpotTipouts.KpotPay.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
