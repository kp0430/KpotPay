package com.KpotTipouts.KpotPay.Repository;

import com.KpotTipouts.KpotPay.Entity.Shift;
import com.KpotTipouts.KpotPay.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByUser(User user);
    List<Shift> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

}
