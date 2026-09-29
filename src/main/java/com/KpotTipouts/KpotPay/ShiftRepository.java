package com.KpotTipouts.KpotPay;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByUser(User user);
    List<Shift> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

}
